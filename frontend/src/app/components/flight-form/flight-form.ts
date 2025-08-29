import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { DurationDropdownComponent } from '../shared/duration-dropdown/duration-dropdown';
import { AirportService } from '../../services/airport-service';
import { forkJoin } from 'rxjs';
import { StationService } from '../../services/station-service';
import { FlightDetailsService, FlightDetails } from '../../services/flight-details-service';
import { StationSearchComponent } from '../shared/station-search/station-search';

@Component({
  selector: 'app-flight-form',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
    TimeDropdownComponent, 
    DurationDropdownComponent,
    StationSearchComponent
  ],
  templateUrl: './flight-form.html',
  styleUrls: ['./flight-form.css','../shared/form-styles.css'],
})
export class FlightFormComponent implements OnInit {
  flightForm!: FormGroup;
  isInvalid: boolean = false;
  isSubmitting = false;
  submitMessage = '';
  submitSuccess = false;

  constructor(
    private fb: FormBuilder,
    private flightDetailsService: FlightDetailsService,
    private stationService: StationService) {}

  ngOnInit(): void {
      this.flightForm = this.fb.group({
        flightNumber: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(10)]],
        flightAirline: ['', [Validators.required, Validators.minLength(2)]],
        flightOrigin: ['', Validators.required],
        flightDestination: ['', Validators.required],
        // FIX: Put ALL synchronous validators in the same array
        flightDepartureDate: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
        flightArrivalDate: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
        flightDepartureTime: ['', [Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)]],
        flightArrivalTime: ['', [Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)]],
        flightTravelTime: ['', [Validators.required, Validators.pattern(/^\d{1,2}h\s?\d{0,2}m?|\d{1,2}h$/)]],
        flightPrice: ['', [Validators.required, Validators.min(0), Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
      });

      console.log('Initial form state:', {
        dirty: this.flightForm.dirty,
        pristine: this.flightForm.pristine,
        valid: this.flightForm.valid
      });
    }

isFieldInvalid(fieldName: string): boolean {
    const field = this.flightForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }

  onSubmit(): void {
    if (this.flightForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';

      // Get airport IDs from form (these come from AirportSearchComponent)
      const originId = this.flightForm.value.flightOrigin;
      const destinationId = this.flightForm.value.flightDestination;

      // Get Airport objects to create the flightDetails properly
      this.getAirportObjects(originId, destinationId);
    } else {
      this.flightForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }


/**
   * Fetch Airport objects by their IDs and create the BusDetails
   */
  private getAirportObjects(originId: number, destinationId: number): void {
    console.log('🔍 Fetching Airports with IDs:', { originId, destinationId });

    // Fetch both Airports in parallel
    const origin$ = this.stationService.getAirportById(originId);
    const destination$ = this.stationService.getAirportById(destinationId);

    // Use forkJoin to wait for both Airport requests to complete
    forkJoin({
      originAirport: origin$,
      destinationAirport: destination$
    }).subscribe({
      next: (airports) => {
        console.log('🚉 Fetched Airports:', airports);

        if (!airports.originAirport || !airports.destinationAirport) {
          this.submitMessage = 'Error: Could not fetch Airport details. Please try again.';
          this.submitSuccess = false;
          this.isSubmitting = false;
          return;
        }

        // Create the AFlightDetails object with full Airport data (matches Java model)
        const flightDetailsData: FlightDetails = {
          flightNumber: this.flightForm.value.flightNumber,
          flightAirline: this.flightForm.value.flightAirline,
          flightOrigin: {
            id: airports.originAirport.id,                              
            airportFullName: airports.originAirport.airportFullName,
            airportCode: airports.originAirport.airportCode,
            airportCityLocation: airports.originAirport.airportCityLocation,
            airportCountryLocation: airports.originAirport.airportCountryLocation,
            airportTimezone: airports.originAirport.airportTimezone
          },
          flightDestination: {
            id: airports.destinationAirport.id,                                
            airportFullName: airports.destinationAirport.airportFullName,
            airportCode: airports.destinationAirport.airportCode,
            airportCityLocation: airports.destinationAirport.airportCityLocation,
            airportCountryLocation: airports.destinationAirport.airportCountryLocation,
            airportTimezone: airports.destinationAirport.airportTimezone
          },
          flightDepartureDate: this.flightForm.value.flightDepartureDate,
          flightDepartureTime: this.flightForm.value.flightDepartureTime,
          flightArrivalDate: this.flightForm.value.flightArrivalDate,
          flightArrivalTime: this.flightForm.value.flightArrivalTime,
          flightTravelTime: this.flightForm.value.flightTravelTime,
          flightPrice: this.flightForm.value.flightPrice
        };


        console.log('📤 Submitting bus details data:', flightDetailsData);
        console.log('🚉 origin Airport:', airports.originAirport);
        console.log('🚉 destination Airport:', airports.destinationAirport);

        this.flightDetailsService.addFlightDetails(flightDetailsData).subscribe({
          next: (response) => {
            console.log('✅ Bus Details added successfully:', response);
            this.submitMessage = response;
            this.submitSuccess = true;
            this.isSubmitting = false;
            this.flightForm.reset();
          },
          error: (error) => {
            console.error('❌ Error adding flight Details:', error);
            this.submitMessage = 'Failed to add flight Details. Please try again.';
            this.submitSuccess = false;
            this.isSubmitting = false;
          },
        });
      },
      error: (error) => {
        console.error('❌ Error fetching Airports:', error);
        this.submitMessage = 'Error: Could not fetch Airport details. Please try again.';
        this.submitSuccess = false;
        this.isSubmitting = false;
      }
    });
  }
}