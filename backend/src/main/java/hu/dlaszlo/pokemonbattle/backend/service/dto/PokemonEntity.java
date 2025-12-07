package hu.dlaszlo.pokemonbattle.backend.service.dto;

/**
 * Represents the essential data for a Pokemon participating in a battle
 *
 * @param name The name of the Pokemon
 * @param types The Pokemon's type(s), concatenated into a single, comma-separated string (e.g., "fire, flying")
 * @param imageUrl The URL of the Pokemon's default image sprite
 * @param power The randomly generated combat power value (1-20) used to determine the winner
 */
public record PokemonEntity(
        String name,
        String types,
        String imageUrl,
        int power
) {
}