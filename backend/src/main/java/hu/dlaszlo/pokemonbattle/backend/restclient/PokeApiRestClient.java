package hu.dlaszlo.pokemonbattle.backend.restclient;

import hu.dlaszlo.pokemonbattle.backend.restclient.dto.PokeApiDetail;
import hu.dlaszlo.pokemonbattle.backend.restclient.dto.PokeApiNameList;

/**
 * REST Client for communicating with the PokeAPI (https://pokeapi.co/).
 */
public interface PokeApiRestClient {

    /**
     * @return A {@link PokeApiNameList} containing a list of Pokemon names
     */
    PokeApiNameList getPokeApiNameList();

    /**
     * @param name The name of the Pokemon to retrieve
     * @return A {@link PokeApiDetail} object containing the Pokemon's data
     */
    PokeApiDetail getPokeApiDetail(String name);

    /**
     * Clears all caches associated with this REST client
     */
    void clearAllCaches();

}
