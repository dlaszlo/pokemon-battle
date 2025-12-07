package hu.dlaszlo.pokemonbattle.backend.pokeapi;

import lombok.Getter;

/**
 * Exception that occurs while communicating with the external pokeapi service.
 * This class can optionally hold the HTTP status code and the raw HTTP response body.
 */
@Getter
public class PokeApiException extends RuntimeException {

    private final Integer httpStatusCode;
    private final String httpResponseBody;

    public PokeApiException(String message) {
        this(message, null, null, null);
    }

    public PokeApiException(String message, Throwable cause) {
        this(message, cause, null, null);
    }

    public PokeApiException(String message, Throwable cause, String responseMessage, Integer httpStatusCode) {
        super(message, cause);
        this.httpResponseBody = responseMessage;
        this.httpStatusCode = httpStatusCode;
    }

}
