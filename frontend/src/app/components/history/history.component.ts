import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BattleService } from '../../services/battle.service';
import { Battle } from '../../models/battle.interface';
import { PokemonEntity } from '../../models/pokemon.interface';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './history.component.html',
  styleUrl: './history.component.scss'
})
export class HistoryComponent implements OnInit {
  battles: Battle[] = [];
  errorMessage: string = '';

  constructor(private battleService: BattleService) {}

  ngOnInit(): void {
    this.refreshList();
  }

  refreshList(query: string = ''): void {
    this.errorMessage = '';

    this.battleService.listBattles(query).subscribe({
      next: (data) => {
        this.battles = data;
      },
      error: (err) => {
        console.error('Hiba:', err);
        this.errorMessage = 'Failed to load the data. Please check if the server is running!';
        this.battles = [];
      }
    });
  }

  onSearch(event: any): void {
    const query = event.target.value;
    this.refreshList(query);
  }

  getWinner(battle: Battle): PokemonEntity {
    return battle.winnerSide === 'SECOND' ? battle.second : battle.first;
  }

  getLoser(battle: Battle): PokemonEntity {
    return battle.winnerSide === 'SECOND' ? battle.first : battle.second;
  }
}
