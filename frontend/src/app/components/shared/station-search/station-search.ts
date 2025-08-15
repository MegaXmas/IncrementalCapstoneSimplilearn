import { Component, Input, forwardRef, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, map } from 'rxjs/operators';
import { Subject, Observable, of } from 'rxjs';
import { StationService, BusStation, TrainStation, Airport } from '../../../services/station-service';

// Simple station interface
export interface Station {
  id: string;
  fullName: string;
  code: string;
  cityLocation: string;
}

@Component({
  selector: 'app-station-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="station-search-container">
      <label [for]="id" class="form-label text-center d-block">{{ label }}</label>
      
      <!-- Search Input -->
      <input
        type="text"
        [id]="id"
        class="form-control text-center"
        [placeholder]="placeholder"
        [(ngModel)]="searchQuery"
        (keyup)="onSearchKeyup($event)"
        autocomplete="off">

      <!-- Search Results Dropdown -->
      <div *ngIf="filteredStations.length > 0" class="search-results">
        <div *ngFor="let station of filteredStations" 
             class="search-result-item"
             (click)="selectStation(station)">
          
          <div class="station-info">
            <div class="station-name">{{ station.fullName }}</div>
            <div class="station-details">
              <span class="station-code">{{ station.code }}</span>
              <span class="station-location">{{ station.cityLocation }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Selected Station Display -->
      <div *ngIf="selectedStation" class="selected-station mt-2">
        <div class="alert alert-success">
          <strong>Selected:</strong> {{ selectedStation.fullName }} ({{ selectedStation.code }})
          <button type="button" class="btn btn-sm btn-outline-secondary ms-2" 
                  (click)="clearSelection()">
            Clear
          </button>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./station-search.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => StationSearchComponent),
      multi: true
    }
  ]
})
export class StationSearchComponent implements ControlValueAccessor, OnInit {
  
  @Input() label: string = 'Station';
  @Input() id: string = 'station-search';
  @Input() placeholder: string = 'Search for a station...';
  @Input() stationType: 'bus' | 'train' | 'airport' = 'bus';
  
  // Basic component state
  searchQuery: string = '';
  selectedStation: Station | null = null;
  filteredStations: Station[] = [];

  // RxJS for search
  private searchSubject = new Subject<string>();

  // Form control methods
  onChange = (value: any) => {};
  onTouch = () => {};

  constructor(private stationService: StationService) {
    // Set up search with debouncing
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => this.searchStations(query))
    ).subscribe(stations => {
      this.filteredStations = stations;
    });
  }

  ngOnInit(): void {
    // Component is ready
  }

  /**
   * Handle keyup events on search input
   */
  onSearchKeyup(event: KeyboardEvent): void {
    const query = (event.target as HTMLInputElement).value;
    this.searchQuery = query;

    // Trigger search
    if (query.length > 0) {
      this.searchSubject.next(query);
    } else {
      this.filteredStations = [];
    }

    // Log keyup for tracking
    console.log('Station search keyup:', {
      key: event.key,
      query: query,
      timestamp: new Date().toLocaleTimeString()
    });
  }

  /**
   * Search stations using backend service
   */
  private searchStations(query: string): Observable<Station[]> {
    if (!query || query.trim().length === 0) {
      return of([]);
    }

    // Choose search method based on station type
    switch (this.stationType) {
      case 'bus':
        return this.stationService.universalBusStationSearch(query).pipe(
          map(stations => this.convertToStationInterface(stations, 'bus'))
        );
      case 'train':
        return this.stationService.universalTrainStationSearch(query).pipe(
          map(stations => this.convertToStationInterface(stations, 'train'))
        );
      case 'airport':
        return this.stationService.searchAirports(query).pipe(
          map(stations => this.convertToStationInterface(stations, 'airport'))
        );
      default:
        return of([]);
    }
  }

  /**
   * Convert backend data to simple Station interface
   */
  private convertToStationInterface(stations: any[], type: string): Station[] {
    return stations.map(station => {
      switch (type) {
        case 'bus':
          return {
            id: station.busStationId,
            fullName: station.busStationFullName,
            code: station.busStationCode,
            cityLocation: station.busStationCityLocation
          };
        case 'train':
          return {
            id: station.trainStationId,
            fullName: station.trainStationFullName,
            code: station.trainStationCode,
            cityLocation: station.trainStationCityLocation
          };
        case 'airport':
          return {
            id: station.airportId,
            fullName: station.airportFullName,
            code: station.airportCode,
            cityLocation: station.airportLocationCity
          };
        default:
          return station;
      }
    });
  }

  /**
   * Select a station from search results
   */
  selectStation(station: Station): void {
    this.selectedStation = station;
    this.searchQuery = station.fullName;
    this.filteredStations = [];
    
    // Update form value
    this.onChange(station.id);
    this.onTouch();

    console.log('Station selected:', station);
  }

  /**
   * Clear the current selection
   */
  clearSelection(): void {
    this.selectedStation = null;
    this.searchQuery = '';
    this.filteredStations = [];
    this.onChange(null);
  }

  // Required ControlValueAccessor methods
  writeValue(value: any): void {
    if (!value) {
      this.clearSelection();
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouch = fn;
  }
}