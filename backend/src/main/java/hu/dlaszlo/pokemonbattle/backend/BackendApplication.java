package hu.dlaszlo.pokemonbattle.backend;

import hu.dlaszlo.pokemonbattle.backend.restclient.PokeApiRestClientImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    private PokeApiRestClientImpl pokeAPI;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
