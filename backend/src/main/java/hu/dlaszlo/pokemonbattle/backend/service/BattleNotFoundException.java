package hu.dlaszlo.pokemonbattle.backend.service;

/**
 * Exception thrown when a battle cannot be found.
 */
public class BattleNotFoundException extends RuntimeException {
    public BattleNotFoundException(Long battleId) {
        super("Battle not found with id: " + battleId);
    }
}