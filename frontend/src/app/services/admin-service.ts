import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface AdminUser {
  id?: number;
  adminUsername: string;
  adminPassword?: string;  // Optional - never received from backend for security
  enabled?: boolean;
  accountLocked?: boolean;
  createdAt?: string;
  lastLogin?: string;
}

/**
 * Interface for admin login form
 * Simple form with just the credentials needed
 */
export interface AdminLogin {
  adminUsername: string;
  adminPassword: string;
}

/**
 * Response interfaces matching your backend API structure
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
}

export interface LoginResult {
  token: string;
  admin: AdminUser;
  message: string;
}


/**
 * Angular service to communicate with Spring Boot AdminController
 * This handles all HTTP requests related to admin authentication
 */
@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private readonly API_BASE_URL = 'http://localhost:8080/api/admin'; // Adjust to your Spring Boot port
  private readonly TOKEN_KEY = 'admin_jwt_token';

  constructor(private http: HttpClient) {}

  /**
   * Login with admin username and password
   * Calls POST /api/admin/login
   */
  login(loginData: AdminLogin): Observable<ApiResponse<LoginResult>> {
    const loginRequest = {
      adminUsername: loginData.adminUsername,
      adminPassword: loginData.adminPassword
    };

    return this.http.post<ApiResponse<LoginResult>>(
      `${this.API_BASE_URL}/login`,
      loginRequest
    );
  }

  /**
   * Get current admin profile
   * Calls GET /api/admin/profile
   */
  getProfile(): Observable<ApiResponse<AdminUser>> {
    return this.http.get<ApiResponse<AdminUser>>(
      `${this.API_BASE_URL}/profile`,
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Store JWT token in localStorage
   */
  storeToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Get JWT token from localStorage
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Remove JWT token from localStorage
   */
  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Check if admin is logged in
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      // Check if token is expired
      const tokenParts = token.split('.');
      if (tokenParts.length !== 3) return false;

      const payload = JSON.parse(atob(tokenParts[1]));
      const currentTime = Math.floor(Date.now() / 1000);

      return payload.exp > currentTime;
    } catch (error) {
      console.error('Invalid token format:', error);
      this.removeToken();
      return false;
    }
  }

  /**
   * Get username from JWT token
   */
  getUsernameFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const tokenParts = token.split('.');
      if (tokenParts.length !== 3) return null;

      const payload = JSON.parse(atob(tokenParts[1]));
      return payload.sub || null;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  /**
   * Create HTTP headers with JWT token for authenticated requests
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }
}
