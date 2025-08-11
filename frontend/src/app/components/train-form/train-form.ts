import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';
import { TimeDropdownComponent } from '../shared/time-dropdown/time-dropdown';
import { DurationDropdownComponent } from '../shared/duration-dropdown/duration-dropdown';

@Component({
  selector: 'app-train-form',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
    TimeDropdownComponent, 
    DurationDropdownComponent
  ],
  templateUrl: './train-form.html',
  styleUrls: ['./train-form.css','../shared/form-styles.css'],
})
export class TrainFormComponent implements OnInit {
  trainForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}
 
  ngOnInit(): void {
    this.trainForm = this.fb.group({
      trainNumber: ['', Validators.required],
      trainLine: ['', Validators.required],
      trainDepartureStation: ['', Validators.required],
      trainArrivalStation: ['', Validators.required],
      trainDepartureDate: ['', Validators.required],
      trainDepartureTime: ['', Validators.required],
      trainArrivalDate: ['', Validators.required],
      trainArrivalTime: ['', Validators.required],
      trainRideDuration: ['', Validators.required],
      trainRidePrice: ['', [Validators.required, Validators.min(0)]],
    });
console.log('Initial form state:', {
      dirty: this.trainForm.dirty,
      pristine: this.trainForm.pristine,
      valid: this.trainForm.valid
    });
  }

isFieldInvalid(fieldName: string): boolean {
    const field = this.trainForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.trainForm.valid) {
      console.log('Form submitted:', this.trainForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.trainForm.controls).forEach(key => {
        this.trainForm.get(key)?.markAsTouched();
      });
    }
  }
}