import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';

@Component({
  selector: 'app-user-bus-booking',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
  ],
  templateUrl: './user-bus-booking.html',
  styleUrls: ['./user-bus-booking.css','../shared/form-styles.css'],
})
export class UserBusBookingComponent {
  userBusBookingForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}

  isFieldInvalid(fieldName: string): boolean {
    const field = this.userBusBookingForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.userBusBookingForm.valid) {
      console.log('Form submitted:', this.userBusBookingForm.value);
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
