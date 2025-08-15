import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';
import { StationSearchComponent } from '../shared/station-search/station-search';

@Component({
  selector: 'app-user-bus-booking',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
    StationSearchComponent,
  ],
  templateUrl: './user-bus-booking.html',
  styleUrls: ['./user-bus-booking.css','../shared/form-styles.css'],
})
export class UserBusBookingComponent implements OnInit {
  userBusBookingForm!: FormGroup;
  
  // Simple tracking properties
  showInputTracking: boolean = false;
  currentInputs: any = {
    busDepartureStation: '',
    busArrivalStation: '',
    busDepartureDate: ''
  };

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    // Create the reactive form
    this.userBusBookingForm = this.fb.group({
      busDepartureStation: ['', Validators.required],
      busArrivalStation: ['', Validators.required],
      busDepartureDate: ['', Validators.required]
    });

    // Track form changes
    this.userBusBookingForm.valueChanges.subscribe(values => {
      this.currentInputs = { ...values };
      console.log('Form values changed:', values);
    });
  }

  /**
   * Check if a field is invalid and has been touched
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.userBusBookingForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  /**
   * Toggle the input tracking display
   */
  toggleInputTracking(): void {
    this.showInputTracking = !this.showInputTracking;
  }

  /**
   * Handle form reset
   */
  onFormReset(): void {
    this.userBusBookingForm.reset();
    this.currentInputs = {
      busDepartureStation: '',
      busArrivalStation: '',
      busDepartureDate: ''
    };
    console.log('Form reset');
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.userBusBookingForm.valid) {
      console.log('Form submitted successfully:', this.userBusBookingForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.userBusBookingForm.controls).forEach(key => {
        this.userBusBookingForm.get(key)?.markAsTouched();
      });
    }
  }
}