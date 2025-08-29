import { Component, OnInit} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Client, ClientRegistration, ClientLogin, ClientService } from '../../services/client-service';


@Component({
  selector: 'app-client-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './client-form.html',
  styleUrls: [ './client-form.css', '../shared/form-styles.css' ],
})
export class ClientFormComponent implements OnInit {

// Form for client registration
  clientForm: FormGroup;
  loginForm: FormGroup;
  isInvalid: boolean = false;
  
  // Current client data (after login or registration)
  currentClient: Client | null = null;
  
  // UI state management
  isLoading = false;
  isEditMode = false;
  isLoginMode = true;

  // Success/error messages
  successMessage: string = '';
  errorMessage: string = '';
  
  constructor(
    private formBuilder: FormBuilder,
    private clientService: ClientService
  ) {
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
// Initialize login form
    this.loginForm = this.formBuilder.group({
      usernameOrEmail: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    // Check if user is already logged in and load their profile
    if (this.clientService.isLoggedIn()) {
      this.loadCurrentUserProfile();
    }
  }

onSubmit(): void {
    if (this.clientForm.valid) {
      this.isLoading = true;
      this.clearMessages();
      
      const clientData: ClientRegistration = this.clientForm.value;
      
      // Call the backend registration API
      this.clientService.register(clientData).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success && response.data) {
            this.successMessage = response.message;
            this.resetForm();
            // Optionally redirect to login or automatically log them in
          } else {
            this.errorMessage = response.message || 'Registration failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Registration failed due to server error';
          console.error('Registration error:', error);
        }
      });
    } else {
      // Mark all fields as touched to show validation errors
      this.markFormGroupTouched(this.clientForm);
    }
  }

  /**
   * Handle user login
   */
  onLogin(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.clearMessages();
      
      const loginData: ClientLogin = this.loginForm.value;
      
      this.clientService.login(loginData).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success && response.data) {
            // Store the JWT token
            this.clientService.storeToken(response.data.token);
            // Set current client data
            this.currentClient = response.data.client;
            this.successMessage = response.message || 'Login successful';
            // Switch to profile view
            this.isEditMode = false;
          } else {
            this.errorMessage = response.message || 'Login failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Login failed due to server error';
          console.error('Login error:', error);
        }
      });
    }
  }

  /**
   * Load current user profile from backend
   */
  private loadCurrentUserProfile(): void {
    this.clientService.getProfile().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.currentClient = response.data;
        } else {
          // Token might be invalid, remove it
          this.clientService.removeToken();
          this.currentClient = null;
        }
      },
      error: (error) => {
        console.error('Profile loading error:', error);
        // Token might be invalid, remove it
        this.clientService.removeToken();
        this.currentClient = null;
      }
    });
  }

  /**
   * Handle profile updates
   */
  onProfileUpdate(): void {
    if (this.clientForm.valid && this.currentClient) {
      this.isLoading = true;
      this.clearMessages();
      
      const updatedClient: Client = {
        ...this.currentClient,
        ...this.clientForm.value
      };
      
      this.clientService.updateProfile(updatedClient).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success && response.data) {
            this.currentClient = response.data;
            this.successMessage = 'Profile updated successfully';
            this.isEditMode = false;
          } else {
            this.errorMessage = response.message || 'Profile update failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Profile update failed due to server error';
          console.error('Profile update error:', error);
        }
      });
    } else {
      this.markFormGroupTouched(this.clientForm);
    }
  }

  /**
   * Handle user logout
   */
  onLogout(): void {
    this.clientService.removeToken();
    this.currentClient = null;
    this.isEditMode = false;
    this.resetForm();
    this.clearMessages();
  }

  /**
   * Clear success and error messages
   */
  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
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
  isFieldInvalid(fieldName: string): boolean {
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