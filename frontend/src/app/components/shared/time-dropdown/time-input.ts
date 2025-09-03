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
      <input 
        [id]="id"
        type="time"
        class="form-control"
        [class.is-invalid]="isInvalid"
        [value]="timeValue"
        [step]="stepMinutes * 60"
        (change)="onTimeChange($event)">
      @if (isInvalid && errorMessage) {
        <div class="error-message">{{ errorMessage }}</div>
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
  @Input() stepMinutes: number = 5; // Time intervals (5 = 5-minute steps)

  timeValue: string = '';

  /**
   * Handle time input change
   * Automatically outputs in HH:MM format (24-hour) - same as your backend expects
   */
  onTimeChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.timeValue = input.value;
    this.updateValue(this.timeValue); // Passes HH:MM format to form
  }

  /**
   * Write value from form to component
   * Expects HH:MM format (same as your current component outputs)
   */
  writeValue(value: string): void {
    this.timeValue = value || '';
  }
}