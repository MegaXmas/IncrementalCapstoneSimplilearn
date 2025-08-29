import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TrainStation } from './train-station-service';

export interface TrainDetails {
    id?: number;
    trainNumber: string;
    trainLine: string;
    trainDepartureStation: TrainStation;
    trainArrivalStation: TrainStation;
    trainDepartureDate: string; // ISO date string
    trainDepartureTime: string; // ISO time string
    trainArrivalDate: string; // ISO date string
    trainArrivalTime: string; // ISO time string
    trainRideDuration: string; // e.g., "2h 30m"
    trainRidePrice: string; // Price in appropriate currency units
}

@Injectable({
  providedIn: 'root'
})
export class TrainDetailsService {

  private baseUrl = 'http://localhost:8080/api/train-details';

  constructor(private http: HttpClient) {}

  /**
   * Add a new train Details
   * @param trainDetails The train Details data to send to backend
   * @returns Observable<string> Success message from backend
   */
  addTrainDetails (trainDetails: TrainDetails): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<string>(this.baseUrl, trainDetails, {
      headers,
      responseType: 'text' as 'json' // Handle string response from backend
    });
  }

  /**
   * Get all train Detailss
   * @returns Observable<TrainDetails[]> List of train Detailss
   */
  getAllTrainDetailss(): Observable<TrainDetails[]> {
    return this.http.get<TrainDetails[]>(this.baseUrl);
  }
}