package hu.dlaszlo.pokemonbattle.backend.service.dto;

/**
 * Defines which participant won a completed battle
 */
public enum WinnerSide {
    /** The first Pokemon won the battle. */
    FIRST,
    /** The second Pokemon won the battle. */
    SECOND,
    /** The battle resulted in a draw (equal power). */
    DRAW
}