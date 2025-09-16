import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface TrainStation {
  id?: number;
  trainStationFullName: string;
  trainStationCode: string;
  trainStationCityLocation: string;
}

@Injectable({
  providedIn: 'root'
})
export class TrainStationService {

  private baseUrl = 'http://localhost:8080/api/train-stations';

  constructor(private http: HttpClient) {}

  /**
   * Add a new train station
   * @param trainStation The train station data to send to backend
   * @returns Observable<string> Success message from backend
   */
  addTrainStation(trainStation: TrainStation): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<string>(this.baseUrl, trainStation, {
      headers,
      responseType: 'text' as 'json' // Handle string response from backend
    });
  }

  /**
   * Get all train stations
   * @returns Observable<TrainStation[]> List of train stations
   */
  getAllTrainStations(): Observable<TrainStation[]> {
    return this.http.get<TrainStation[]>(this.baseUrl);
  }

  /**
   * Update an existing train station
   * @param trainStation The train station data to update
   * @returns Observable<string> Success message from backend
   */
  updateTrainStation(trainStation: TrainStation): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.put<string>(this.baseUrl, trainStation, {
      headers,
      responseType: 'text' as 'json'
    });
  }

  /**
   * Delete a train station by ID
   * @param id The ID of the train station to delete
   * @returns Observable<string> Success message from backend
   */
  deleteTrainStation(id: number): Observable<string> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.delete<string>(this.baseUrl, {
      headers,
      body: id,
      responseType: 'text' as 'json'
    });
  }

  /**
   * Get train station by ID
   * @param id The train station ID
   * @returns Observable<TrainStation> The train station data
   */
  getTrainStationById(id: number): Observable<TrainStation> {
    return this.http.get<TrainStation>(`${this.baseUrl}/${id}`);
  }

  /**
   * Search train stations by search term
   * @param searchTerm The search term
   * @returns Observable<TrainStation[]> List of matching train stations
   */
  searchTrainStations(searchTerm: string): Observable<TrainStation[]> {
    return this.http.get<TrainStation[]>(`${this.baseUrl}/search?searchTerm=${encodeURIComponent(searchTerm)}`);
  }
}
