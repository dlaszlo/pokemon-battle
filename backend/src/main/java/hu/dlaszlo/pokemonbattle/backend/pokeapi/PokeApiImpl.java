package hu.dlaszlo.pokemonbattle.backend.pokeapi;

import hu.dlaszlo.pokemonbattle.backend.pokeapi.dto.Pokemon;
import hu.dlaszlo.pokemonbattle.backend.restclient.PokeApiRestClient;
import hu.dlaszlo.pokemonbattle.backend.restclient.dto.*;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PokeApiImpl implements PokeApi {

    private final PokeApiRestClient pokeApiRestClient;

    private final String fallbackSprite;

    @Autowired
    public PokeApiImpl(@Value("${pokeapi.fallback_sprite}") String fallbackSprite,
                       PokeApiRestClient pokeApiRestClient) {
        this.fallbackSprite = fallbackSprite;
        this.pokeApiRestClient = pokeApiRestClient;
    }


    @Override
    public List<String> listPokemonNames() {
        long startTime = System.nanoTime();
        try {
            log.info("listPokemonNames() started");

            PokeApiNameList response = pokeApiRestClient.getPokeApiNameList();

            return Optional.ofNullable(response)
                    .map(PokeApiNameList::results)
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(PokeApiName::name)
                    .toList();

        } catch (RestClientResponseException e) {
            pokeApiRestClient.clearAllCaches();
            log.error("listPokemonNames() failed", e);
            throw new PokeApiException("Error occurred during PokeAPI call", e,
                    e.getResponseBodyAsString(), e.getStatusCode().value());
        } catch (Exception e) {
            pokeApiRestClient.clearAllCaches();
            log.error("listPokemonNames() failed", e);
            throw new PokeApiException("Error occurred during PokeAPI call", e);
        } finally {
            long endTime = System.nanoTime();
            log.info("listPokemonNames() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }
    }

    @Override
    public Pokemon getPokemon(String name) {

        long startTime = System.nanoTime();
        try {
            log.info("getPokemon() started");

            PokeApiDetail response = pokeApiRestClient.getPokeApiDetail(name);

            Pokemon pokemon = mapPokemon(
                    Objects.requireNonNull(response, "response must not be null"));
            validatePokemon(pokemon);

            return pokemon;
        } catch (RestClientResponseException e) {
            pokeApiRestClient.clearAllCaches();
            log.error("getPokemon() failed", e);
            throw new PokeApiException("Error occurred during PokeAPI call", e,
                    e.getResponseBodyAsString(), e.getStatusCode().value());
        } catch (Exception e) {
            pokeApiRestClient.clearAllCaches();
            log.error("getPokemon() failed", e);
            throw new PokeApiException("Error occurred during PokeAPI call", e);
        } finally {
            long endTime = System.nanoTime();
            log.info("getPokemon() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }

    }

    private Pokemon mapPokemon(PokeApiDetail response) {

        String types = null;
        if (response.types() != null) {
            types = response.types().stream()
                    .map(PokeApiTypeSlot::type).map(PokeApiType::name).collect(Collectors.joining(", "));
        }

        String frontDefault = null;
        if (response.sprites() != null
                && StringUtils.isNotBlank(response.sprites().frontDefault())) {
            frontDefault = response.sprites().frontDefault();
        } else {
            frontDefault = fallbackSprite;
        }
        return new Pokemon(
                response.id(),
                response.name(),
                types,
                frontDefault
        );
    }

    private static void validatePokemon(Pokemon pokemon) {
        if (pokemon.id() == null
                || StringUtils.isBlank(pokemon.name())
                || StringUtils.isBlank(pokemon.types())) {
            throw new IllegalArgumentException("Invalid pokemon: " + pokemon);
        }
    }

}
