package hu.dlaszlo.pokemonbattle.backend.service;

import hu.dlaszlo.pokemonbattle.backend.service.dto.Battle;

import java.util.List;

/**
 * Service responsible for creating, simulating and searching Pokemon battles.
 */
public interface BattleService {

    /**
     * Creates a new battle with two randomly selected Pokemon and random power values.
     * The battle is stored with status {@code PENDING}.
     *
     * @return the newly created pending battle
     */
    Battle createRandomBattle();

    /**
     * Simulates the battle with the given id, using the stored Pokemon power values.
     * Updates the battle status to {@code FINISHED} and sets the winner side.
     *
     * @param battleId the id of the battle to simulate
     * @return the updated, finished battle
     * @throws IllegalArgumentException if a battle with the given id does not exist
     */
    Battle simulateBattle(Long battleId);

    /**
     * Returns battles filtered by Pokemon name.
     * If {@code nameFilter} is {@code null} or blank, all battles are returned.
     * Implementations may limit the number of returned battles (e.g. last 20).
     *
     * @param nameFilter optional name substring to search in both Pokemon names
     * @return list of matching battles, ordered from newest to oldest
     */
    List<Battle> searchBattles(String nameFilter);

}