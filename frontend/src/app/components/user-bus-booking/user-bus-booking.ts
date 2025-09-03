// Updated booking-search.component.ts
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
  templateUrl: './bus-booking-search.html',
  styleUrls: ['../shared/booking-search/booking-search.css', '../shared/form-styles.css']
})
export class UserBusBookingComponent implements OnInit {
 
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
    // Create the search form for buses only
    this.searchForm = this.fb.group({
      departureStation: [''],
      arrivalStation: [''],
      departureDate: [''],
      departureTime: [''],
      minPrice: [0],
      maxPrice: [20000],
      line: ['']  // Bus line (e.g., Greyhound)
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.searchForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }
  
  // Always return 'bus' since this component is bus-specific
  getStationType(): 'bus' {
    return 'bus';
  }
  
  onSearch(): void {
    // Create search criteria with bus type fixed
    const searchCriteria: BookingSearchCriteria = {
      transportType: 'bus',  // Fixed to bus
      ...this.searchForm.value
    };
    
    console.log('Searching buses with criteria:', searchCriteria);
    
    this.isSearching = true;
    this.searchError = '';
    this.searchResults = [];
    
    this.bookingService.searchAvailableTickets(searchCriteria).subscribe({
      next: (tickets: AvailableTicket[]) => {
        console.log('✅ Bus search completed, found tickets:', tickets);
        this.searchResults = tickets;
        this.isSearching = false;
        
        if (tickets.length === 0) {
          this.searchError = 'No bus tickets found matching your criteria. Try adjusting your search.';
        }
      },
      error: (error) => {
        console.error('❌ Bus search failed:', error);
        this.searchError = 'Bus search failed. Please check your connection and try again.';
        this.isSearching = false;
        this.searchResults = [];
      }
    });
  }
  
  selectTicket(ticket: AvailableTicket): void {
    this.ticketSelected.emit(ticket);
    console.log('Bus ticket selected:', ticket);
  }
  
  // Helper methods using the service
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
      departureStation: '',
      arrivalStation: '',
      departureTime: '',
      minPrice: 0,
      maxPrice: 20000,
      line: ''
    });
  }
}