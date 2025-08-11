import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BaseFormControl } from '../base-form-control';

@Component({
  selector: 'app-time-dropdown',
  imports: [CommonModule],
  templateUrl: './time-dropdown.html',
  styleUrls: ['./time-dropdown.css', '../form-styles.css'],
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TimeDropdownComponent),
      multi: true
    }
  ]
})

export class TimeDropdownComponent extends BaseFormControl {

  /**
   * Input properties for the time dropdown component.
   */
  @Input() label: string = 'Time';


  /**
   * The id for the time dropdown component.
   */
  @Input() id: string = 'time-dropdown';


  /**
   * Selected hours from the dropdown.
   */
  selectedHours: string = '';


  /**
   * Selected minutes from the dropdown.
   */
  selectedMinutes: string = '';


  /**
   * Selected period (AM/PM) from the dropdown.
   */
  selectedPeriod: string = '';


  /**
   * Array of hours from 1 to 12 for the dropdown.
   */
  hours: number[] = Array.from({ length: 12 }, (_, i) => i + 1);


  /**
   * Array of minutes from 0 to 55 in 5-minute increments for the dropdown.
   */
  minutes: number[] = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];


   /**
   * Format hour for display (adds leading zero if needed)
   */
  formatHourDisplay(hour: number): string {
    return hour.toString();
  }

  /**
   * Format minute for display (adds leading zero)
   */
  formatMinuteDisplay(minute: number): string {
    return minute.toString().padStart(2, '0');
  }

  /**
   * Handle hour selection change
   */
  onHoursChange(event: Event): void {
    this.selectedHours = (event.target as HTMLSelectElement).value;
    this.updateFormValue();
  }

  /**
   * Handle minute selection change
   */
  onMinutesChange(event: Event): void {
    this.selectedMinutes = (event.target as HTMLSelectElement).value;
    this.updateFormValue();
  }

  /**
   * Handle AM/PM period change
   */
  onPeriodChange(event: Event): void {
    this.selectedPeriod = (event.target as HTMLSelectElement).value;
    this.updateFormValue();
  }

  /**
   * Update the form value and notify parent
   */
  private updateFormValue(): void {
    // Check if all fields have values (selectedMinutes can be '0' which is falsy)
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

  /**
   * Write value from form to component (ControlValueAccessor implementation)
   */
  writeValue(value: string): void {
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
  }
}