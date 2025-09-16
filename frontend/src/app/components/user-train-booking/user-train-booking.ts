import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {StationSearchComponent} from '../shared/station-search/station-search';
import {AvailableTicket, BookingSearchCriteria, BookingService} from '../../services/booking-service';
import {DateInputComponent} from "../shared/date-dropdown/date-input";
import {TimeDropdownComponent} from '../shared/time-dropdown/time-dropdown';

@Component({
  selector: 'app-booking-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StationSearchComponent, DateInputComponent, TimeDropdownComponent],
  templateUrl: './train-booking-search.html',
  styleUrls: ['../shared/booking-search/booking-search.css', '../shared/form-styles.css']
})
export class UserTrainBookingComponent implements OnInit {

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
    // Create the search form for traines only
    this.searchForm = this.fb.group({
      departureStation: [''],
      arrivalStation: [''],
      departureDate: [''],
      departureTime: [''],
      minPrice: [0],
      maxPrice: [20000],
      line: ['']
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.searchForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }

  // Always return 'train' since this component is train-specific
  getStationType(): 'train' {
    return 'train';
  }

  onSearch(): void {
    const formValue = this.searchForm.value;

    const searchCriteria: BookingSearchCriteria = {
      transportType: 'train',
      // Form field names already match backend expectations
      departureStation: formValue.departureStation,
      arrivalStation: formValue.arrivalStation,
      departureTime: formValue.departureTime,
      minPrice: formValue.minPrice,
      maxPrice: formValue.maxPrice,
      line: formValue.line
    };

    console.log('Searching trains with criteria:', searchCriteria);

    this.isSearching = true;
    this.searchError = '';
    this.searchResults = [];

    this.bookingService.searchAvailableTickets(searchCriteria).subscribe({
      next: (tickets: AvailableTicket[]) => {
        console.log('✅ Train search completed, found tickets:', tickets);
        this.searchResults = tickets;
        this.isSearching = false;

        if (tickets.length === 0) {
          this.searchError = 'No train tickets found matching your criteria. Try adjusting your search.';
        }
      },
      error: (error) => {
        console.error('❌ Train search failed:', error);
        this.searchError = 'Train search failed. Please check your connection and try again.';
        this.isSearching = false;
        this.searchResults = [];
      }
    });
  }

  selectTicket(ticket: AvailableTicket): void {
    this.ticketSelected.emit(ticket);
    console.log('Train ticket selected:', ticket);
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
      departureStation: '',
      arrivalStation: '',
      departureDate: '',
      departureTime: '',
      minPrice: 0,
      maxPrice: 20000,
      line: ''
    });
  }
}
