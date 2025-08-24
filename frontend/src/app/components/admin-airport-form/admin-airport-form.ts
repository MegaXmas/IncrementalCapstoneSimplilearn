import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { AirportService, Airport } from '../../services/airport-service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
@Component({
  selector: 'app-admin-airport-form',
  templateUrl: './admin-airport-form.html',
  imports: [
    ReactiveFormsModule
  ],
  styleUrls: ['./admin-airport-form.css', '../shared/form-styles.css']
})
export class AdminAirportFormComponent implements OnInit {

  adminAirportForm!: FormGroup;
  isSubmitting = false;
  submitMessage = '';
  submitSuccess = false;

  constructor(
    private fb: FormBuilder,
    private airportService: AirportService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.adminAirportForm = this.fb.group({
      airportFullName: ['', [Validators.required, Validators.minLength(3)]],
      airportCode: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(4)]],
      airportLocationCity: ['', [Validators.required, Validators.minLength(2)]],
      airportLocationCountry: ['', [Validators.required, Validators.minLength(2)]],
      airportTimezone: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.adminAirportForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';

      // Create airport object from form values
      const airportData: Airport = {
        airportFullName: this.adminAirportForm.value.airportFullName,
        airportCode: this.adminAirportForm.value.airportCode.toUpperCase(), // Airport codes are typically uppercase
        airportLocationCity: this.adminAirportForm.value.airportLocationCity,
        airportLocationCountry: this.adminAirportForm.value.airportLocationCountry,
        airportTimezone: this.adminAirportForm.value.airportTimezone
      };

      console.log('Submitting airport data:', airportData);

      // Make HTTP POST request to SpringBoot backend
      this.airportService.addAirport(airportData).subscribe({
        next: (response) => {
          console.log('✓ Airport added successfully:', response);
          this.submitMessage = response; // "Airport added successfully"
          this.submitSuccess = true;
          this.isSubmitting = false;

          // Reset form after successful submission
          this.adminAirportForm.reset();
        },
        error: (error) => {
          console.error('✗ Error adding airport:', error);
          this.submitMessage = error.error || 'Failed to add airport. Please try again.';
          this.submitSuccess = false;
          this.isSubmitting = false;
        }
      });
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.adminAirportForm.controls).forEach(key => {
        this.adminAirportForm.get(key)?.markAsTouched();
      });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminAirportForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
