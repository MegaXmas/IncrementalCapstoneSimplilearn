import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface BookingSearchCriteria {
  transportType?: string;
  departureCity?: string;
  arrivalCity?: string;
  departureStation?: string;
  arrivalStation?: string;
  departureTime?: string;
  departureDate?: string; 
  minPrice?: number;
  maxPrice?: number;
  airline?: string; // For flights
  line?: string;    // For trains/buses
}
export interface AvailableTicket {
  id: number;
  transportType: string;
  number: string; // flight/train/bus number
  departureLocation: string;
  arrivalLocation: string;
  departureTime: string;
  arrivalTime: string;
  price: number;
  additionalInfo: string; // airline, line, etc.
}

export interface Booking {
  id?: number;
  bookingId: string;
  transportDetailsJson: string;
  clientName: string;
  clientEmail: string;
  clientPhone: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  
  private baseUrl = 'http://localhost:8080/api/search';
  
  constructor(private http: HttpClient) {}

  /**
   * Search for available tickets to book using your backend service
   * POST /api/search/available-tickets
   */
  searchAvailableTickets(criteria: BookingSearchCriteria): Observable<AvailableTicket[]> {
    console.log('🔍 Searching available tickets with criteria:', criteria);
    
    return this.http.post<AvailableTicket[]>(`${this.baseUrl}/available-tickets`, criteria)
      .pipe(
        map(tickets => {
          console.log('✅ Found available tickets:', tickets);
          return tickets;
        }),
        catchError(error => {
          console.error('❌ Error searching available tickets:', error);
          return of([]); // Return empty array on error
        })
      );
  }

  /**
   * Search for existing bookings using your backend service
   * POST /api/search/existing-bookings
   */
  searchExistingBookings(criteria: BookingSearchCriteria): Observable<Booking[]> {
    console.log('🔍 Searching existing bookings with criteria:', criteria);
    
    return this.http.post<Booking[]>(`${this.baseUrl}/existing-bookings`, criteria)
      .pipe(
        map(bookings => {
          console.log('✅ Found existing bookings:', bookings);
          return bookings;
        }),
        catchError(error => {
          console.error('❌ Error searching existing bookings:', error);
          return of([]); // Return empty array on error
        })
      );
  }

  /**
   * Quick search for flights by airline
   * GET /api/search/flights?airline=Delta
   */
  searchFlights(airline?: string): Observable<AvailableTicket[]> {
    let params = new HttpParams();
    if (airline) {
      params = params.set('airline', airline);
    }

    return this.http.get<AvailableTicket[]>(`${this.baseUrl}/flights`, { params })
      .pipe(
        catchError(error => {
          console.error('❌ Error searching flights:', error);
          return of([]);
        })
      );
  }

  /**
   * Quick search for trains by line
   * GET /api/search/trains?line=Amtrak
   */
  searchTrains(line?: string): Observable<AvailableTicket[]> {
    let params = new HttpParams();
    if (line) {
      params = params.set('line', line);
    }

    return this.http.get<AvailableTicket[]>(`${this.baseUrl}/trains`, { params })
      .pipe(
        catchError(error => {
          console.error('❌ Error searching trains:', error);
          return of([]);
        })
      );
  }

  /**
   * Quick search for buses by line
   * GET /api/search/buses?line=Greyhound
   */
  searchBuses(line?: string): Observable<AvailableTicket[]> {
    let params = new HttpParams();
    if (line) {
      params = params.set('line', line);
    }

    return this.http.get<AvailableTicket[]>(`${this.baseUrl}/buses`, { params })
      .pipe(
        catchError(error => {
          console.error('❌ Error searching buses:', error);
          return of([]);
        })
      );
  }

  /**
   * Get my bookings by email
   * GET /api/search/my-bookings?email=john@example.com
   */
  getMyBookings(email: string): Observable<Booking[]> {
    const params = new HttpParams().set('email', email);

    return this.http.get<Booking[]>(`${this.baseUrl}/my-bookings`, { params })
      .pipe(
        catchError(error => {
          console.error('❌ Error getting my bookings:', error);
          return of([]);
        })
      );
  }

  /**
   * Helper method to format ticket display info
   */
  formatTicketRoute(ticket: AvailableTicket): string {
    return `${ticket.departureLocation} → ${ticket.arrivalLocation}`;
  }

  /**
   * Helper method to format ticket time info
   */
  formatTicketTime(ticket: AvailableTicket): string {
    const depTime = new Date(ticket.departureTime).toLocaleString();
    const arrTime = new Date(ticket.arrivalTime).toLocaleString();
    return `${depTime} → ${arrTime}`;
  }

  /**
   * Helper method to format price
   */
  formatPrice(price: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  }
}