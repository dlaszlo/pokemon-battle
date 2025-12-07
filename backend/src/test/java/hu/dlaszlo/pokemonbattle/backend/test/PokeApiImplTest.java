package hu.dlaszlo.pokemonbattle.backend.test;

import hu.dlaszlo.pokemonbattle.backend.pokeapi.PokeApiException;
import hu.dlaszlo.pokemonbattle.backend.pokeapi.PokeApiImpl;
import hu.dlaszlo.pokemonbattle.backend.pokeapi.dto.Pokemon;
import hu.dlaszlo.pokemonbattle.backend.restclient.PokeApiRestClient;
import hu.dlaszlo.pokemonbattle.backend.restclient.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokeApiImplTest {

    @Mock
    private PokeApiRestClient restClient;

    private PokeApiImpl pokeApi;

    @BeforeEach
    void setUp() {
        pokeApi = new PokeApiImpl("http://fallback_sprite", restClient);
    }

    @Test
    void listPokemonNames_shouldReturnMappedNames_whenResponseOk() {
        PokeApiNameList response = new PokeApiNameList(
                List.of(new PokeApiName("pikachu"), new PokeApiName("bulbasaur"))
        );
        when(restClient.getPokeApiNameList()).thenReturn(response);

        List<String> result = pokeApi.listPokemonNames();

        assertThat(result).containsExactly("pikachu", "bulbasaur");
        verify(restClient).getPokeApiNameList();
        verify(restClient, never()).clearAllCaches();
    }

    @Test
    void listPokemonNames_shouldReturnEmptyList_whenResponseIsNull() {
        when(restClient.getPokeApiNameList()).thenReturn(null);

        List<String> result = pokeApi.listPokemonNames();

        assertThat(result).isEmpty();
        verify(restClient).getPokeApiNameList();
        verify(restClient, never()).clearAllCaches();
    }

    @Test
    void listPokemonNames_shouldReturnEmptyList_whenResultsIsNull() {
        PokeApiNameList response = new PokeApiNameList(null);
        when(restClient.getPokeApiNameList()).thenReturn(response);

        List<String> result = pokeApi.listPokemonNames();

        assertThat(result).isEmpty();
        verify(restClient).getPokeApiNameList();
        verify(restClient, never()).clearAllCaches();
    }

    @Test
    void listPokemonNames_shouldWrapRestClientResponseExceptionAndClearCache() {
        RestClientResponseException ex = new RestClientResponseException(
                "error",
                500,
                "Internal Server Error",
                new HttpHeaders(),
                "body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(restClient.getPokeApiNameList()).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.listPokemonNames())
                .isInstanceOf(PokeApiException.class)
                .hasMessageContaining("Error occurred during PokeAPI call")
                .hasCause(ex);

        verify(restClient).getPokeApiNameList();
        verify(restClient).clearAllCaches();
    }

    @Test
    void listPokemonNames_shouldWrapOtherExceptionsAndClearCache() {
        RuntimeException ex = new RuntimeException();
        when(restClient.getPokeApiNameList()).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.listPokemonNames())
                .isInstanceOf(PokeApiException.class)
                .hasMessageContaining("Error occurred during PokeAPI call")
                .hasCause(ex);

        verify(restClient).getPokeApiNameList();
        verify(restClient).clearAllCaches();
    }

    @Test
    void getPokemon_shouldReturnMappedPokemon_whenResponseOk() {
        String name = "charmander";

        PokeApiType fire = new PokeApiType("fire");
        PokeApiTypeSlot typeSlot = new PokeApiTypeSlot(fire);
        PokeApiSprite sprite = new PokeApiSprite("https://img/charmander.png");

        PokeApiDetail detail = new PokeApiDetail(
                4,
                name,
                List.of(typeSlot),
                sprite
        );
        when(restClient.getPokeApiDetail(name)).thenReturn(detail);

        Pokemon result = pokeApi.getPokemon(name);

        assertThat(result.id()).isEqualTo(4);
        assertThat(result.name()).isEqualTo("charmander");
        assertThat(result.types()).isEqualTo("fire");
        assertThat(result.imageUrl()).isEqualTo("https://img/charmander.png");

        verify(restClient).getPokeApiDetail(name);
        verify(restClient, never()).clearAllCaches();
    }

    @Test
    void getPokemon_shouldFailValidationAndWrapIntoPokeApiException_whenMappedPokemonInvalid() {
        String name = "charmander";

        PokeApiType fire = new PokeApiType("fire");
        PokeApiTypeSlot typeSlot = new PokeApiTypeSlot(fire);
        PokeApiDetail detail = new PokeApiDetail(
                4,
                null, // name == null
                List.of(typeSlot),
                null
        );
        when(restClient.getPokeApiDetail(name)).thenReturn(detail);

        assertThatThrownBy(() -> pokeApi.getPokemon(name))
                .isInstanceOf(PokeApiException.class);

        verify(restClient).getPokeApiDetail(name);
        verify(restClient).clearAllCaches();
    }

    @Test
    void getPokemon_shouldWrapRestClientResponseExceptionAndClearCache() {
        String name = "pikachu";

        RestClientResponseException ex = new RestClientResponseException(
                "error",
                404,
                "Not Found",
                new HttpHeaders(),
                "body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(restClient.getPokeApiDetail(name)).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.getPokemon(name))
                .isInstanceOf(PokeApiException.class)
                .hasCause(ex);

        verify(restClient).getPokeApiDetail(name);
        verify(restClient).clearAllCaches();
    }

    @Test
    void getPokemon_shouldWrapOtherExceptionsAndClearCache() {
        String name = "pikachu";

        RuntimeException ex = new RuntimeException();
        when(restClient.getPokeApiDetail(name)).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.getPokemon(name))
                .isInstanceOf(PokeApiException.class)
                .hasCause(ex);

        verify(restClient).getPokeApiDetail(name);
        verify(restClient).clearAllCaches();
    }

    @Test
    void listPokemonNames_shouldClearCacheAndWrapRestClientResponseException() {

        RestClientResponseException ex =
                new RestClientResponseException("error", 500, "ISE", null,
                        "body".getBytes(), null);

        when(restClient.getPokeApiNameList()).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.listPokemonNames())
                .isInstanceOf(PokeApiException.class)
                .hasCause(ex)
                .extracting("httpStatusCode")
                .isEqualTo(500);

        verify(restClient).clearAllCaches();
    }

    @Test
    void listPokemonNames_shouldClearCacheAndWrapGenericException() {

        RuntimeException ex = new RuntimeException();

        when(restClient.getPokeApiNameList()).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.listPokemonNames())
                .isInstanceOf(PokeApiException.class)
                .hasCause(ex);

        verify(restClient).clearAllCaches();
    }

    @Test
    void getPokemon_shouldClearCacheAndWrapRestClientResponseException() {

        String name = "pikachu";
        RestClientResponseException ex =
                new RestClientResponseException("error", 404, "NF", null,
                        "body".getBytes(), null);

        when(restClient.getPokeApiDetail(name)).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.getPokemon(name))
                .isInstanceOf(PokeApiException.class)
                .hasCause(ex)
                .extracting("httpStatusCode")
                .isEqualTo(404);

        verify(restClient).clearAllCaches();
    }

    @Test
    void getPokemon_shouldClearCacheAndWrapGenericException() {

        String name = "pikachu";
        RuntimeException ex = new RuntimeException();

        when(restClient.getPokeApiDetail(name)).thenThrow(ex);

        assertThatThrownBy(() -> pokeApi.getPokemon(name))
                .isInstanceOf(PokeApiException.class)
                .hasCause(ex);

        verify(restClient).clearAllCaches();
    }

    @Test
    void getPokemon_shouldThrowIllegalArgumentExceptionForInvalidPokemon() {

        String name = "pikachu";

        PokeApiDetail detail = new PokeApiDetail(
                null,          // id null -> invalid
                "pikachu",
                List.of(),
                new PokeApiSprite("url")
        );

        when(restClient.getPokeApiDetail(name)).thenReturn(detail);

        assertThatThrownBy(() -> pokeApi.getPokemon(name))
                .isInstanceOf(PokeApiException.class)
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid pokemon");
    }

}
