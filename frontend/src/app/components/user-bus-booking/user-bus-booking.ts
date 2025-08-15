import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DateDropdownComponent } from '../shared/date-dropdown/date-dropdown';

@Component({
  selector: 'app-user-bus-booking',
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    DateDropdownComponent,
  ],
  templateUrl: './user-bus-booking.html',
  styleUrls: ['./user-bus-booking.css','../shared/form-styles.css'],
})
export class UserBusBookingComponent implements OnInit {
  userBusBookingForm!: FormGroup;
  isInvalid: boolean = false;
  
  // Properties for tracking inputs
  showInputTracking: boolean = false;
  lastKeyPressed: string = '';
  currentInputs: any = {
    busDepartureStation: '',
    busArrivalStation: '',
    busDepartureDate: ''
  };

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    // Initialize the reactive form
    this.userBusBookingForm = this.fb.group({
      busDepartureStation: ['', Validators.required],
      busArrivalStation: ['', Validators.required],
      busDepartureDate: ['', Validators.required]
    });

    // Subscribe to form value changes for reactive tracking
    this.userBusBookingForm.valueChanges.subscribe(values => {
      this.currentInputs = { ...values };
      console.log('Form values changed:', values);
    });
  }

  /**
   * Handle keyup events on form fields
   * @param fieldName - The name of the form field
   * @param event - The keyup event
   */
  onFieldKeyup(fieldName: string, event: any): void {
    const target = event.target;
    const value = target.value;
    const keyPressed = event.key;
    
    // Update tracking variables
    this.lastKeyPressed = keyPressed;
    this.currentInputs[fieldName] = value;
    
    // Log the keyup event details
    console.log('Keyup Event Details:', {
      field: fieldName,
      value: value,
      keyPressed: keyPressed,
      timestamp: new Date().toLocaleTimeString()
    });

    // You can add specific logic based on the field or key pressed
    if (keyPressed === 'Enter') {
      console.log(`Enter pressed in ${fieldName} field`);
      // Could trigger form submission or move to next field
    }

    // Update form validation status
    this.validateField(fieldName, value);
  }

  /**
   * Handle change events (for dropdowns)
   * @param fieldName - The name of the form field
   * @param event - The change event
   */
  onFieldChange(fieldName: string, event: any): void {
    const value = event.target.value;
    this.currentInputs[fieldName] = value;
    
    console.log('Field Changed:', {
      field: fieldName,
      value: value,
      timestamp: new Date().toLocaleTimeString()
    });
  }

  /**
   * Validate individual field and provide feedback
   * @param fieldName - The name of the field to validate
   * @param value - The current value of the field
   */
  validateField(fieldName: string, value: string): void {
    const field = this.userBusBookingForm.get(fieldName);
    
    if (field) {
      // Mark field as touched to trigger validation
      field.markAsTouched();
      
      // Custom validation feedback
      if (!value || value.trim() === '') {
        console.log(`${fieldName} is empty - validation failed`);
      } else {
        console.log(`${fieldName} has valid input: ${value}`);
      }
    }
  }

  /**
   * Check if a specific field is invalid
   * @param fieldName - The name of the field to check
   * @returns boolean indicating if field is invalid
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.userBusBookingForm.get(fieldName);
    return (field?.invalid ?? false) && (field?.touched ?? false);
  }

  /**
   * Toggle the input tracking display
   */
  toggleInputTracking(): void {
    this.showInputTracking = !this.showInputTracking;
    console.log('Input tracking display:', this.showInputTracking ? 'shown' : 'hidden');
  }

  /**
   * Handle form reset
   */
  onFormReset(): void {
    this.userBusBookingForm.reset();
    this.currentInputs = {
      busDepartureStation: '',
      busArrivalStation: '',
      busDepartureDate: ''
    };
    this.lastKeyPressed = '';
    console.log('Form reset - all tracking cleared');
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.userBusBookingForm.valid) {
      console.log('Form submitted successfully:', this.userBusBookingForm.value);
      console.log('Final input tracking data:', this.currentInputs);
      // Handle form submission here
    } else {
      console.log('Form is invalid - cannot submit');
      // Mark all fields as touched to show validation errors
      Object.keys(this.userBusBookingForm.controls).forEach(key => {
        this.userBusBookingForm.get(key)?.markAsTouched();
      });
    }
  }
}