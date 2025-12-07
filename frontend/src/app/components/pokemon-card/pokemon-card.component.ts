import {Component, Input} from '@angular/core';
import {CommonModule} from '@angular/common';
import {PokemonEntity} from '../../models/pokemon.interface';

@Component({
  selector: 'app-pokemon-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pokemon-card.component.html',
  styleUrl: './pokemon-card.component.scss'
})
export class PokemonCardComponent {
  @Input() pokemon: PokemonEntity | null = null;
}
