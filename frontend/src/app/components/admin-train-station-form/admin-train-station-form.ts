import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-train-station-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-train-station-form.html',
  styleUrls: ['./admin-train-station-form.css', '../shared/form-styles.css'],
})
export class AdminTrainStationFormComponent implements OnInit {
  adminTrainStationForm!: FormGroup;
  isInvalid: boolean = false;

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    this.adminTrainStationForm = this.fb.group({
      trainStationFullName: ['', Validators.required],
      trainStationCode: ['', [Validators.required, Validators.pattern(/^[A-Z]{3}$/)]],
      trainStationLocationCity: ['', Validators.required],
    });

    console.log('Initial form state:', {
      dirty: this.adminTrainStationForm.dirty,
      pristine: this.adminTrainStationForm.pristine,
      valid: this.adminTrainStationForm.valid
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminTrainStationForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  onSubmit(): void {
    if (this.adminTrainStationForm.valid) {
      console.log('Form submitted:', this.adminTrainStationForm.value);
      // Handle form submission here
    } else {
      console.log('Form is invalid');
      // Mark all fields as touched to show validation errors
      Object.keys(this.adminTrainStationForm.controls).forEach(key => {
        this.adminTrainStationForm.get(key)?.markAsTouched();
      });
    }
  }

}
