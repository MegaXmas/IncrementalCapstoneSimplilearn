import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { StationSearchComponent } from '../station-search/station-search';

export interface BookingSearchCriteria {
  transportType: string;
  departureCity: string;
  arrivalCity: string;
  departureStation: string;
  arrivalStation: string;
  departureTime: string;
  minPrice: number;
  maxPrice: number;
}

@Component({
  selector: 'app-booking-search',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StationSearchComponent],
  templateUrl: './booking-search.html',
  styleUrls: ['./booking-search.css', '../form-styles.css']
})
export class BookingSearchComponent implements OnInit {
  
  searchForm!: FormGroup;
  searchResults: any[] = [];
  
  @Output() ticketSelected = new EventEmitter<any>();

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    // Create the search form with all criteria
    this.searchForm = this.fb.group({
      transportType: [''],
      departureCity: [''],
      arrivalCity: [''],
      departureStation: [''],
      arrivalStation: [''],
      departureTime: [''],
      minPrice: [0],
      maxPrice: [20000]
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
    
    // Here you would call your booking service to search
    // For now, we'll simulate some results
    this.searchResults = [
      {
        id: 1,
        transportType: searchCriteria.transportType || 'flight',
        route: `${searchCriteria.departureCity || 'City A'} → ${searchCriteria.arrivalCity || 'City B'}`,
        departureTime: searchCriteria.departureTime || '2024-12-01 10:00',
        price: 150
      },
      {
        id: 2,
        transportType: searchCriteria.transportType || 'train',
        route: `${searchCriteria.departureCity || 'City A'} → ${searchCriteria.arrivalCity || 'City B'}`,
        departureTime: searchCriteria.departureTime || '2024-12-01 14:30',
        price: 75
      }
    ];
  }

  selectTicket(ticket: any): void {
    this.ticketSelected.emit(ticket);
    console.log('Ticket selected:', ticket);
  }
}