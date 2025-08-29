import { Component, OnInit} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Client, ClientRegistration, ClientLogin } from '../../services/client-service';


@Component({
  selector: 'app-client-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-form.html',
  styleUrls: [ './client-form.css', '../shared/form-styles.css' ],
})
export class ClientFormComponent implements OnInit {

// Form for client registration
  clientForm: FormGroup;
  
  // Current client data (after login or registration)
  currentClient: Client | null = null;
  
  // UI state management
  isLoading = false;
  isEditMode = false;
  
  constructor(private formBuilder: FormBuilder) {
    // Initialize the reactive form with validation rules that match your Java model
    this.clientForm = this.formBuilder.group({
      username: ['', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9_.-]+$/)
      ]],
      email: ['', [
        Validators.required,
        Validators.email,
        Validators.maxLength(100)
      ]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/)
      ]],
      firstName: ['', [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z\s'-]+$/)
      ]],
      lastName: ['', [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z\s'-]+$/)
      ]],
      phone: ['', [
        Validators.required,
        Validators.pattern(/^[+]?[1-9]\d{1,14}$/)
      ]],
      address: ['', [
        Validators.minLength(10),
        Validators.maxLength(200)
      ]]
    });
  }

  ngOnInit(): void {
    // Component initialization
  }

  /**
   * Handle form submission for client registration
   */
  onSubmit(): void {
    if (this.clientForm.valid) {
      this.isLoading = true;
      
      const clientData: ClientRegistration = this.clientForm.value;
      
      // Here you would typically call a service to send data to your Spring Boot backend
      console.log('Registering client:', clientData);
      
      // Simulate API call
      setTimeout(() => {
        this.isLoading = false;
        // Handle success/error responses here
      }, 2000);
    } else {
      // Mark all fields as touched to show validation errors
      this.markFormGroupTouched(this.clientForm);
    }
  }

  /**
   * Toggle between view and edit modes
   */
  toggleEditMode(): void {
    this.isEditMode = !this.isEditMode;
    
    if (this.isEditMode && this.currentClient) {
      // Populate form with current client data for editing
      this.clientForm.patchValue(this.currentClient);
    }
  }

  /**
   * Get error message for a specific form field
   */
  getFieldError(fieldName: string): string {
    const field = this.clientForm.get(fieldName);
    
    if (field?.errors && field.touched) {
      if (field.errors['required']) {
        return `${fieldName} is required`;
      }
      if (field.errors['minlength']) {
        return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      }
      if (field.errors['maxlength']) {
        return `${fieldName} must not exceed ${field.errors['maxlength'].requiredLength} characters`;
      }
      if (field.errors['email']) {
        return 'Please enter a valid email address';
      }
      if (field.errors['pattern']) {
        return this.getPatternErrorMessage(fieldName);
      }
    }
    
    return '';
  }

  /**
   * Get specific pattern error messages
   */
  private getPatternErrorMessage(fieldName: string): string {
    switch (fieldName) {
      case 'username':
        return 'Username can only contain letters, numbers, underscore, period, and dash';
      case 'password':
        return 'Password must contain at least one uppercase, lowercase, number, and special character';
      case 'firstName':
      case 'lastName':
        return 'Name can only contain letters, spaces, apostrophes, and hyphens';
      case 'phone':
        return 'Phone number must be in valid international format (e.g., +1234567890)';
      default:
        return 'Invalid format';
    }
  }

  /**
   * Mark all form fields as touched to trigger validation display
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(field => {
      const control = formGroup.get(field);
      control?.markAsTouched({ onlySelf: true });
    });
  }

  /**
   * Check if a field has errors and is touched
   */
  hasFieldError(fieldName: string): boolean {
    const field = this.clientForm.get(fieldName);
    return !!(field?.errors && field.touched);
  }

  /**
   * Reset the form to its initial state
   */
  resetForm(): void {
    this.clientForm.reset();
    this.isEditMode = false;
  }
}
