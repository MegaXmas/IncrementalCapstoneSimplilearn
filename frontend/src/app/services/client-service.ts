export interface Client {
  id?: number;
  username: string;
  email: string;
  password?: string;

  firstName: string;
  lastName: string;
  phone: string;
  address?: string;
  credit_card?: string;

  enabled?: boolean;
  accountLocked?: boolean;
  createdAt?: string;    // ISO date string from backend
  lastLogin?: string;    // ISO date string from backend
}

/**
 * Interface for client registration form
 * This contains only the fields needed when creating a new account
 */
export interface ClientRegistration {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
}

/**
 * Interface for client login form
 * Simple form with just the credentials needed
 */
export interface ClientLogin {
  usernameOrEmail: string;
  password: string;
}

import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

/**
 * Angular service to communicate with Spring Boot ClientController
 * This handles all HTTP requests related to client authentication and management
 */
@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private readonly API_BASE_URL = 'http://localhost:8080/api/clients'; // Adjust to your Spring Boot port


  constructor(private http: HttpClient) {}

  /**
   * Register a new client account
   * Calls POST /api/clients/register
   */
  register(registrationData: ClientRegistration): Observable<ApiResponse<RegistrationResult>> {
    return this.http.post<ApiResponse<RegistrationResult>>(
      `${this.API_BASE_URL}/register`,
      registrationData
    );
  }

  /**
   * Login with username/email and password
   * Calls POST /api/clients/login
   */
  login(loginData: ClientLogin): Observable<ApiResponse<LoginResult>> {
    const loginRequest = {
      usernameOrEmail: loginData.usernameOrEmail,
      password: loginData.password
    };

    return this.http.post<ApiResponse<LoginResult>>(
      `${this.API_BASE_URL}/login`,
      loginRequest
    );
  }

  /**
   * Get current client profile
   * Calls GET /api/clients/profile
   */
  getProfile(): Observable<ApiResponse<Client>> {
    return this.http.get<ApiResponse<Client>>(
      `${this.API_BASE_URL}/profile`,
      { headers: this.getAuthHeaders() }
    );
  }

 getUsernameFromToken(): string | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      // Decode JWT token (it has 3 parts separated by dots)
      const tokenParts = token.split('.');
      if (tokenParts.length !== 3) return null;

      // Decode the payload (middle part)
      const payload = JSON.parse(atob(tokenParts[1]));

      // JWT stores username in the 'sub' field
      return payload.sub || null;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  /**
   * Update client profile
   * Calls PUT /api/clients/profile
   */
  updateProfile(clientData: Client): Observable<ApiResponse<Client>> {
    return this.http.put<ApiResponse<Client>>(
      `${this.API_BASE_URL}/profile`,
      clientData,
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Change password
   * Calls POST /api/clients/change-password
   */
  changePassword(currentPassword: string, newPassword: string): Observable<ApiResponse<string>> {
    const passwordChangeRequest = {
      currentPassword,
      newPassword
    };

    return this.http.post<ApiResponse<string>>(
      `${this.API_BASE_URL}/change-password`,
      passwordChangeRequest,
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Refresh JWT token
   * Calls POST /api/clients/refresh-token
   */
  refreshToken(): Observable<ApiResponse<TokenRefreshResult>> {
    return this.http.post<ApiResponse<TokenRefreshResult>>(
      `${this.API_BASE_URL}/refresh-token`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  /**
   * Validate current JWT token
   * Calls POST /api/clients/validate-token
   */
  validateToken(): Observable<ApiResponse<TokenValidationResult>> {
    return this.http.post<ApiResponse<TokenValidationResult>>(
      `${this.API_BASE_URL}/validate-token`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  // ============================================================================
  // TOKEN MANAGEMENT METHODS
  // ============================================================================

  /**
   * Store JWT token in browser storage
   */
  storeToken(token: string): void {
    sessionStorage.setItem('jwt_token', token);
  }

  /**
   * Get JWT token from browser storage
   */
  getToken(): string | null {
    return sessionStorage.getItem('jwt_token');
  }

  /**
   * Remove JWT token from browser storage
   */
  removeToken(): void {
    sessionStorage.removeItem('jwt_token');
  }

  /**
   * Check if user is logged in (has valid token)
   */
  isLoggedIn(): boolean {
    const token = this.getToken();
    return token !== null && token.length > 0;
  }

  /**
   * Create authorization headers with JWT token
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    if (token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      });
    }
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }
}

// ============================================================================
// INTERFACES - Match the Spring Boot controller response structures
// ============================================================================

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
}

export interface RegistrationResult {
  username: string;
  message: string;
}

export interface LoginResult {
  token: string;
  client: Client;
  message: string;
}

export interface TokenRefreshResult {
  token: string;
  message: string;
}

export interface TokenValidationResult {
  valid: boolean;
  username: string | null;
}
