import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateInputComponent } from '../shared/date-dropdown/date-input';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { ClientService } from '../../services/client-service';
@Component({
  selector: 'app-user-train-booking',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateInputComponent,
    TimeDropdownComponent
  ],
  templateUrl: './user-train-booking.html',
  styleUrls: ['./user-train-booking.css','../shared/form-styles.css'],
})
export class UserTrainBookingComponent {
  userTrainBookingForm!: FormGroup;
  isInvalid: boolean = false;
  isLoggedIn: boolean = false;

  constructor(
    private fb: FormBuilder,
    private clientService: ClientService) {}


  isFieldInvalid(fieldName: string): boolean {
    this.isLoggedIn = this.clientService.getToken() !== null;
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
