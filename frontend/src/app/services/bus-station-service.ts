import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BusStation {
  id?: number;
  busStationFullName: string;
  busStationCode: string;
  busStationCityLocation: string;
}

@Injectable({
  providedIn: 'root'
})
export class BusStationService {

  private baseUrl = 'http://localhost:8080/api/bus-stations';

  constructor(private http: HttpClient) {}

  /**
   * Add a new bus station
   * @param busStation The bus station data to send to backend
   * @returns Observable<string> Success message from backend
   */
  addBusStation (busStation: BusStation): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<string>(this.baseUrl, busStation, {
      headers,
      responseType: 'text' as 'json' // Handle string response from backend
    });
  }

  /**
   * Get all bus stations
   * @returns Observable<BusStation[]> List of bus stations
   */
  getAllBusStations(): Observable<BusStation[]> {
    return this.http.get<BusStation[]>(this.baseUrl);
  }
}