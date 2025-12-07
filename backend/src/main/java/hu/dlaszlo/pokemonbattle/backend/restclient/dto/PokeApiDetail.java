package hu.dlaszlo.pokemonbattle.backend.restclient.dto;

import java.util.List;

/**
 * Detailed information returned by the PokeAPI for a specific Pokemon
 *
 * @param id ID of the Pokemon
 * @param name Name of the Pokemon
 * @param types A list of type slots associated with the Pokemon
 * @param sprites The container object holding the image URLs
 */
public record PokeApiDetail(
        Integer id,
        String name,
        List<PokeApiTypeSlot> types,
        PokeApiSprite sprites
) {
}
