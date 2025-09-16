import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {CommonModule} from '@angular/common';
import {Airport, AirportService} from '../../services/airport-service';

@Component({
  selector: 'app-admin-airport-form',
  standalone: true,
  templateUrl: './admin-airport-form.html',
  imports: [
    ReactiveFormsModule,
    HttpClientModule,
    CommonModule
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
    private airportService: AirportService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  private initializeForm(): void {
    this.adminAirportForm = this.fb.group({
      airportFullName: ['', [Validators.required, Validators.minLength(3)]],
      airportCode: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(4)]],
      airportCityLocation: ['', [Validators.required, Validators.minLength(2)]],
      airportCountryLocation: ['', [Validators.required, Validators.minLength(2)]],
      airportTimezone: ['', [Validators.required]]
    });
  }

  /**
   * Check if a form field is invalid and has been touched or is dirty
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminAirportForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  onSubmit(): void {
    if (this.adminAirportForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';

      // Create airport object from form values
      const airportData: Airport = {
        airportFullName: this.adminAirportForm.value.airportFullName,
        airportCode: this.adminAirportForm.value.airportCode.toUpperCase(),
        airportCityLocation: this.adminAirportForm.value.airportCityLocation,
        airportCountryLocation: this.adminAirportForm.value.airportCountryLocation,
        airportTimezone: this.adminAirportForm.value.airportTimezone
      };

      console.log('Submitting airport data:', airportData);

      // Make HTTP POST request to SpringBoot backend
      this.airportService.addAirport(airportData).subscribe({
        next: (response) => {
          console.log('✓ Airport added successfully:', response);
          this.submitMessage = 'Airport added successfully!';
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
      // Mark all fields as touched to show validation errors
      this.adminAirportForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }
}
