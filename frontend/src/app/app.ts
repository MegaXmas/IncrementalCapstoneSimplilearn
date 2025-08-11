import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { HeaderComponent } from './components/header/header';
import { FlightFormComponent } from "./components/flight-form/flight-form";
import { TrainFormComponent } from "./components/train-form/train-form";
import { BusFormComponent } from "./components/bus-form/bus-form";

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet, 
    LoginComponent, 
    HeaderComponent, 
    FlightFormComponent, 
    TrainFormComponent, 
    BusFormComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('angular-capstone');
}
