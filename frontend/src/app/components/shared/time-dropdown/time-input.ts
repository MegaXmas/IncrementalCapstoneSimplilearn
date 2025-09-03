// frontend/src/app/components/shared/time-input/time-input.ts
import { Component, Input, forwardRef } from '@angular/core';
import { NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BaseFormControl } from '../base-form-control';

@Component({
  selector: 'app-time-input',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="form-group">
      <label [for]="id">{{ label }}</label>
      
      @if (useDropdown) {
        <!-- Dropdown version with selects -->
        <div class="time-selectors d-flex gap-2">
          <select [id]="id + '-hour'"
                  class="form-control"
                  [class.is-invalid]="isInvalid"
                  [value]="selectedHours"
                  (change)="onHoursChange($event)">
            <option value="" disabled>Hour</option>
            @for (hour of hours; track hour) {
              <option [value]="hour">{{ formatHour(hour) }}</option>
            }
          </select>
          
          <select [id]="id + '-minute'"
                  class="form-control"
                  [class.is-invalid]="isInvalid"
                  [value]="selectedMinutes"
                  (change)="onMinutesChange($event)">
            <option value="" disabled>Min</option>
            @for (minute of minutes; track minute) {
              <option [value]="minute">{{ formatMinute(minute) }}</option>
            }
          </select>
          
          <select [id]="id + '-period'"
                  class="form-control"
                  [class.is-invalid]="isInvalid"
                  [value]="selectedPeriod"
                  (change)="onPeriodChange($event)">
            <option value="" disabled>AM/PM</option>
            <option value="AM">AM</option>
            <option value="PM">PM</option>
          </select>
        </div>
      } @else {
        <!-- Native time input -->
        <input 
          [id]="id"
          type="time"
          class="form-control"
          [class.is-invalid]="isInvalid"
          [value]="timeValue"
          [step]="stepMinutes * 60"
          (change)="onTimeChange($event)">
      }
      
      @if (isInvalid && errorMessage) {
        <div class="invalid-feedback">{{ errorMessage }}</div>
      }
    </div>
  `,
  styleUrls: ['../form-styles.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => TimeInputComponent),
    multi: true
  }]
})
export class TimeInputComponent extends BaseFormControl {
  
  @Input() label: string = 'Time';
  @Input() id: string = 'time-input';
  @Input() isInvalid: boolean = false;
  @Input() errorMessage: string = 'Please select a valid time';
  @Input() stepMinutes: number = 15; // For native input
  @Input() useDropdown: boolean = true; // Toggle between dropdown and native

  // For native input
  timeValue: string = '';

  // For dropdown version
  selectedHours: string = '';
  selectedMinutes: string = '';
  selectedPeriod: string = '';

  // Dropdown options
  hours: number[] = Array.from({ length: 12 }, (_, i) => i + 1);
  minutes: number[] = [];

  ngOnInit() {
    // Create minutes array based on step
    this.minutes = [];
    for (let i = 0; i < 60; i += this.stepMinutes) {
      this.minutes.push(i);
    }
  }

  formatHour(hour: number): string {
    return hour.toString();
  }

  formatMinute(minute: number): string {
    return minute.toString().padStart(2, '0');
  }

  // Native input handler
  onTimeChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.timeValue = input.value;
    this.updateValue(this.timeValue);
  }

  // Dropdown handlers
  onHoursChange(event: Event): void {
    this.selectedHours = (event.target as HTMLSelectElement).value;
    this.updateDropdownValue();
  }

  onMinutesChange(event: Event): void {
    this.selectedMinutes = (event.target as HTMLSelectElement).value;
    this.updateDropdownValue();
  }

  onPeriodChange(event: Event): void {
    this.selectedPeriod = (event.target as HTMLSelectElement).value;
    this.updateDropdownValue();
  }

  private updateDropdownValue(): void {
    if (this.selectedHours && this.selectedMinutes !== '' && this.selectedPeriod) {
      let hour = parseInt(this.selectedHours);
      
      // Convert 12-hour to 24-hour format
      if (this.selectedPeriod === 'PM' && hour !== 12) {
        hour += 12;
      } else if (this.selectedPeriod === 'AM' && hour === 12) {
        hour = 0;
      }
      
      const formattedTime = `${hour.toString().padStart(2, '0')}:${this.selectedMinutes.padStart(2, '0')}`;
      this.updateValue(formattedTime);
    } else {
      this.updateValue('');
    }
  }

  writeValue(value: string): void {
    if (this.useDropdown) {
      // Handle dropdown format
      if (value) {
        const [hourStr, minuteStr] = value.split(':');
        let hour = parseInt(hourStr);
        
        // Convert from 24-hour to 12-hour format
        if (hour === 0) {
          this.selectedHours = '12';
          this.selectedPeriod = 'AM';
        } else if (hour < 12) {
          this.selectedHours = hour.toString();
          this.selectedPeriod = 'AM';
        } else if (hour === 12) {
          this.selectedHours = '12';
          this.selectedPeriod = 'PM';
        } else {
          this.selectedHours = (hour - 12).toString();
          this.selectedPeriod = 'PM';
        }
        
        this.selectedMinutes = minuteStr || '';
      } else {
        this.selectedHours = '';
        this.selectedMinutes = '';
        this.selectedPeriod = '';
      }
    } else {
      // Handle native input format
      this.timeValue = value || '';
    }
  }
}