import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-airport-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-airport-form.html',
  styleUrls: ['./admin-airport-form.css','../shared/form-styles.css'],
})
export class AdminAirportFormComponent implements OnInit {
  adminAirportForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.adminAirportForm = this.fb.group({
      airportFullName: ['', Validators.required],
      airportCode: ['', [Validators.required, Validators.pattern(/^[A-Z]{3}$/)]],
      airportLocationCity: ['', Validators.required],
      airportLocationCountry: ['', Validators.required],
      airportTimezone: ['', Validators.required], //create validator pattern for timezone
    });
    console.log('Initial form state:', {
      dirty: this.adminAirportForm.dirty,
      pristine: this.adminAirportForm.pristine,
      valid: this.adminAirportForm.valid
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminAirportForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.adminAirportForm.valid) {
      console.log('Form submitted:', this.adminAirportForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.adminAirportForm.controls).forEach(key => {
        this.adminAirportForm.get(key)?.markAsTouched();
      });
    }
  }
}
