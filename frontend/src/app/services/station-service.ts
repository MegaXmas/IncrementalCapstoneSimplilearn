import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';

export interface BusStation {
  id: number;
  busStationFullName: string;
  busStationCode: string;
  busStationCityLocation: string;
}

export interface TrainStation {
  id: number;
  trainStationFullName: string;
  trainStationCode: string;
  trainStationCityLocation: string;
}

export interface Airport {
  id: number;
  airportFullName: string;
  airportCode: string;
  airportCityLocation: string;
  airportCountryLocation: string;
  airportTimezone: string;
}

@Injectable({
  providedIn: 'root'
})
export class StationService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /**
   * Search airports by any term (name, code, or city)
   * Uses the airport controller search endpoint we created
   */
  searchAirports(query: string): Observable<Airport[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('searchTerm', query.trim());

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

  /**
   * Search bus stations by any term (name, code, or city)
   */
  searchBusStations(query: string): Observable<BusStation[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('searchTerm', query.trim());

    return this.http.get<BusStation[]>(`${this.baseUrl}/bus-stations/search`, { params })
      .pipe(
        map(stations => {
          console.log(`Bus station search found ${stations.length} results for "${query}"`);
          return stations;
        }),
        catchError(error => {
          console.error('Bus station search error:', error);
          return of([]);
        })
      );
  }

  /**
   * Get a bus station by ID
   */
  getBusStationById(id: string): Observable<BusStation | null> {
    return this.http.get<BusStation>(`${this.baseUrl}/bus-stations/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error fetching bus station:', error);
          return of(null);
        })
      );
  }

    /**
   * Search train stations by any term (name, code, or city)
   */
  searchTrainStations(query: string): Observable<TrainStation[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    const params = new HttpParams().set('searchTerm', query.trim());

    return this.http.get<TrainStation[]>(`${this.baseUrl}/train-stations/search`, { params })
      .pipe(
        map(stations => {
          console.log(`Train station search found ${stations.length} results for "${query}"`);
          return stations;
        }),
        catchError(error => {
          console.error('Train station search error:', error);
          return of([]);
        })
      );
  }

  /**
   * Get a train station by ID
   */
  getTrainStationById(id: string): Observable<TrainStation | null> {
    return this.http.get<TrainStation>(`${this.baseUrl}/train-stations/${id}`)
      .pipe(
        catchError(error => {
          console.error('Error fetching train station:', error);
          return of(null);
        })
      );
  }
}
