package hu.dlaszlo.pokemonbattle.backend.service;

import hu.dlaszlo.pokemonbattle.backend.pokeapi.PokeApi;
import hu.dlaszlo.pokemonbattle.backend.pokeapi.dto.Pokemon;
import hu.dlaszlo.pokemonbattle.backend.service.dto.Battle;
import hu.dlaszlo.pokemonbattle.backend.service.dto.BattleStatus;
import hu.dlaszlo.pokemonbattle.backend.service.dto.PokemonEntity;
import hu.dlaszlo.pokemonbattle.backend.service.dto.WinnerSide;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InMemoryBattleServiceImpl implements BattleService {

    private static final int MAX_RETURNED_BATTLES = 20;
    private static final int MIN_POWER = 1;
    private static final int MAX_POWER = 20;

    private final PokeApi pokeApi;
    private final List<Battle> battles = new CopyOnWriteArrayList<>();
    private final Map<Long, Battle> battleById = new ConcurrentHashMap<>();
    private final AtomicLong battleIdGenerator = new AtomicLong(0);

    @Autowired
    public InMemoryBattleServiceImpl(PokeApi pokeApi) {
        this.pokeApi = pokeApi;
    }

    @Override
    public Battle createRandomBattle() {
        long startTime = System.nanoTime();
        try {
            log.info("createRandomBattle() started");

            var allNames = pokeApi.listPokemonNames();
            if (allNames == null || allNames.size() < 2) {
                throw new IllegalStateException("Not enough Pokemon available to create a battle.");
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();
            int firstIndex = random.nextInt(allNames.size());
            int secondIndex;
            do {
                secondIndex = random.nextInt(allNames.size());
            } while (secondIndex == firstIndex);

            String firstName = allNames.get(firstIndex);
            String secondName = allNames.get(secondIndex);

            Pokemon firstPokemon = pokeApi.getPokemon(firstName);
            Pokemon secondPokemon = pokeApi.getPokemon(secondName);

            PokemonEntity first = toPokemonEntity(firstPokemon, randomPower());
            PokemonEntity second = toPokemonEntity(secondPokemon, randomPower());

            Battle battle = new Battle(
                    battleIdGenerator.getAndIncrement(),
                    BattleStatus.PENDING,
                    first,
                    second,
                    null,
                    Instant.now(),
                    null
            );

            battleById.put(battle.id(), battle);

            return battle;

        } finally {
            long endTime = System.nanoTime();
            log.info("createRandomBattle() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }
    }

    @Override
    public Battle simulateBattle(Long battleId) {
        long startTime = System.nanoTime();
        try {
            log.info("simulateBattle() started");
            Objects.requireNonNull(battleId, "battleId must not be null");

            Battle existing = battleById.get(battleId);
            if (existing == null) {
                throw new BattleNotFoundException(battleId);
            }

            if (existing.status() == BattleStatus.FINISHED) {
                return existing;
            }

            WinnerSide winnerSide = decideWinner(existing.first(), existing.second());

            Battle finishedBattle = new Battle(
                    existing.id(),
                    BattleStatus.FINISHED,
                    existing.first(),
                    existing.second(),
                    winnerSide,
                    existing.createdAt(),
                    Instant.now()
            );

            battleById.put(finishedBattle.id(), finishedBattle);

            battles.add(0, finishedBattle);

            return finishedBattle;
        } finally {
            long endTime = System.nanoTime();
            log.info("simulateBattle() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }
    }

    @Override
    public List<Battle> searchBattles(String queryParam) {
        long startTime = System.nanoTime();
        try {
            log.info("searchBattles() started");
            String query = StringUtils.trimToNull(queryParam);
            return battles.stream()
                    .filter(battle -> matchesQuery(battle, query))
                    .limit(MAX_RETURNED_BATTLES)
                    .collect(Collectors.toList());
        } finally {
            long endTime = System.nanoTime();
            log.info("searchBattles() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }
    }

    private WinnerSide decideWinner(PokemonEntity first, PokemonEntity second) {
        return first.power() > second.power() ? WinnerSide.FIRST
                : second.power() > first.power() ? WinnerSide.SECOND
                : WinnerSide.DRAW;
    }

    private PokemonEntity toPokemonEntity(Pokemon pokemon, int power) {
        return new PokemonEntity(
                pokemon.name(),
                pokemon.types(),
                pokemon.imageUrl(),
                power
        );
    }

    private int randomPower() {
        return ThreadLocalRandom.current().nextInt(MIN_POWER, MAX_POWER + 1);
    }

    private boolean matchesQuery(Battle battle, String query) {
        return battle.status() == BattleStatus.FINISHED
                && (query == null
                || Strings.CI.contains(battle.first().name(), query)
                || Strings.CI.contains(battle.second().name(), query));
    }
}
