export interface Client {
  // Core Identity
  id?: number;
  username: string;
  email: string;
  password?: string;  // Optional - never received from backend for security

  // Personal Information
  firstName: string;
  lastName: string;
  phone: string;
  address?: string;
  credit_card?: string;  // Usually masked when received from backend

  // Account Management
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
  username: string;
  password: string;
}
