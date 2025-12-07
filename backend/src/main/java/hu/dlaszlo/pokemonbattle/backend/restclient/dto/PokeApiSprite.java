package hu.dlaszlo.pokemonbattle.backend.restclient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Container for the Pokemon's image sprite URLs
 *
 * @param frontDefault The URL for the default front sprite
 */
public record PokeApiSprite(@JsonProperty("front_default") String frontDefault) {
}
