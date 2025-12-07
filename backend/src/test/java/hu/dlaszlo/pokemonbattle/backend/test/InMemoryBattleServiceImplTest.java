package hu.dlaszlo.pokemonbattle.backend.test;

import hu.dlaszlo.pokemonbattle.backend.pokeapi.PokeApi;
import hu.dlaszlo.pokemonbattle.backend.pokeapi.dto.Pokemon;
import hu.dlaszlo.pokemonbattle.backend.service.BattleNotFoundException;
import hu.dlaszlo.pokemonbattle.backend.service.InMemoryBattleServiceImpl;
import hu.dlaszlo.pokemonbattle.backend.service.dto.Battle;
import hu.dlaszlo.pokemonbattle.backend.service.dto.BattleStatus;
import hu.dlaszlo.pokemonbattle.backend.service.dto.PokemonEntity;
import hu.dlaszlo.pokemonbattle.backend.service.dto.WinnerSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryBattleServiceImplTest {

    @Mock
    private PokeApi pokeApi;

    private InMemoryBattleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InMemoryBattleServiceImpl(pokeApi);
    }

    @Test
    void createRandomBattle_shouldCreatePendingBattleAndStoreItInMap() throws Exception {
        List<String> names = List.of("pikachu", "bulbasaur");
        when(pokeApi.listPokemonNames()).thenReturn(names);

        when(pokeApi.getPokemon("pikachu"))
                .thenReturn(new Pokemon(25, "pikachu", "electric", "url1"));
        when(pokeApi.getPokemon("bulbasaur"))
                .thenReturn(new Pokemon(1, "bulbasaur", "grass", "url2"));

        Battle battle = service.createRandomBattle();

        assertThat(battle.id()).isEqualTo(0L);
        assertThat(battle.status()).isEqualTo(BattleStatus.PENDING);
        assertThat(battle.createdAt()).isNotNull();
        assertThat(battle.finishedAt()).isNull();

        assertThat(battle.first().name()).isIn(names);
        assertThat(battle.second().name()).isIn(names);
        assertThat(battle.first().name()).isNotEqualTo(battle.second().name());

        assertThat(battle.first().power()).isBetween(1, 20);
        assertThat(battle.second().power()).isBetween(1, 20);

        Map<Long, Battle> map = internalBattleMap();
        assertThat(map).containsKey(battle.id());
        assertThat(map.get(battle.id())).isEqualTo(battle);

        List<Battle> history = internalHistoryList();
        assertThat(history).isEmpty(); // Még csak pending, nincs a historyban
    }

    @Test
    void createRandomBattle_shouldThrowIfNotEnoughPokemon() {
        when(pokeApi.listPokemonNames()).thenReturn(List.of("onlyOne"));

        assertThatThrownBy(() -> service.createRandomBattle())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not enough Pokemon");
    }

    @Test
    void simulateBattle_shouldFinishPendingBattle_SetWinner_AndAddToHistory() throws Exception {
        long battleId = 123L;
        Battle pending = new Battle(
                battleId,
                BattleStatus.PENDING,
                new PokemonEntity("pikachu", "electric", "u1", 15),
                new PokemonEntity("bulbasaur", "grass", "u2", 5),
                null,
                Instant.now(),
                null
        );

        internalBattleMap().put(battleId, pending);

        Battle finished = service.simulateBattle(battleId);

        assertThat(finished.status()).isEqualTo(BattleStatus.FINISHED);
        assertThat(finished.winnerSide()).isEqualTo(WinnerSide.FIRST); // 15 > 5
        assertThat(finished.finishedAt()).isNotNull();

        List<Battle> history = internalHistoryList();
        assertThat(history).hasSize(1);
        assertThat(history.get(0)).isEqualTo(finished);
    }

    @Test
    void simulateBattle_shouldReturnExisting_WhenAlreadyFinished() throws Exception {

        long battleId = 10L;
        Battle finishedBattle = new Battle(
                battleId,
                BattleStatus.FINISHED,
                new PokemonEntity("p1", "t1", "u1", 5),
                new PokemonEntity("p2", "t2", "u2", 7),
                WinnerSide.SECOND,
                Instant.now(),
                Instant.now()
        );

        internalBattleMap().put(battleId, finishedBattle);

        internalHistoryList().add(finishedBattle);
        int originalHistorySize = internalHistoryList().size();

        Battle result = service.simulateBattle(battleId);

        assertThat(result).isSameAs(finishedBattle);
        assertThat(internalHistoryList()).hasSize(originalHistorySize);
    }

    @Test
    void simulateBattle_shouldThrow_WhenBattleIdIsNull() {
        assertThatThrownBy(() -> service.simulateBattle(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("battleId");
    }

    @Test
    void simulateBattle_shouldThrow_WhenBattleNotFoundInMap() {
        assertThatThrownBy(() -> service.simulateBattle(999L))
                .isInstanceOf(BattleNotFoundException.class);
    }

    @Test
    void searchBattles_shouldReturnAll_WhenQueryIsNull() throws Exception {
        List<Battle> history = internalHistoryList();
        history.addAll(sampleFinishedBattles());

        List<Battle> result = service.searchBattles(null);

        assertThat(result).hasSize(2); // A sampleBattles mérete
        assertThat(result).containsExactlyElementsOf(history);
    }

    @Test
    void searchBattles_shouldFilterByName_CaseInsensitive() throws Exception {
        List<Battle> history = internalHistoryList();
        history.addAll(sampleFinishedBattles());

        List<Battle> result = service.searchBattles("squir");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).second().name()).isEqualTo("Squirtle");
    }

    @Test
    void searchBattles_shouldLimitToMaxReturnedBattles() throws Exception {

        List<Battle> history = internalHistoryList();
        for (long i = 0; i < 25; i++) {
            history.add(new Battle(
                    i,
                    BattleStatus.FINISHED,
                    new PokemonEntity("p" + i, "t", "u", 1),
                    new PokemonEntity("q" + i, "t", "u", 1),
                    WinnerSide.FIRST,
                    Instant.now(),
                    Instant.now()
            ));
        }

        List<Battle> result = service.searchBattles(null);

        assertThat(result).hasSize(20);
    }

    @SuppressWarnings("unchecked")
    private List<Battle> internalHistoryList() throws Exception {
        Field field = InMemoryBattleServiceImpl.class.getDeclaredField("battles");
        field.setAccessible(true);
        return (List<Battle>) field.get(service);
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Battle> internalBattleMap() throws Exception {
        Field field = InMemoryBattleServiceImpl.class.getDeclaredField("battleById");
        field.setAccessible(true);
        return (Map<Long, Battle>) field.get(service);
    }

    private List<Battle> sampleFinishedBattles() {
        List<Battle> list = new ArrayList<>();
        list.add(new Battle(
                1L,
                BattleStatus.FINISHED,
                new PokemonEntity("Pikachu", "electric", "u1", 10),
                new PokemonEntity("Bulbasaur", "grass", "u2", 12),
                WinnerSide.SECOND,
                Instant.now(),
                Instant.now()
        ));
        list.add(new Battle(
                2L,
                BattleStatus.FINISHED,
                new PokemonEntity("Charmander", "fire", "u3", 15),
                new PokemonEntity("Squirtle", "water", "u4", 8),
                WinnerSide.FIRST,
                Instant.now(),
                Instant.now()
        ));
        return list;
    }
}