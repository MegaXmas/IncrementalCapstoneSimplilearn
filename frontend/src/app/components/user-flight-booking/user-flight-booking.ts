import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StationSearchComponent } from '../shared/station-search/station-search';
import { BookingService, BookingSearchCriteria, AvailableTicket } from '../../services/booking-service';
import { DateInputComponent } from "../shared/date-dropdown/date-input";
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';

@Component({
  selector: 'app-booking-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StationSearchComponent, DateInputComponent, TimeDropdownComponent],
  templateUrl: './flight-booking-search.html',
  styleUrls: ['../shared/booking-search/booking-search.css', '../shared/form-styles.css']
})
export class UserFlightBookingComponent implements OnInit {

searchForm!: FormGroup;
  searchResults: AvailableTicket[] = [];
  isSearching = false;
  searchError = '';

  @Output() ticketSelected = new EventEmitter<AvailableTicket>();

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService
  ) {}

  ngOnInit(): void {
    // Create the search form for flights only
    this.searchForm = this.fb.group({
      originAirport: [''],
      destinationAirport: [''],
      departureDate: [''],
      departureTime: [''],
      minPrice: [0],
      maxPrice: [20000],
      airline: ['']
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.searchForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }

  // Always return 'airport' since this component is flight-specific
  getStationType(): 'airport' {
    return 'airport';
  }

  onSearch(): void {
  const formValue = this.searchForm.value;

  const searchCriteria: BookingSearchCriteria = {
    transportType: 'flight',
    // Map frontend form fields to backend expected fields
    departureStation: formValue.originAirport,      // backend expects departureStation
    arrivalStation: formValue.destinationAirport,   // backend expects arrivalStation
    departureDate: formValue.departureDate,
    departureTime: formValue.departureTime,
    minPrice: formValue.minPrice,
    maxPrice: formValue.maxPrice,
    airline: formValue.airline
  };

  console.log('Searching flights with criteria:', searchCriteria);

  this.isSearching = true;
  this.searchError = '';
  this.searchResults = [];

  this.bookingService.searchAvailableTickets(searchCriteria).subscribe({
    next: (tickets: AvailableTicket[]) => {
      console.log('✅ Flight search completed, found tickets:', tickets);
      this.searchResults = tickets;
      this.isSearching = false;

      if (tickets.length === 0) {
        this.searchError = 'No flight tickets found matching your criteria. Try adjusting your search.';
      }
    },
    error: (error) => {
      console.error('❌ Flight search failed:', error);
      this.searchError = 'Flight search failed. Please check your connection and try again.';
      this.isSearching = false;
      this.searchResults = [];
    }
  });
}

  selectTicket(ticket: AvailableTicket): void {
    this.ticketSelected.emit(ticket);
    console.log('Flight ticket selected:', ticket);
  }

  formatTicketRoute(ticket: AvailableTicket): string {
    return this.bookingService.formatTicketRoute(ticket);
  }

  formatTicketTime(ticket: AvailableTicket): string {
    return this.bookingService.formatTicketTime(ticket);
  }

  formatPrice(price: number): string {
    return this.bookingService.formatPrice(price);
  }

  // Clear search results
  clearSearch(): void {
    this.searchResults = [];
    this.searchError = '';
    this.searchForm.reset({
      originAirport: '',
      destinationAirport: '',
      departureTime: '',
      minPrice: 0,
      maxPrice: 20000,
      airline: ''
    });
  }
}
