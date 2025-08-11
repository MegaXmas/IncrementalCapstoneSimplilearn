import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent implements OnInit {


  /**
   * Component properties
   * isLoggedIn: boolean to track user login status
   * userName: string to store the logged-in user's name
   */
  isLoggedIn: boolean = false;
  userName: string = '';

  
  /**
   * Constructor to inject dependencies
   * @param router - Angular Router for navigation
   */
  constructor(private router: Router) {}

  /**
   * Lifecycle hook that is called after component initialization
   * Used to check login status when component starts
   */
  ngOnInit(): void {
    // Check if user is logged in when component starts
    this.checkLoginStatus();
  }

  /**
   * Check if user is currently logged in
   * In a real app, this would check authentication service
   */
  checkLoginStatus(): void {
    // For demo purposes, check localStorage
    // In real app, use proper authentication service
    const token = localStorage.getItem('authToken');
    const user = localStorage.getItem('userName');
    
    if (token && user) {
      this.isLoggedIn = true;
      this.userName = user;
    } else {
      this.isLoggedIn = false;
      this.userName = '';
    }
  }

  /**
   * Handle user logout
   * Clears user data and redirects to home
   */
  // onLogout(): void {
  //   // Clear user authentication data
  //   localStorage.removeItem('authToken');
  //   localStorage.removeItem('userName');
    
  //   // Update component state
  //   this.isLoggedIn = false;
  //   this.userName = '';
    
  //   // Redirect to home page
  //   this.router.navigate(['/home']);
    
  //   // Optional: Show success message
  //   alert('You have been logged out successfully!');
  // }

  /**
   * Handle login button click
   * Navigates to login page
   */
  onLogin(): void {
    // Navigate to login page
    this.router.navigate(['/login']);
  }


  /**
   *Dropdown menu state
   */
  isDropdownOpen = false;

  /**
   * Toggle the dropdown menu visibility
   */
  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  /**
   * Handle logout button click
   */
  logout() {
    // Handle logout logic
    console.log('Logging out...');
  }

  /**
   * Navigate to the profile page
   */
  goToProfile() {
    // Navigate to profile
    console.log('Going to profile...');
  }

}