package hu.dlaszlo.pokemonbattle.backend.restclient.dto;

import java.util.List;

/**
 * List of Pokemon names
 *
 * @param results A list of {@link PokeApiName} objects containing the name
 */
public record PokeApiNameList(List<PokeApiName> results) {
}
