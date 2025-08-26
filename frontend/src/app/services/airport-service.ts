import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Airport {
  id?: number;
  airportFullName: string;
  airportCode: string;
  airportCityLocation: string;
  airportCountryLocation: string;
  airportTimezone: string;
}

@Injectable({
  providedIn: 'root'
})
export class AirportService {

  private baseUrl = 'http://localhost:8080/api/airports';

  constructor(private http: HttpClient) {}

  /**
   * Add a new airport
   * @param airport The airport data to send to backend
   * @returns Observable<string> Success message from backend
   */
  addAirport(airport: Airport): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<string>(this.baseUrl, airport, {
      headers,
      responseType: 'text' as 'json' // Handle string response from backend
    });
  }

  /**
   * Get all airports
   * @returns Observable<Airport[]> List of airports
   */
  getAllAirports(): Observable<Airport[]> {
    return this.http.get<Airport[]>(this.baseUrl);
  }
}
