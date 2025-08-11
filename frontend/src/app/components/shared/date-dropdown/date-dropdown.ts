import { Component, Input, Output, EventEmitter, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BaseFormControl } from '../base-form-control';

@Component({
  selector: 'app-date-dropdown',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './date-dropdown.html',
  styleUrls: ['./date-dropdown.css', '../form-styles.css'],
  providers: [{
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DateDropdownComponent),
      multi: true}]
})

export class DateDropdownComponent extends BaseFormControl {


  /**
   * Input properties for the date dropdown component.
   */
  @Input() label: string = 'Date';


  /**
   * The id for the date dropdown component.
   */
  @Input() id: string = 'date-dropdown';


  /**
   * Whether the date dropdown is required.
   */
  @Input() isInvalid: boolean = false;


  /**
   * Error message to display when the date is invalid.
   */
  @Input() errorMessage: string = 'Please select a valid date';


  /**
   * Selected day from the dropdown.
   * This is a string representing the day (1-31).
   */
  selectedDay: string = '';


  /**
   * Selected month from the dropdown.
   * This is a string representing the month (1-12).
   */
  selectedMonth: string = '';


  /**
   * Selected year from the dropdown.
   * This is a string representing the year (e.g., '2023').
   */
  selectedYear: string = '';


  /**
   * Array of days from 1 to 31. This will be updated dynamically based on the selected month and year.
   */
  days: number[] = Array.from({ length: 31 }, (_, i) => i + 1);


  /**
   * Array of month names for the dropdown.
   */
  months: string[] = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];


  /**
   * Array of years starting from the current year to the next 10 years.
   */
  years: number[] = Array.from({ length: 10 }, (_, i) => new Date().getFullYear() + i);


  /**
   * @param event Event triggered when the day is changed.
   * Updates the selected day and recalculates the days in the selected month.
   */
   onDayChange(event: Event): void {
    this.selectedDay = (event.target as HTMLSelectElement).value;
    this.updateDaysForSelectedMonth();
    this.updateFormValue();
  }


  /**
   * @param event Event triggered when the month is changed.
   * Updates the selected month, recalculates the days in the selected month, and updates the form value.
   */
  onMonthChange(event: Event): void {
    this.selectedMonth = (event.target as HTMLSelectElement).value;
    this.updateDaysForSelectedMonth();
    this.updateFormValue();
  }


  /**
   * @param event Event triggered when the year is changed.
   * Updates the selected year, recalculates the days in the selected month, and updates the form value.
   */
  onYearChange(event: Event): void {
    this.selectedYear = (event.target as HTMLSelectElement).value;
    this.updateDaysForSelectedMonth();
    this.updateFormValue();
  }


  /**
   * Function to call when the value changes.
   */
  private updateDaysForSelectedMonth(): void {
    if (this.selectedMonth && this.selectedYear) {
      const daysInMonth = new Date(
        parseInt(this.selectedYear), 
        parseInt(this.selectedMonth), 
        0
      ).getDate();
      
      this.days = Array.from({ length: daysInMonth }, (_, i) => i + 1);
      
      if (parseInt(this.selectedDay) > daysInMonth) {
        this.selectedDay = '';
      }
    } else {
      this.days = Array.from({ length: 31 }, (_, i) => i + 1);
    }
  }


  /**
   * Method to update the value of the control and notify the parent form.
   * @param formattedDate The formatted date string in 'YYYY-MM-DD' format.
   * If the selected day, month, and year are valid,
   * it formats the date as 'YYYY-MM-DD' and calls the base class method to update the value.
   * If any of the selected values are empty, it resets the value to an empty string
   * and calls the base class method to update the value.
   * This method is used to ensure that the form control's value is updated correctly
   */
  private updateFormValue(): void {
    if (this.selectedDay && this.selectedMonth && this.selectedYear) {
      const formattedDate = `${this.selectedYear}-${this.selectedMonth.padStart(2, '0')}-${this.selectedDay.padStart(2, '0')}`;
      this.updateValue(formattedDate); // Uses base class method
    } else {
      this.updateValue('');
    }
  }

  
  /**
   * Required implementation from base class
   * Writes a value to the control.
   * @param value The value to write to the control, expected in 'YYYY-MM-DD' format.
   * If the value is empty, it resets the selected day, month, and year.
   * If the value is valid, it extracts the day, month, and year,
   * updates the selectedDay, selectedMonth, and selectedYear properties,
   * and updates the days array based on the selected month and year.
   * If the value is invalid, it resets the selectedDay, selectedMonth, and selectedYear properties
   * and sets the days array to a default of 31 days.
   */
  writeValue(value: string): void {
    if (value) {
      const date = new Date(value);
      this.selectedDay = date.getDate().toString();
      this.selectedMonth = (date.getMonth() + 1).toString();
      this.selectedYear = date.getFullYear().toString();
      this.updateDaysForSelectedMonth();
    } else {
      this.selectedDay = '';
      this.selectedMonth = '';
      this.selectedYear = '';
      this.days = Array.from({ length: 31 }, (_, i) => i + 1);
    }
  }
}