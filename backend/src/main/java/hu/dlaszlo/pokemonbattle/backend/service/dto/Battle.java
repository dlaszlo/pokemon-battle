package hu.dlaszlo.pokemonbattle.backend.service.dto;

import java.time.Instant;

/**
 * Represents the record of a single Pokemon battle.
 *
 * @param id ID of the battle
 * @param status Current status of the battle (PENDING, FINISHED)
 * @param first The data of the first Pokemon participating in the battle
 * @param second The data of the second Pokemon participating in the battle
 * @param winnerSide Which side won the battle (FIRST, SECOND, DRAW), only set if status is FINISHED
 * @param createdAt The timestamp when the battle was initiated
 * @param finishedAt The timestamp when the simulation was completed, or null if status is PENDING
 */
public record Battle(
        Long id,
        BattleStatus status,
        PokemonEntity first,
        PokemonEntity second,
        WinnerSide winnerSide,
        Instant createdAt,
        Instant finishedAt
) {
}