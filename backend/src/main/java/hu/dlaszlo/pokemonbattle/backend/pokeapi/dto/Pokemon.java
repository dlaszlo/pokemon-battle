package hu.dlaszlo.pokemonbattle.backend.pokeapi.dto;

/**
 * Represents the Pokemon data needed for battle simulation and frontend display.
 *
 * @param id ID of the Pokemon
 * @param name The name of the Pokemon
 * @param types The Pokemon's types, concatenated into a single, comma-separated string (e.g., "fire, flying")
 * @param imageUrl Pokemon's default image URL
 */
public record Pokemon(
        Integer id,
        String name,
        String types,
        String imageUrl
) {
}

