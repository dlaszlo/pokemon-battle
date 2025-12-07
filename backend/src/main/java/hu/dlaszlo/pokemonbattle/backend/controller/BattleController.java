package hu.dlaszlo.pokemonbattle.backend.controller;

import hu.dlaszlo.pokemonbattle.backend.service.BattleNotFoundException;
import hu.dlaszlo.pokemonbattle.backend.service.BattleService;
import hu.dlaszlo.pokemonbattle.backend.service.dto.Battle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing endpoints for Pokemon battles.
 */
@RestController
@RequestMapping("/api/battles")
public class BattleController {

    private final BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    /**
     * Creates a new battle with two random Pokemon.
     */
    @PostMapping("/random")
    public Battle createRandomBattle() {
        return battleService.createRandomBattle();
    }

    /**
     * Simulates a battle by its id and returns the updated battle
     * (including the winner information).
     */
    @PostMapping("/{id}/simulate")
    public Battle simulateBattle(@PathVariable("id") Long id) {
        return battleService.simulateBattle(id);
    }

    /**
     * Returns the latest battles (max 20), optionally filtered by Pokemon name.
     * If query is null or empty, all battles are returned (limited to 20).
     */
    @GetMapping
    public List<Battle> listBattles(@RequestParam(name = "q", required = false) String query) {
        return battleService.searchBattles(query);
    }

    /**
     * Maps BattleNotFoundException to HTTP 404.
     */
    @ExceptionHandler(BattleNotFoundException.class)
    public ResponseEntity<String> handleBattleNotFound(BattleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}