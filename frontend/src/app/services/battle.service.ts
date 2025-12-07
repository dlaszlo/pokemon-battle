import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Battle} from '../models/battle.interface';

import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BattleService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  createRandomBattle(): Observable<Battle> {
    return this.http.post<Battle>(`${this.apiUrl}/random`, {});
  }

  simulateBattle(id: number): Observable<Battle> {
    return this.http.post<Battle>(`${this.apiUrl}/${id}/simulate`, {});
  }

  listBattles(query?: string): Observable<Battle[]> {
    let params = new HttpParams();
    if (query) {
      params = params.set('q', query);
    }
    return this.http.get<Battle[]>(this.apiUrl, { params });
  }
}
