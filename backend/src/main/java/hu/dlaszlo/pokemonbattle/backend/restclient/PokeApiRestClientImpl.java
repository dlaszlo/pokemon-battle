package hu.dlaszlo.pokemonbattle.backend.restclient;

import hu.dlaszlo.pokemonbattle.backend.BackendConfig;
import hu.dlaszlo.pokemonbattle.backend.restclient.dto.PokeApiDetail;
import hu.dlaszlo.pokemonbattle.backend.restclient.dto.PokeApiNameList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PokeApiRestClientImpl implements PokeApiRestClient {

    private final static int MAX_POKEMON_COUNT = 100_000;
    private final String baseUrl;
    private final RestClient restClient;

    @Autowired
    public PokeApiRestClientImpl(@Value("${pokeapi.baseurl}") String baseUrl, RestClient restClient) {
        this.baseUrl = baseUrl;
        this.restClient = restClient;
    }

    @Override
    @RestClientRetry
    @Cacheable(value = BackendConfig.CACHE_POKEMON_NAMES)
    public PokeApiNameList getPokeApiNameList() {
        long startTime = System.nanoTime();
        try {
            log.info("getPokeApiNameList() started");

            return restClient.get()
                    .uri(baseUrl + "/pokemon?limit={limit}&offset=0", MAX_POKEMON_COUNT)
                    .retrieve()
                    .body(PokeApiNameList.class);

        } finally {
            long endTime = System.nanoTime();
            log.info("getPokeApiNameList() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }
    }

    @Override
    @RestClientRetry
    @Cacheable(value = BackendConfig.CACHE_POKEMON_DETAIL, key = "#name")
    public PokeApiDetail getPokeApiDetail(String name) {

        long startTime = System.nanoTime();
        try {
            log.info("getPokeApiNameList() started");
            return restClient.get()
                    .uri(baseUrl + "/pokemon/{name}",
                            Objects.requireNonNull(name, "name must not be null"))
                    .retrieve()
                    .body(PokeApiDetail.class);

        } finally {
            long endTime = System.nanoTime();
            log.info("getPokeApiNameList() ended in {} ms.", TimeUnit.NANOSECONDS.toMillis(endTime - startTime));
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = BackendConfig.CACHE_POKEMON_NAMES, allEntries = true),
            @CacheEvict(value = BackendConfig.CACHE_POKEMON_DETAIL, allEntries = true)
    })
    public void clearAllCaches() {
        log.info("clear caches");
    }
}
