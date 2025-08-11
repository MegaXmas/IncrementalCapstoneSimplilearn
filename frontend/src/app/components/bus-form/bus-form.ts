import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { DurationDropdownComponent } from '../shared/duration-dropdown/duration-dropdown';

@Component({
  selector: 'app-bus-form',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
    TimeDropdownComponent, 
    DurationDropdownComponent
  ],
  templateUrl: './bus-form.html',
  styleUrls: ['./bus-form.css','../shared/form-styles.css'],
})
export class BusFormComponent implements OnInit {
  busForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}
 
  ngOnInit(): void {
    this.busForm = this.fb.group({
      busNumber: ['', Validators.required],
      busLine: ['', Validators.required],
      busDepartureStation: ['', Validators.required],
      busArrivalStation: ['', Validators.required],
      busDepartureDate: ['', Validators.required],
      busDepartureTime: ['', Validators.required],
      busArrivalDate: ['', Validators.required],
      busArrivalTime: ['', Validators.required],
      busRideDuration: ['', Validators.required],
      busRidePrice: ['', [Validators.required, Validators.min(0)]],
    });
console.log('Initial form state:', {
      dirty: this.busForm.dirty,
      pristine: this.busForm.pristine,
      valid: this.busForm.valid
    });
  }

isFieldInvalid(fieldName: string): boolean {
    const field = this.busForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.busForm.valid) {
      console.log('Form submitted:', this.busForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.busForm.controls).forEach(key => {
        this.busForm.get(key)?.markAsTouched();
      });
    }
  }
}