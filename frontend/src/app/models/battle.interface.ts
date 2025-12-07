import {PokemonEntity} from './pokemon.interface';

export type BattleStatus = 'PENDING' | 'FINISHED';
export type WinnerSide = 'FIRST' | 'SECOND' | 'DRAW';

export interface Battle {
  id: number;
  status: BattleStatus;
  first: PokemonEntity;
  second: PokemonEntity;
  winnerSide: WinnerSide | null;
  createdAt: string;
  finishedAt: string | null;
}
