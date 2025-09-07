import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StationSearchComponent } from '../station-search/station-search';
import { BookingService, BookingSearchCriteria, AvailableTicket } from '../../../services/booking-service';

@Component({
  selector: 'app-booking-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StationSearchComponent],
  templateUrl: './booking-search.html',
  styleUrls: ['./booking-search.css', '../form-styles.css']
})
export class BookingSearchComponent implements OnInit {
 
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
    // Create the search form with all criteria
    this.searchForm = this.fb.group({
      transportType: [''],
      departureCity: [''],
      arrivalCity: [''],
      departureStation: [''],
      arrivalStation: [''],
      departureDate: [''],
      departureTime: [''],
      minPrice: [0],
      maxPrice: [20000],
      airline: [''], // For flights
      line: ['']     // For trains/buses
    });
  }
  
  // Convert transport type to station type for StationSearchComponent
  getStationType(): 'bus' | 'train' | 'airport' {
    const transportType = this.searchForm.get('transportType')?.value;
    if (transportType === 'flight') return 'airport';
    if (transportType === 'train') return 'train';
    return 'bus';
  }
  
  onSearch(): void {
    const searchCriteria: BookingSearchCriteria = this.searchForm.value;
    console.log('Searching with criteria:', searchCriteria);
    
    this.isSearching = true;
    this.searchError = '';
    this.searchResults = [];
    
    // Always use the full search with all criteria
    this.bookingService.searchAvailableTickets(searchCriteria).subscribe({
      next: (tickets: AvailableTicket[]) => {
        console.log('✅ Search completed, found tickets:', tickets);
        this.searchResults = tickets;
        this.isSearching = false;
        
        if (tickets.length === 0) {
          this.searchError = 'No tickets found matching your criteria. Try adjusting your search.';
        }
      },
      error: (error) => {
        console.error('❌ Search failed:', error);
        this.searchError = 'Search failed. Please check your connection and try again.';
        this.isSearching = false;
        this.searchResults = [];
      }
    });
  }
  
  selectTicket(ticket: AvailableTicket): void {
    this.ticketSelected.emit(ticket);
    console.log('Ticket selected:', ticket);
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
      transportType: '',
      departureCity: '',
      arrivalCity: '',
      departureStation: '',
      arrivalStation: '',
      departureTime: '',
      minPrice: 0,
      maxPrice: 20000,
      airline: '',
      line: ''
    });
  }
}