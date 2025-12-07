package hu.dlaszlo.pokemonbattle.backend.test;

import hu.dlaszlo.pokemonbattle.backend.restclient.PokeApiRestClientImpl;
import hu.dlaszlo.pokemonbattle.backend.restclient.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokeApiRestClientImplTest {

    private static final String BASE_URL = "https://pokeapi.co/api/v2";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    private PokeApiRestClientImpl client;

    @BeforeEach
    void setUp() {
        client = new PokeApiRestClientImpl(BASE_URL, restClient);
    }

    @Test
    void getPokeApiNameList_shouldCallCorrectUrlAndReturnBody() {

        PokeApiNameList expected =
                new PokeApiNameList(List.of(new PokeApiName("pikachu")));

        when(restClient.get()
                .uri(eq(BASE_URL + "/pokemon?limit={limit}&offset=0"), anyInt())
                .retrieve()
                .body(PokeApiNameList.class))
                .thenReturn(expected);

        PokeApiNameList result = client.getPokeApiNameList();

        assertThat(result).isSameAs(expected);

        verify(restClient.get())
                .uri(eq(BASE_URL + "/pokemon?limit={limit}&offset=0"), anyInt());

    }

    @Test
    void getPokeApiDetail_shouldCallCorrectUrlAndReturnBody() {

        String name = "pikachu";

        PokeApiDetail expected =
                new PokeApiDetail(25, "pikachu",
                        List.of(new PokeApiTypeSlot(new PokeApiType("electric"))),
                        new PokeApiSprite("url"));

        when(restClient.get()
                .uri(eq(BASE_URL + "/pokemon/{name}"), eq(name))
                .retrieve()
                .body(PokeApiDetail.class))
                .thenReturn(expected);

        PokeApiDetail result = client.getPokeApiDetail(name);

        assertThat(result).isSameAs(expected);
    }


    @Test
    void getPokeApiDetail_shouldThrowWhenNameIsNull() {
        assertThatThrownBy(() -> client.getPokeApiDetail(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("name must not be null");
    }

    @Test
    void clearAllCaches_shouldNotThrow() {
        client.clearAllCaches();
    }

}
