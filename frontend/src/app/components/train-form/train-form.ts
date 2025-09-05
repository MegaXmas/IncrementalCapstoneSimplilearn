import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { DateInputComponent } from '../shared/date-dropdown/date-input';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { DurationDropdownComponent } from '../shared/duration-dropdown/duration-dropdown';
import { TrainDetailsService, TrainDetails } from '../../services/train-details-service';
import { StationSearchComponent } from "../shared/station-search/station-search";
import { StationService } from '../../services/station-service';

@Component({
  selector: 'app-train-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DateInputComponent,
    DateInputComponent,
    DurationDropdownComponent,
    StationSearchComponent 
  ],
  standalone: true,
  templateUrl: './train-form.html',
  styleUrls: ['./train-form.css','../shared/form-styles.css'],
})
export class TrainFormComponent implements OnInit {
  trainForm!: FormGroup;
  isInvalid: boolean = false;
  isSubmitting = false;
  submitMessage = '';
  submitSuccess = false;

  constructor(
    private fb: FormBuilder,
    private trainDetailsService: TrainDetailsService,
    private stationService: StationService  
  ) {}
 
  ngOnInit(): void {
    this.trainForm = this.fb.group({
      trainNumber: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(10)]],
      trainLine: ['', [Validators.required, Validators.minLength(2)]],
      trainDepartureStation: ['', Validators.required],  // This matches the formControlName in template
      trainArrivalStation: ['', Validators.required],    // This matches the formControlName in template
      trainDepartureDate: ['', [Validators.required, Validators.pattern(/\d{4}-\d{2}-\d{2}$/)]],
      trainDepartureTime: ['', [Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)]],
      trainArrivalDate: ['', [Validators.required, Validators.pattern(/\d{4}-\d{2}-\d{2}$/)]],
      trainArrivalTime: ['', [Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)]],
      trainRideDuration: ['', [Validators.required, Validators.pattern(/^\d{1,2}h\s?\d{0,2}m?|\d{1,2}h$/)]],
      trainRidePrice: ['', [Validators.required, Validators.min(0), Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
    });

    console.log('Train form initialized with controls:', Object.keys(this.trainForm.controls));
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.trainForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }

  onSubmit(): void {
    if (this.trainForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';

      // Get station IDs from form (these come from StationSearchComponent)
      const departureStationId = this.trainForm.value.trainDepartureStation;
      const arrivalStationId = this.trainForm.value.trainArrivalStation;

      // Get station objects to create the TrainDetails properly
      this.getStationObjects(departureStationId, arrivalStationId);
    } else {
      this.trainForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }

  /**
   * Fetch station objects by their IDs and create the TrainDetails
   */
  private getStationObjects(departureStationId: string, arrivalStationId: string): void {
    console.log('🔍 Fetching stations with IDs:', { departureStationId, arrivalStationId });

    // Fetch both stations in parallel
    const departure$ = this.stationService.getTrainStationById(departureStationId);
    const arrival$ = this.stationService.getTrainStationById(arrivalStationId);

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

        // Create the TrainDetails object with full station data (matches Java model)
        const trainDetailsData: TrainDetails = {
          trainNumber: this.trainForm.value.trainNumber,
          trainLine: this.trainForm.value.trainLine,
          trainDepartureStation: {
            id: stations.departureStation.id,                              
            trainStationFullName: stations.departureStation.trainStationFullName,
            trainStationCode: stations.departureStation.trainStationCode,
            trainStationCityLocation: stations.departureStation.trainStationCityLocation
          },
          trainArrivalStation: {
            id: stations.arrivalStation.id,                                
            trainStationFullName: stations.arrivalStation.trainStationFullName,
            trainStationCode: stations.arrivalStation.trainStationCode,
            trainStationCityLocation: stations.arrivalStation.trainStationCityLocation
          },
          trainDepartureDate: this.trainForm.value.trainDepartureDate,
          trainDepartureTime: this.trainForm.value.trainDepartureTime,
          trainArrivalDate: this.trainForm.value.trainArrivalDate,
          trainArrivalTime: this.trainForm.value.trainArrivalTime,
          trainRideDuration: this.trainForm.value.trainRideDuration,
          trainRidePrice: this.trainForm.value.trainRidePrice.toString()
        };

        console.log('📤 Submitting train details data:', trainDetailsData);
        console.log('🚉 Departure station:', stations.departureStation);
        console.log('🚉 Arrival station:', stations.arrivalStation);

        this.trainDetailsService.addTrainDetails(trainDetailsData).subscribe({
          next: (response) => {
            console.log('✅ Train Details added successfully:', response);
            this.submitMessage = response;
            this.submitSuccess = true;
            this.isSubmitting = false;
            this.trainForm.reset();
          },
          error: (error) => {
            console.error('❌ Error adding train Details:', error);
            this.submitMessage = 'Failed to add train Details. Please try again.';
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