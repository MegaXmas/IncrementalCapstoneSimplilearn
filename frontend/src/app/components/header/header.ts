import {Component, OnInit} from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {ClientService} from '../../services/client-service';
import {AdminService} from '../../services/admin-service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css']
})
export class HeaderComponent implements OnInit {

  /**
   * Component properties for client authentication
   */
  isClientLoggedIn: boolean = false;
  clientUsername: string = '';

  /**
   * Component properties for admin authentication
   */
  isAdminLoggedIn: boolean = false;
  adminUsername: string = '';

  /**
   * Legacy properties for backward compatibility
   */
  get isLoggedIn(): boolean {
    return this.isClientLoggedIn || this.isAdminLoggedIn;
  }

  get userName(): string {
    return this.clientUsername || this.adminUsername || 'Guest';
  }

  /**
   * Constructor to inject dependencies
   */
  constructor(
    private router: Router,
    private clientService: ClientService,
    private adminService: AdminService
  ) {}

  /**
   * Lifecycle hook to initialize component
   */
  ngOnInit(): void {
    this.checkAuthenticationStatus();
  }

  /**
   * Check authentication status for both clients and admins
   */
  checkAuthenticationStatus(): void {
    // Check client authentication
    this.isClientLoggedIn = this.clientService.isLoggedIn();
    if (this.isClientLoggedIn) {
      this.clientUsername = this.clientService.getUsernameFromToken() || 'Client';
    }

    // Check admin authentication
    this.isAdminLoggedIn = this.adminService.isLoggedIn();
    if (this.isAdminLoggedIn) {
      this.adminUsername = this.adminService.getUsernameFromToken() || 'Admin';
    }
  }

  /**
   * Handle client logout
   */
  onClientLogout(event: Event): void {
    event.preventDefault();

    // Clear client authentication
    this.clientService.removeToken();
    this.isClientLoggedIn = false;
    this.clientUsername = '';

    // Navigate to home or login page
    this.router.navigate(['/client-form']);

    // Show logout confirmation
    alert('You have been logged out successfully!');
  }

  /**
   * Handle admin logout
   */
  onAdminLogout(event: Event): void {
    event.preventDefault();

    // Clear admin authentication
    this.adminService.removeToken();
    this.isAdminLoggedIn = false;
    this.adminUsername = '';

    // Navigate to admin login page
    this.router.navigate(['/admin-login']);

    // Show logout confirmation
    alert('Admin session ended successfully!');
  }

  /**
   * Legacy logout method for backward compatibility
   */
  onLogout(): void {
    if (this.isClientLoggedIn) {
      this.onClientLogout(new Event('click'));
    } else if (this.isAdminLoggedIn) {
      this.onAdminLogout(new Event('click'));
    }
  }

  /**
   * Handle login button click
   * Navigates to appropriate login page
   */
  onLogin(): void {
    this.router.navigate(['/client-form']);
  }

  /**
   * Legacy dropdown functionality
   */
  isDropdownOpen = false;

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  /**
   * Navigate to the appropriate profile page
   */
  goToProfile(): void {
    if (this.isClientLoggedIn) {
      this.router.navigate(['/client-form']);
    } else if (this.isAdminLoggedIn) {
      this.router.navigate(['/admin-dashboard']);
    }
  }

  /**
   * Legacy logout method
   */
  logout(): void {
    console.log('Logging out...');
    this.onLogout();
  }

  /**
   * Method to refresh authentication status
   */
  refreshAuthStatus(): void {
    this.checkAuthenticationStatus();
  }
}
