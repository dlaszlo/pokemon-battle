import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BattleService } from '../../services/battle.service';
import { Battle } from '../../models/battle.interface';
import { PokemonCardComponent } from '../pokemon-card/pokemon-card.component';

@Component({
  selector: 'app-battle',
  standalone: true,
  imports: [CommonModule, PokemonCardComponent],
  templateUrl: './battle.component.html',
  styleUrl: './battle.component.scss'
})
export class BattleComponent implements OnInit {
  battle: Battle | null = null;
  errorMessage = '';
  isLoading = false;
  isSimulating = false;

  constructor(private battleService: BattleService) {}

  ngOnInit(): void {
    this.startNewBattle();
  }

  startNewBattle(): void {
    this.errorMessage = '';
    this.isLoading = true;
    this.isSimulating = false;
    this.battle = null;

    this.battleService.createRandomBattle().subscribe({
      next: (battle) => {
        // UI update
        this.battle = battle;
        this.isLoading = false;
        this.isSimulating = true;

        this.battleService.simulateBattle(battle.id).subscribe({
          next: (updatedBattle) => {
            this.battle = updatedBattle;
            this.isSimulating = false;
          },
          error: (err) => {
            console.error(err);
            this.errorMessage = 'Error during battle simulation.';
            this.isSimulating = false;
          }
        });
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = 'Error during loading Pokémons.';
        this.isLoading = false;
      }
    });
  }


  isFinished(): boolean {
    return this.battle?.status === 'FINISHED';
  }

  getWinnerName(): string {
    if (!this.battle || this.battle.winnerSide === 'DRAW') {
      return 'Draw';
    }
    return this.battle.winnerSide === 'FIRST'
      ? this.battle.first.name
      : this.battle.second.name;
  }
}
