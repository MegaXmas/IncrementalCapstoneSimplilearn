import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-bus-station-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-bus-station-form.html',
  styleUrls: ['./admin-bus-station-form.css','../shared/form-styles.css'],
})
export class AdminBusStationFormComponent implements OnInit {
  adminBusStationForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.adminBusStationForm = this.fb.group({
      busStationFullName: ['', Validators.required],
      busStationCode: ['', [Validators.required, Validators.pattern(/^[A-Z]{3}$/)]],
      busStationLocationCity: ['', Validators.required],
    });

    console.log('Initial form state:', {
      dirty: this.adminBusStationForm.dirty,
      pristine: this.adminBusStationForm.pristine,
      valid: this.adminBusStationForm.valid
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminBusStationForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.adminBusStationForm.valid) {
      console.log('Form submitted:', this.adminBusStationForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.adminBusStationForm.controls).forEach(key => {
        this.adminBusStationForm.get(key)?.markAsTouched();
      });
    }
  }

}
