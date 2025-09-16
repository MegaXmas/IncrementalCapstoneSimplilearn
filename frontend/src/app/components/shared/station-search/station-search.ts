import {Component, forwardRef, Input, OnInit} from '@angular/core';
import {ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {debounceTime, distinctUntilChanged, map, switchMap} from 'rxjs/operators';
import {Observable, of, Subject} from 'rxjs';
import {StationService} from '../../../services/station-service';
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
  @Input() stationType: 'bus' | 'train' | 'airport' = 'airport';

  searchQuery: string = '';
  selectedStation: Station | null = null;
  filteredStations: Station[] = [];
  isSearching: boolean = false;

  private searchSubject = new Subject<string>();

  onChange = (value: any) => {};
  onTouch = () => {};

  constructor(private stationService: StationService) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        console.log('🔍 Starting search for:', query, 'Type:', this.stationType);
        this.isSearching = true;
        return this.searchStations(query);
      })
    ).subscribe(stations => {
      console.log('🎯 Search results received:', stations.length, 'stations');
      this.filteredStations = stations;
      this.isSearching = false;
    });
  }

  ngOnInit(): void {
    console.log('🚀 Station search component initialized for type:', this.stationType);
  }

  /**
   * Handle keyup events on search input
   */
  onSearchKeyup(event: KeyboardEvent): void {
    const query = (event.target as HTMLInputElement).value;
    this.searchQuery = query;

    // ENHANCED LOGGING FOR DEBUGGING
    console.log('⌨️ KEYUP EVENT:', {
      key: event.key,
      query: query,
      queryLength: query.length,
      stationType: this.stationType,
      timestamp: new Date().toLocaleTimeString()
    });

    // Trigger search
    if (query.length >= 2) {
      console.log('✅ Query length >= 2, triggering search...');
      this.searchSubject.next(query);
    } else {
      console.log('❌ Query too short, clearing results');
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

    console.log('🔎 searchStations called with:', query, 'for type:', this.stationType);

    switch (this.stationType) {
      case 'airport':
        console.log('✈️ Searching airports...');
        return this.stationService.searchAirports(query).pipe(
          map(airports => this.convertToStationInterface(airports, 'airport'))
        );

      case 'bus':
        console.log('🚌 Searching bus stations...');
        return this.stationService.searchBusStations(query).pipe(
          map(busStations => {
            console.log('🚌 Raw bus stations from API:', busStations);
            return this.convertToStationInterface(busStations, 'bus');
          })
        );

      case 'train':
        console.log('🚌 Searching train stations...');
        return this.stationService.searchTrainStations(query).pipe(
          map(trainStations => {
            console.log('🚌 Raw train stations from API:', trainStations);
            return this.convertToStationInterface(trainStations, 'train');
          })
        );


      default:
        console.log('❓ Unknown station type:', this.stationType);
        return of([]);
    }
  }

  /**
   * Convert backend data to simple Station interface
   */
  private convertToStationInterface(stations: any[], type: string): Station[] {
    console.log(`🔄 Converting ${stations.length} ${type} stations to interface`);
    console.log('🔄 First station structure:', stations[0]);

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
          const converted = {
            id: station.id.toString(),
            fullName: station.busStationFullName,
            code: station.busStationCode,
            cityLocation: station.busStationCityLocation
          };
          console.log('🚌 Converted bus station:', station, '→', converted);
          return converted;
        case 'train':
          return {
            id: station.id.toString(),
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
    console.log('✅ Station selected:', station);
    this.selectedStation = station;
    this.searchQuery = `${station.code} - ${station.fullName}`;
    this.filteredStations = [];

    // Update form value with the station ID
    this.onChange(station.code);
    this.onTouch();
  }

  /**
   * Clear the current selection
   */
  clearSelection(): void {
    console.log('🗑️ Station selection cleared');
    this.selectedStation = null;
    this.searchQuery = '';
    this.filteredStations = [];
    this.onChange(null);
  }

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

  setDisabledState?(isDisabled: boolean): void {
  }
}
