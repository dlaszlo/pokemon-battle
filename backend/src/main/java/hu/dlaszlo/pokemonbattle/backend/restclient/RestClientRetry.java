package hu.dlaszlo.pokemonbattle.backend.restclient;

import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        includes = {HttpServerErrorException.class, ResourceAccessException.class},
        maxRetries = 4, delayString = "500ms", multiplier = 1.5, maxDelay = 3000
)
public @interface RestClientRetry {
}
