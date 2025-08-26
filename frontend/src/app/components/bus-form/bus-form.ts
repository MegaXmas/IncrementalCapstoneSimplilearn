import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { DurationDropdownComponent } from '../shared/duration-dropdown/duration-dropdown';
import { BusDetailsService, BusDetails } from '../../services/bus-details-service';
import { StationSearchComponent } from "../shared/station-search/station-search";
@Component({
  selector: 'app-bus-form',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DateDropdownComponent,
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
    private busDetailsService: BusDetailsService  
  ) {}
 
  ngOnInit(): void {
    this.busForm = this.fb.group({
      busNumber: ['', Validators.required],
      busLine: ['', Validators.required],
      busDepartureDetails: ['', Validators.required],
      busArrivalDetails: ['', Validators.required],
      busDepartureDate: ['', Validators.required, Validators.pattern(/\d{4}-\d{2}-\d{2}$/)],
      busDepartureTime: ['', Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)],
      busArrivalDate: ['', Validators.required, Validators.pattern(/\d{4}-\d{2}-\d{2}$/)],
      busArrivalTime: ['', Validators.required, Validators.pattern(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/)],
      busRideDuration: ['', Validators.required, Validators.pattern(/^\d{1,2}h\s?\d{0,2}m?|\d{1,2}h$/)],
      busRidePrice: ['', [Validators.required, Validators.min(0), Validators.pattern(/^\d+(\.\d{1,2})??$/)]],
    });
    console.log('Initial form state:', {
      dirty: this.busForm.dirty,
      pristine: this.busForm.pristine,
      valid: this.busForm.valid
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.busForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }


  onSubmit(): void {
    if (this.busForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';
    
      const busDetailsData: BusDetails = {
        busNumber: this.busForm.value.busNumber,
        busLine: this.busForm.value.busLine,
        busDepartureStation: this.busForm.value.busDepartureDetails,
        busArrivalStation: this.busForm.value.busArrivalDetails,
        busDepartureDate: this.busForm.value.busDepartureDate,
        busDepartureTime: this.busForm.value.busDepartureTime,
        busArrivalDate: this.busForm.value.busArrivalDate,
        busArrivalTime: this.busForm.value.busArrivalTime,
        busRideDuration: this.busForm.value.busRideDuration,
        busRidePrice: parseFloat(this.busForm.value.busRidePrice)
      };

      console.log('Submitting bus Details data:', busDetailsData);

      this.busDetailsService.addBusDetails(busDetailsData).subscribe({
        next: (response) => {
          console.log('Bus Details added successfully:', response);
          this.submitMessage = response;
          this.submitSuccess = true;

          this.busForm.reset();
        },
        error: (error) => {
          console.error('Error adding bus Details:', error);
          this.submitMessage = 'Failed to add bus Details. Please try again.';
          this.submitSuccess = false;
          this.isSubmitting = false;
        },
      });
    } else {

      this.busForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }
}