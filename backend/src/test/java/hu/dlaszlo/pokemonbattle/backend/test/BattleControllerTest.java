package hu.dlaszlo.pokemonbattle.backend.test;

import hu.dlaszlo.pokemonbattle.backend.controller.BattleController;
import hu.dlaszlo.pokemonbattle.backend.service.BattleNotFoundException;
import hu.dlaszlo.pokemonbattle.backend.service.BattleService;
import hu.dlaszlo.pokemonbattle.backend.service.dto.Battle;
import hu.dlaszlo.pokemonbattle.backend.service.dto.BattleStatus;
import hu.dlaszlo.pokemonbattle.backend.service.dto.PokemonEntity;
import hu.dlaszlo.pokemonbattle.backend.service.dto.WinnerSide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BattleController.class)
class BattleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BattleService battleService;

    @TestConfiguration
    static class TestCacheConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Test
    void createRandomBattle_shouldReturnBattleFromService() throws Exception {
        Battle battle = new Battle(
                1L,
                BattleStatus.PENDING,
                new PokemonEntity("Pikachu", "electric", "url1", 10),
                new PokemonEntity("Bulbasaur", "grass", "url2", 12),
                null,
                Instant.now(),
                null
        );

        when(battleService.createRandomBattle()).thenReturn(battle);

        mockMvc.perform(post("/api/battles/random")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.first.name").value("Pikachu"))
                .andExpect(jsonPath("$.second.name").value("Bulbasaur"));

        verify(battleService).createRandomBattle();
    }

    @Test
    void simulateBattle_shouldReturnFinishedBattle() throws Exception {
        long id = 42L;

        Battle finished = new Battle(
                id,
                BattleStatus.FINISHED,
                new PokemonEntity("Pikachu", "electric", "url1", 15),
                new PokemonEntity("Bulbasaur", "grass", "url2", 5),
                WinnerSide.FIRST,
                Instant.now(),
                Instant.now()
        );

        when(battleService.simulateBattle(id)).thenReturn(finished);

        mockMvc.perform(post("/api/battles/{id}/simulate", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("FINISHED"))
                .andExpect(jsonPath("$.winnerSide").value("FIRST"));

        verify(battleService).simulateBattle(id);
    }

    @Test
    void simulateBattle_whenBattleNotFound_shouldReturn404() throws Exception {
        long id = 99L;

        when(battleService.simulateBattle(id))
                .thenThrow(new BattleNotFoundException(id));

        mockMvc.perform(post("/api/battles/{id}/simulate", id)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Battle not found with id: 99")));

        verify(battleService).simulateBattle(id);
    }

    @Test
    void listBattles_withoutQuery_shouldPassNullAndReturnList() throws Exception {
        Battle b1 = new Battle(
                1L,
                BattleStatus.PENDING,
                new PokemonEntity("Pikachu", "electric", "u1", 10),
                new PokemonEntity("Bulbasaur", "grass", "u2", 12),
                null,
                Instant.now(),
                null
        );
        Battle b2 = new Battle(
                2L,
                BattleStatus.FINISHED,
                new PokemonEntity("Charmander", "fire", "u3", 15),
                new PokemonEntity("Squirtle", "water", "u4", 8),
                WinnerSide.FIRST,
                Instant.now(),
                Instant.now()
        );

        when(battleService.searchBattles(null)).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/battles")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(battleService).searchBattles(isNull());
    }

    @Test
    void listBattles_withQuery_shouldPassQueryToService() throws Exception {
        String query = "pika";

        Battle b1 = new Battle(
                1L,
                BattleStatus.PENDING,
                new PokemonEntity("Pikachu", "electric", "u1", 10),
                new PokemonEntity("Bulbasaur", "grass", "u2", 12),
                null,
                Instant.now(),
                null
        );

        when(battleService.searchBattles(query)).thenReturn(List.of(b1));

        mockMvc.perform(get("/api/battles")
                        .param("q", query)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].first.name").value("Pikachu"));

        verify(battleService).searchBattles(eq(query));
    }
}
