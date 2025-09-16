import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {forkJoin} from 'rxjs';
import {DateInputComponent} from '../shared/date-dropdown/date-input';
import {TimeDropdownComponent} from '../shared/time-dropdown/time-dropdown';
import {DurationDropdownComponent} from '../shared/duration-dropdown/duration-dropdown';
import {BusDetails, BusDetailsService} from '../../services/bus-details-service';
import {StationSearchComponent} from "../shared/station-search/station-search";
import {StationService} from '../../services/station-service';

@Component({
  selector: 'app-bus-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DateInputComponent,
    TimeDropdownComponent,
    DurationDropdownComponent,
    StationSearchComponent
  ],
  standalone: true,
  templateUrl: './bus-form.html',
  styleUrls: ['./bus-form.css','../shared/form-styles.css'],
})
export class BusFormComponent implements OnInit {
  busForm!: FormGroup;
  isInvalid: boolean = false;
  isSubmitting = false;
  submitMessage = '';
  submitSuccess = false;

  constructor(
    private fb: FormBuilder,
    private busDetailsService: BusDetailsService,
    private stationService: StationService
  ) {}

  ngOnInit(): void {
    this.busForm = this.fb.group({
      busNumber: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      busLine: ['', [Validators.required, Validators.minLength(2)]],
      busDepartureStation: ['', Validators.required],
      busArrivalStation: ['', Validators.required],
      busDepartureDate: ['', [Validators.required, Validators.pattern(/\d{4}-\d{2}-\d{2}$/)]],
      busDepartureTime: ['', [Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)]],
      busArrivalDate: ['', [Validators.required, Validators.pattern(/\d{4}-\d{2}-\d{2}$/)]],
      busArrivalTime: ['', [Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)]],
      busRideDuration: ['', [Validators.required, Validators.pattern(/^\d{1,2}h\s?\d{0,2}m?|\d{1,2}h$/)]],
      busRidePrice: ['', [Validators.required, Validators.min(0), Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
    });

    console.log('Bus form initialized with controls:', Object.keys(this.busForm.controls));
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.busForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }

  onSubmit(): void {
    if (this.busForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';

      // Get station IDs from form
      const departureStationId = this.busForm.value.busDepartureStation;
      const arrivalStationId = this.busForm.value.busArrivalStation;

      // Get station objects
      this.getStationObjects(departureStationId, arrivalStationId);
    } else {
      this.busForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }

  /**
   * Fetch station objects by their IDs and create the BusDetails
   */
  private getStationObjects(departureStationId: string, arrivalStationId: string): void {
    console.log('🔍 Fetching stations with IDs:', { departureStationId, arrivalStationId });

    // Fetch both stations in parallel
    const departure$ = this.stationService.getBusStationById(departureStationId);
    const arrival$ = this.stationService.getBusStationById(arrivalStationId);

    // Use forkJoin to wait for both station requests to complete
    forkJoin({
      departureStation: departure$,
      arrivalStation: arrival$
    }).subscribe({
      next: (stations) => {
        console.log('🚉 Fetched stations:', stations);

        if (!stations.departureStation || !stations.arrivalStation) {
          this.submitMessage = 'Error: Could not fetch station details. Please try again.';
          this.submitSuccess = false;
          this.isSubmitting = false;
          return;
        }

        // Create the BusDetails object with full station data
        const busDetailsData: BusDetails = {
          busNumber: this.busForm.value.busNumber,
          busLine: this.busForm.value.busLine,
          busDepartureStation: {
            id: stations.departureStation.id,
            busStationFullName: stations.departureStation.busStationFullName,
            busStationCode: stations.departureStation.busStationCode,
            busStationCityLocation: stations.departureStation.busStationCityLocation
          },
          busArrivalStation: {
            id: stations.arrivalStation.id,
            busStationFullName: stations.arrivalStation.busStationFullName,
            busStationCode: stations.arrivalStation.busStationCode,
            busStationCityLocation: stations.arrivalStation.busStationCityLocation
          },
          busDepartureDate: this.busForm.value.busDepartureDate,
          busDepartureTime: this.busForm.value.busDepartureTime,
          busArrivalDate: this.busForm.value.busArrivalDate,
          busArrivalTime: this.busForm.value.busArrivalTime,
          busRideDuration: this.busForm.value.busRideDuration,
          busRidePrice: this.busForm.value.busRidePrice.toString()
        };

        console.log('📤 Submitting bus details data:', busDetailsData);
        console.log('🚉 Departure station:', stations.departureStation);
        console.log('🚉 Arrival station:', stations.arrivalStation);

        this.busDetailsService.addBusDetails(busDetailsData).subscribe({
          next: (response) => {
            console.log('✅ Bus Details added successfully:', response);
            this.submitMessage = response;
            this.submitSuccess = true;
            this.isSubmitting = false;
            this.busForm.reset();
          },
          error: (error) => {
            console.error('❌ Error adding bus Details:', error);
            this.submitMessage = 'Failed to add bus Details. Please try again.';
            this.submitSuccess = false;
            this.isSubmitting = false;
          },
        });
      },
      error: (error) => {
        console.error('❌ Error fetching stations:', error);
        this.submitMessage = 'Error: Could not fetch station details. Please try again.';
        this.submitSuccess = false;
        this.isSubmitting = false;
      }
    });
  }
}
