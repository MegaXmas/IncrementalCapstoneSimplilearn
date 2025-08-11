import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { DurationDropdownComponent } from '../shared/duration-dropdown/duration-dropdown';

@Component({
  selector: 'app-flight-form',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
    TimeDropdownComponent, 
    DurationDropdownComponent
  ],
  templateUrl: './flight-form.html',
  styleUrls: ['./flight-form.css','../shared/form-styles.css'],
})
export class FlightFormComponent implements OnInit {
  flightForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.flightForm = this.fb.group({
      flightNumber: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      flightAirline: ['', [Validators.required, Validators.minLength(2)]],
      flightOrigin: ['', [Validators.required, Validators.minLength(2)]],
      flightDestination: ['', [Validators.required, Validators.minLength(2)]],
      flightDepartureDate: ['', Validators.required],
      flightArrivalDate: ['', Validators.required],
      flightDepartureTime: ['', Validators.required],
      flightArrivalTime: ['', Validators.required],
      flightTravelTime: ['', [Validators.required, Validators.min(1)]],
      flightPrice: ['', [Validators.required, Validators.min(0)]],
    });

    console.log('Initial form state:', {
      dirty: this.flightForm.dirty,
      pristine: this.flightForm.pristine,
      valid: this.flightForm.valid
    });
  }

isFieldInvalid(fieldName: string): boolean {
    const field = this.flightForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.flightForm.valid) {
      console.log('Form submitted:', this.flightForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.flightForm.controls).forEach(key => {
        this.flightForm.get(key)?.markAsTouched();
      });
    }
  }
}
