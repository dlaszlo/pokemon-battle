package hu.dlaszlo.pokemonbattle.backend.service.dto;

/**
 * Defines the states of a battle simulation
 */
public enum BattleStatus {
    /** The battle has been created but not yet simulated. */
    PENDING,
    /** The battle simulation has been completed and the winner determined. */
    FINISHED
}