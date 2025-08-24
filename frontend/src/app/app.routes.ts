import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { FlightFormComponent } from './components/flight-form/flight-form';
import { TrainFormComponent } from './components/train-form/train-form';
import { BusFormComponent } from './components/bus-form/bus-form';
import { UserBusBookingComponent } from './components/user-bus-booking/user-bus-booking';
import { AdminAirportFormComponent } from './components/admin-airport-form/admin-airport-form';

// Define application routes
export const routes: Routes = [
    // Default route - redirects to login
    { path: '', redirectTo: '/login', pathMatch: 'full' },

    // Main component routes
    { path: 'login', component: LoginComponent },
    { path: 'flight-form', component: FlightFormComponent },
    { path: 'train-form', component: TrainFormComponent },
    { path: 'bus-form', component: BusFormComponent },
    { path: 'user-bus-booking', component: UserBusBookingComponent },

    // Admin routes
    { path: 'admin/airport/add', component: AdminAirportFormComponent },
    
    // Alternative route for backward compatibility
    { path: 'admin-airport-form', component: AdminAirportFormComponent },

    // Wildcard route - must be last
    { path: '**', redirectTo: '/login' }
];