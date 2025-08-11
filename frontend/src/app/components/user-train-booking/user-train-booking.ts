import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';

@Component({
  selector: 'app-user-train-booking',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
  ],
  templateUrl: './user-train-booking.html',
  styleUrls: ['./user-train-booking.css','../shared/form-styles.css'],
})
export class UserTrainBookingComponent {
  userTrainBookingForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}


  isFieldInvalid(fieldName: string): boolean {
    const field = this.userTrainBookingForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.userTrainBookingForm.valid) {
      console.log('Form submitted:', this.userTrainBookingForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.userTrainBookingForm.controls).forEach(key => {
        this.userTrainBookingForm.get(key)?.markAsTouched();
      });
    }
  }
}
