package hu.dlaszlo.pokemonbattle.backend.pokeapi;

import hu.dlaszlo.pokemonbattle.backend.pokeapi.dto.Pokemon;

import java.util.List;

/**
 * High-level services for interacting with processed Pokemon data
 * This layer abstracts the raw REST client calls and provides clean data structures
 */
public interface PokeApi {

    /**
     * @return A list of Pokemon names
     */
    List<String> listPokemonNames();

    /**
     * @param name The name of the Pokemon
     * @return Pokemon details
     */
    Pokemon getPokemon(String name);

}
