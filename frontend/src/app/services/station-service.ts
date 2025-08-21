import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

// Backend data interfaces - matching your Java models
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
  id: number; // Java model uses Integer id
  airportFullName: string;
  airportCode: string;
  airportCityLocation: string; // Match Java field name
  airportCountryLocation: string; // Match Java field name
  airportTimezone: string;
}

@Injectable({
  providedIn: 'root'
})
export class StationService {
  
  private baseUrl = 'http://localhost:8080/api';
  
  constructor(private http: HttpClient) {}

  /**
   * Search bus stations by any term (name, code, or city)
   * TODO: Implement when bus controller is ready
   */
  universalBusStationSearch(query: string): Observable<BusStation[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }
    // Placeholder - implement when bus controller is ready
    console.log('Bus station search not implemented yet');
    return of([]);
  }

  /**
   * Search train stations by any term (name, code, or city)
   * TODO: Implement when train controller is ready
   */
  universalTrainStationSearch(query: string): Observable<TrainStation[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }
    // Placeholder - implement when train controller is ready
    console.log('Train station search not implemented yet');
    return of([]);
  }

  /**
   * Search airports by any term (name, code, or city)
   * Uses the airport controller search endpoint we created
   */
  searchAirports(query: string): Observable<Airport[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    // Use 'term' parameter to match our airport controller
    const params = new HttpParams().set('term', query.trim());
    
    return this.http.get<Airport[]>(`${this.baseUrl}/airports/search`, { params })
      .pipe(
        map(airports => {
          console.log('Airport search results:', airports);
          return airports || [];
        }),
        catchError(error => {
          console.error('Error searching airports:', error);
          return of([]);
        })
      );
  }

  /**
   * Get airport by ID - useful for selection validation
   */
  getAirportById(id: number): Observable<Airport | null> {
    return this.http.get<Airport>(`${this.baseUrl}/airports/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error getting airport by ID:', error);
          return of(null);
        })
      );
  }

  /**
   * Get airports by city - useful for city-specific searches
   */
  getAirportsByCity(cityName: string): Observable<Airport[]> {
    return this.http.get<Airport[]>(`${this.baseUrl}/airports/city/${encodeURIComponent(cityName)}`)
      .pipe(
        map(airports => airports || []),
        catchError(error => {
          console.error('Error getting airports by city:', error);
          return of([]);
        })
      );
  }
}