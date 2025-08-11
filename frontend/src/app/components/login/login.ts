import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})

export class LoginComponent implements OnInit {
  showPassword = false;
  adminLoginForm!: FormGroup;


  constructor(private fb: FormBuilder) {}

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
      console.log('Password visibility toggled. showPassword is now:', this.showPassword);
      console.log('Input type should be:', this.showPassword ? 'text' : 'password');
  }

  ngOnInit() {

    this.adminLoginForm = this.fb.group({
      adminUsername: ['', [Validators.required, Validators.minLength(2)]],
      adminPassword: ['', [Validators.required, Validators.pattern("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}")]]
  });

    console.log('Initial form state:', {
      dirty: this.adminLoginForm.dirty,
      pristine: this.adminLoginForm.pristine,
      valid: this.adminLoginForm.valid,
      nameControl: {
        invalid: this.adminLoginForm.get('adminUsername')?.invalid,
        dirty: this.adminLoginForm.get('adminUsername')?.dirty,
        touched: this.adminLoginForm.get('adminUsername')?.touched
      }
    });
  }

  onSubmit(): void {
    console.log('form submitted', 
      this.adminLoginForm.value
    )
  }
}
