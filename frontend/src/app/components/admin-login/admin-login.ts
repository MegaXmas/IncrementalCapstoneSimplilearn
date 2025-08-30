import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AdminUser, AdminLogin, AdminService } from '../../services/admin-service';

@Component({
  selector: 'app-admin-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-login.html',
  styleUrls: ['./admin-login.css', '../shared/form-styles.css']
})
export class AdminLoginComponent implements OnInit {

  adminLoginForm: FormGroup;
  
  currentAdmin: AdminUser | null = null;
  isLoading = false;
  showPassword = false;

  successMessage: string = '';
  errorMessage: string = '';
  
  constructor(
    private formBuilder: FormBuilder,
    private adminService: AdminService
  ) {
    this.adminLoginForm = this.formBuilder.group({
      adminUsername: ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50)
      ]],
      adminPassword: ['', [
        Validators.required,
        Validators.pattern(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/)
      ]]
    });
  }

  ngOnInit(): void {
    if (this.adminService.isLoggedIn()) {
      this.loadCurrentAdminProfile();
    }
  }

  /**
   * Handle admin login
   */
  onSubmit(): void {
    if (this.adminLoginForm.valid) {
      this.isLoading = true;
      this.clearMessages();
      
      const loginData: AdminLogin = this.adminLoginForm.value;
      
      this.adminService.login(loginData).subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success && response.data) {
            // Store the JWT token
            this.adminService.storeToken(response.data.token);
            // Set current admin data
            this.currentAdmin = response.data.admin;
            this.successMessage = response.message || 'Login successful';
            this.resetForm();
          } else {
            this.errorMessage = response.message || 'Login failed';
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Login failed due to server error';
          console.error('Admin login error:', error);
        }
      });
    } else {
      // Mark all fields as touched to show validation errors
      this.markFormGroupTouched(this.adminLoginForm);
    }
  }

  /**
   * Load current admin profile from backend
   */
  private loadCurrentAdminProfile(): void {
    this.adminService.getProfile().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.currentAdmin = response.data;
        } else {
          // Token might be invalid, remove it
          this.adminService.removeToken();
          this.currentAdmin = null;
        }
      },
      error: (error) => {
        console.error('Admin profile loading error:', error);
        // Token might be invalid, remove it
        this.adminService.removeToken();
        this.currentAdmin = null;
      }
    });
  }

  /**
   * Handle admin logout
   */
  onLogout(): void {
    this.adminService.removeToken();
    this.currentAdmin = null;
    this.resetForm();
    this.clearMessages();
  }

  /**
   * Toggle password visibility
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * Clear success and error messages
   */
  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  /**
   * Get error message for a specific form field
   */
  getFieldError(fieldName: string): string {
    const field = this.adminLoginForm.get(fieldName);
    
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
      case 'adminPassword':
        return 'Password must contain at least 8 characters with uppercase, lowercase, and number';
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
    const field = this.adminLoginForm.get(fieldName);
    return !!(field?.errors && field.touched);
  }

  /**
   * Reset the form to its initial state
   */
  resetForm(): void {
    this.adminLoginForm.reset();
  }

  /**
   * Getter for easy access to form controls in template
   */
  get adminUsername() {
    return this.adminLoginForm.get('adminUsername');
  }

  get adminPassword() {
    return this.adminLoginForm.get('adminPassword');
  }
}