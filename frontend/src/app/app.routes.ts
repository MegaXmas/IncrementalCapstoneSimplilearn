import { Routes } from '@angular/router';
import { AdminLoginComponent } from './components/login/admin-login';
import { FlightFormComponent } from './components/flight-form/flight-form';
import { TrainFormComponent } from './components/train-form/train-form';
import { BusFormComponent } from './components/bus-form/bus-form';
import { UserBusBookingComponent } from './components/user-bus-booking/user-bus-booking';
import { UserFlightBookingComponent } from './components/user-flight-booking/user-flight-booking';
import { AdminAirportFormComponent } from './components/admin-airport-form/admin-airport-form';
import { AdminBusStationFormComponent } from './components/admin-bus-station-form/admin-bus-station-form';
import { AdminTrainStationFormComponent } from './components/admin-train-station-form/admin-train-station-form';
import { ClientFormComponent } from './components/client-form/client-form';

// Define application routes
export const routes: Routes = [
    // Default route - redirects to login
    { path: '', redirectTo: '/client-form', pathMatch: 'full' },

    { path: 'client-form', component: ClientFormComponent },

    // Main component routes
    { path: 'flight-form', component: FlightFormComponent },
    { path: 'train-form', component: TrainFormComponent },
    { path: 'bus-form', component: BusFormComponent },
    { path: 'user-bus-booking', component: UserBusBookingComponent },
    { path: 'user-flight-booking', component: UserFlightBookingComponent },


    // Admin routes
    { path: 'admin-login', component: AdminLoginComponent },

    { path: 'admin/airport/add', component: AdminAirportFormComponent },
    { path: 'admin-airport-form', component: AdminAirportFormComponent },

    { path: 'admin/bus-station/add', component: AdminBusStationFormComponent },
    { path: 'admin-bus-station-form', component: AdminBusStationFormComponent },

    { path: 'admin/train-station/add', component: AdminTrainStationFormComponent },
    { path: 'admin-train-station-form', component: AdminTrainStationFormComponent },
    
    // Wildcard route - must be last
    { path: '**', redirectTo: '/client-form' }
];