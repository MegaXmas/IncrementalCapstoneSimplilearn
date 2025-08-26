import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BusStationService, BusStation } from '../../services/bus-station-service';

@Component({
  selector: 'app-admin-bus-station-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-bus-station-form.html',
  styleUrls: ['./admin-bus-station-form.css','../shared/form-styles.css'],
})
export class AdminBusStationFormComponent implements OnInit {

  adminBusStationForm!: FormGroup;
  isInvalid: boolean = false;
  isSubmitting = false;
  submitMessage = '';
  submitSuccess = false;

    constructor(
      private fb: FormBuilder,
      private busStationService: BusStationService,
    ) {}

  ngOnInit(): void {
    this.adminBusStationForm = this.fb.group({
      busStationFullName: ['', Validators.required],
      busStationCode: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(6)]],
      busStationCityLocation: ['', Validators.required],
    });

    console.log('Initial form state:', {
      dirty: this.adminBusStationForm.dirty,
      pristine: this.adminBusStationForm.pristine,
      valid: this.adminBusStationForm.valid
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminBusStationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched))
  }

  onSubmit(): void {
    if (this.adminBusStationForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';
    
      const busStationData: BusStation = {
        busStationFullName: this.adminBusStationForm.value.busStationFullName,
        busStationCode: this.adminBusStationForm.value.busStationCode.toUpperCase(),
        busStationtCityLocation: this.adminBusStationForm.value.busStationCityLocation
      };

      console.log('Submitting bus station data:', busStationData);

      this.busStationService.addBusStation(busStationData).subscribe({
        next: (response) => {
          console.log('Bus station added successfully:', response);
          this.submitMessage = response;
          this.submitSuccess = true;

          this.adminBusStationForm.reset();
        },
        error: (error) => {
          console.error('Error adding bus station:', error);
          this.submitMessage = 'Failed to add bus station. Please try again.';
          this.submitSuccess = false;
          this.isSubmitting = false;
        },
      });
    } else {

      this.adminBusStationForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }
}