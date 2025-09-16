import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Airport} from './airport-service';

export interface FlightDetails {
    id?: number;
    flightNumber: string;
    flightAirline: string;
    flightOrigin: Airport;
    flightDestination: Airport;
    flightDepartureDate: string; // ISO date string
    flightDepartureTime: string; // ISO time string
    flightArrivalDate: string; // ISO date string
    flightArrivalTime: string; // ISO time string
    flightTravelTime: string; // e.g., "2h 30m"
    flightPrice: string; // Price in appropriate currency units
}

@Injectable({
  providedIn: 'root'
})
export class FlightDetailsService {

  private baseUrl = 'http://localhost:8080/api/flight-details';

  constructor(private http: HttpClient) {}

  /**
   * Add a new flight Details
   * @param flightDetails The flight Details data to send to backend
   * @returns Observable<string> Success message from backend
   */
  addFlightDetails (flightDetails: FlightDetails): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<string>(this.baseUrl, flightDetails, {
      headers,
      responseType: 'text' as 'json' // Handle string response from backend
    });
  }

  /**
   * Get all flight Detailss
   * @returns Observable<flightDetails[]> List of flight Detailss
   */
  getAllFlightDetailss(): Observable<FlightDetails[]> {
    return this.http.get<FlightDetails[]>(this.baseUrl);
  }
}
