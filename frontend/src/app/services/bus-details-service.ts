import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BusStation} from './bus-station-service';

export interface BusDetails {
    id?: number;
    busNumber: string;
    busLine: string;
    busDepartureStation: BusStation;
    busArrivalStation: BusStation;
    busDepartureDate: string; // ISO date string
    busDepartureTime: string; // ISO time string
    busArrivalDate: string; // ISO date string
    busArrivalTime: string; // ISO time string
    busRideDuration: string; // e.g., "2h 30m"
    busRidePrice: string; // Price in appropriate currency units
}

@Injectable({
  providedIn: 'root'
})
export class BusDetailsService {

  private baseUrl = 'http://localhost:8080/api/bus-details';

  constructor(private http: HttpClient) {}

  /**
   * Add a new bus Details
   * @param busDetails The bus Details data to send to backend
   * @returns Observable<string> Success message from backend
   */
  addBusDetails (busDetails: BusDetails): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<string>(this.baseUrl, busDetails, {
      headers,
      responseType: 'text' as 'json' // Handle string response from backend
    });
  }

  /**
   * Get all bus Detailss
   * @returns Observable<BusDetails[]> List of bus Detailss
   */
  getAllBusDetails(): Observable<BusDetails[]> {
    return this.http.get<BusDetails[]>(this.baseUrl);
  }
}
