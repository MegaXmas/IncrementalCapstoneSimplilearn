import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {TrainStation, TrainStationService} from '../../services/train-station-service';

@Component({
  selector: 'app-admin-train-station-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-train-station-form.html',
  styleUrls: ['./admin-train-station-form.css', '../shared/form-styles.css'],
})
export class AdminTrainStationFormComponent implements OnInit {

  adminTrainStationForm!: FormGroup;
  isInvalid: boolean = false;
  isSubmitting = false;
  submitMessage = '';
  submitSuccess = false;

  constructor(
    private fb: FormBuilder,
    private trainStationService: TrainStationService,
  ) {}

  ngOnInit(): void {
    this.adminTrainStationForm = this.fb.group({
      trainStationFullName: ['', Validators.required],
      trainStationCode: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(6)]],
      trainStationCityLocation: ['', Validators.required],
    });

    console.log('Initial form state:', {
      dirty: this.adminTrainStationForm.dirty,
      pristine: this.adminTrainStationForm.pristine,
      valid: this.adminTrainStationForm.valid
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.adminTrainStationForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  onSubmit(): void {
    if (this.adminTrainStationForm.valid) {
      this.isSubmitting = true;
      this.submitMessage = '';

      const trainStationData: TrainStation = {
        trainStationFullName: this.adminTrainStationForm.value.trainStationFullName,
        trainStationCode: this.adminTrainStationForm.value.trainStationCode.toUpperCase(),
        trainStationCityLocation: this.adminTrainStationForm.value.trainStationCityLocation
      };

      console.log('Submitting train station data:', trainStationData);

      this.trainStationService.addTrainStation(trainStationData).subscribe({
        next: (response) => {
          console.log('Train station added successfully:', response);
          this.submitMessage = response;
          this.submitSuccess = true;
          this.isSubmitting = false;

          this.adminTrainStationForm.reset();
        },
        error: (error) => {
          console.error('Error adding train station:', error);
          this.submitMessage = 'Failed to add train station. Please try again.';
          this.submitSuccess = false;
          this.isSubmitting = false;
        },
      });
    } else {
      this.adminTrainStationForm.markAllAsTouched();
      this.submitMessage = 'Please fill in all required fields correctly.';
      this.submitSuccess = false;
    }
  }
}
