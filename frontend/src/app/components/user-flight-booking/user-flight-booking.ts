import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';

@Component({
  selector: 'app-user-flight-booking',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
  ],
  templateUrl: './user-flight-booking.html',
  styleUrls: ['./user-flight-booking.css','../shared/form-styles.css'],
})
export class UserFlightBookingComponent {
  userFlightBookingForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}


  isFieldInvalid(fieldName: string): boolean {
    const field = this.userFlightBookingForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.userFlightBookingForm.valid) {
      console.log('Form submitted:', this.userFlightBookingForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.userFlightBookingForm.controls).forEach(key => {
        this.userFlightBookingForm.get(key)?.markAsTouched();
      });
    }
  }
}
