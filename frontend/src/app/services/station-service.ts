import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

// Backend data interfaces
export interface BusStation {
  busStationId: string;
  busStationFullName: string;
  busStationCode: string;
  busStationCityLocation: string;
  busStationCountryLocation: string;
}

export interface TrainStation {
  trainStationId: string;
  trainStationFullName: string;
  trainStationCode: string;
  trainStationCityLocation: string;
  trainStationCountryLocation: string;
}

export interface Airport {
  airportId: string;
  airportFullName: string;
  airportCode: string;
  airportLocationCity: string;
  airportLocationCountry: string;
}

@Injectable({
  providedIn: 'root'
})
export class StationService {
  
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * Search bus stations by any term (name, code, or city)
   */
  universalBusStationSearch(query: string): Observable<BusStation[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('searchTerm', query.trim());
    
    return this.http.get<BusStation[]>(`${this.baseUrl}/bus-stations/search`, { params })
      .pipe(
        map(stations => stations || []),
        catchError(error => {
          console.error('Error searching bus stations:', error);
          return of([]);
        })
      );
  }

  /**
   * Search train stations by any term (name, code, or city)
   */
  universalTrainStationSearch(query: string): Observable<TrainStation[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('searchTerm', query.trim());
    
    return this.http.get<TrainStation[]>(`${this.baseUrl}/train-stations/search`, { params })
      .pipe(
        map(stations => stations || []),
        catchError(error => {
          console.error('Error searching train stations:', error);
          return of([]);
        })
      );
  }

  /**
   * Search airports by any term (name, code, or city)
   */
  searchAirports(query: string): Observable<Airport[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('searchTerm', query.trim());
    
    return this.http.get<Airport[]>(`${this.baseUrl}/airports/search`, { params })
      .pipe(
        map(airports => airports || []),
        catchError(error => {
          console.error('Error searching airports:', error);
          return of([]);
        })
      );
  }
}