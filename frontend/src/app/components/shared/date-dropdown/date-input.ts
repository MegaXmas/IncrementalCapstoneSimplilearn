import {Component, forwardRef, Input} from '@angular/core';
import {NG_VALUE_ACCESSOR} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {BaseFormControl} from '../base-form-control';

@Component({
  selector: 'app-date-input',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="form-group">
      <label [for]="id">{{ label }}</label>
      <input
        [id]="id"
        type="date"
        class="form-control"
        [class.is-invalid]="isInvalid"
        [value]="dateValue"
        (change)="onDateChange($event)"
        [min]="minDate"
        [max]="maxDate">
      @if (isInvalid && errorMessage) {
        <div class="error-message">{{ errorMessage }}</div>
      }
    </div>
  `,
  styleUrls: ['../form-styles.css'],
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => DateInputComponent),
    multi: true
  }]
})
export class DateInputComponent extends BaseFormControl {

  @Input() label: string = 'Date';
  @Input() id: string = 'date-input';
  @Input() isInvalid: boolean = false;
  @Input() errorMessage: string = 'Please select a valid date';
  @Input() minDate?: string;
  @Input() maxDate?: string;

  dateValue: string = '';

  /**
   * Handle date input change
   * Automatically outputs in YYYY-MM-DD format
   */
  onDateChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.dateValue = input.value;
    this.updateValue(this.dateValue); // Passes YYYY-MM-DD format to form
  }

  /**
   * Write value from form to component
   * Expects YYYY-MM-DD format
   */
  writeValue(value: string): void {
    this.dateValue = value || '';
  }
}
