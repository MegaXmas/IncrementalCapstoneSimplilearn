import { Component, Input, forwardRef, OnInit } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, map } from 'rxjs/operators';
import { Subject, Observable, of } from 'rxjs';
import { StationService } from '../../../services/station-service';

// Simple station interface for component use
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
  templateUrl: './station-search.html',
  styleUrls: ['./station-search.css', '../form-styles.css'],
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
  @Input() stationType: 'bus' | 'train' | 'airport' = 'airport'; // Default to airport for now
  
  // Basic component state
  searchQuery: string = '';
  selectedStation: Station | null = null;
  filteredStations: Station[] = [];
  isSearching: boolean = false;

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
      switchMap(query => {
        this.isSearching = true;
        return this.searchStations(query);
      })
    ).subscribe(stations => {
      this.filteredStations = stations;
      this.isSearching = false;
    });
  }

  ngOnInit(): void {
    console.log('Station search component initialized for type:', this.stationType);
  }

  /**
   * Handle keyup events on search input
   */
  onSearchKeyup(event: KeyboardEvent): void {
    const query = (event.target as HTMLInputElement).value;
    this.searchQuery = query;

    // Log keyup for tracking as requested
    console.log('Station search keyup:', {
      key: event.key,
      query: query,
      stationType: this.stationType,
      timestamp: new Date().toLocaleTimeString()
    });

    // Trigger search
    if (query.length >= 2) { // Start searching after 2 characters
      this.searchSubject.next(query);
    } else {
      this.filteredStations = [];
      this.isSearching = false;
    }
  }

    /**
     * Search stations using backend service
     */
  private searchStations(query: string): Observable<Station[]> {
    if (!query || query.trim().length < 2) {
      return of([]);
    }

    // Now all three types are implemented!
    switch (this.stationType) {
      case 'airport':
        return this.stationService.searchAirports(query).pipe(
          map(airports => this.convertToStationInterface(airports, 'airport'))
        );
      case 'bus':
        return this.stationService.searchBusStations(query).pipe(
          map(busStations => this.convertToStationInterface(busStations, 'bus'))
        );
      case 'train':
        console.log('Train station search not implemented yet');
        return of([]);
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
        case 'airport':
          return {
            id: station.id.toString(),
            fullName: station.airportFullName,
            code: station.airportCode,
            cityLocation: station.airportCityLocation
          };
        case 'bus':
          return {
            id: station.busStationId.toString(),
            fullName: station.busStationFullName,
            code: station.busStationCode,
            cityLocation: station.busStationCityLocation
          };
        case 'train':
          // TODO: Implement when train stations are ready
          return {
            id: station.trainStationId.toString(),
            fullName: station.trainStationFullName,
            code: station.trainStationCode,
            cityLocation: station.trainStationCityLocation
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
    this.searchQuery = `${station.code} - ${station.fullName}`;
    this.filteredStations = [];
    
    // Update form value with the station ID
    this.onChange(station.id);
    this.onTouch();

    console.log('Station selected:', {
      station: station,
      stationType: this.stationType
    });
  }

  /**
   * Clear the current selection
   */
  clearSelection(): void {
    this.selectedStation = null;
    this.searchQuery = '';
    this.filteredStations = [];
    this.onChange(null);
    console.log('Station selection cleared');
  }

  // Required ControlValueAccessor methods
  writeValue(value: any): void {
    if (!value) {
      this.clearSelection();
    }
    // TODO: If value is provided, fetch and display the station
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouch = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // Handle disabled state if needed
  }
}