import { Component, Signal } from "@angular/core";
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';

export interface AdminAccountObject {
  adminUsername: Signal<string>;
  adminPassword: Signal<string>;
}