import { Routes } from '@angular/router';
import { BattleComponent } from './components/battle/battle.component';
import { HistoryComponent } from './components/history/history.component';

export const routes: Routes = [
  { path: 'battle', component: BattleComponent, title: 'Pokemon Battle' },
  { path: 'history', component: HistoryComponent, title: 'Battle History' },
  { path: '', redirectTo: '/battle', pathMatch: 'full' },
  { path: '**', redirectTo: '/battle' }
];
