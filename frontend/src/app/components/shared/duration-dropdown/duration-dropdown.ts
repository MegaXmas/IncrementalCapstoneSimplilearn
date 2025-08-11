import { Component, Input, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BaseFormControl } from '../base-form-control';

@Component({
  selector: 'app-duration-dropdown',
  imports: [ CommonModule ],
  templateUrl: './duration-dropdown.html',
  styleUrls: ['./duration-dropdown.css', '../form-styles.css'],
  standalone: true,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DurationDropdownComponent),
      multi: true
    }
  ]
})

export class DurationDropdownComponent extends BaseFormControl {


  /**
   * Input properties for the duration dropdown component.
   */
  @Input() label: string = 'Duration';


  /**
   * The id for the duration dropdown component.
   */
  @Input() id: string = 'duration-dropdown';


  /**
   * Maximum hours allowed in the dropdown.
   */
  @Input() maxHours: number = 24; // Allow customization of max hours


  /**
   * Selected hours from the dropdown.
   * This is a string representing the hours (0-23).
   */
  selectedHours: string = '';


  /**
   * Selected minutes from the dropdown.
   * This is a string representing the minutes (0-55 in 5-minute increments).
   */
  selectedMinutes: string = '';


  /**
   * Selected period (AM/PM) from the dropdown.
   * This is a string representing the period (AM or PM).
   */
  selectedPeriod: string = 'AM';


  /**
   * Array of hours from 0 to maxHours for the dropdown.
   * This will be generated dynamically based on the maxHours input.
   */
  hours: number[] = [];


  /**
   * Array of minutes from 0 to 55 in 5-minute increments for the dropdown.
   * This is a static array as minutes are always in 5-minute increments.
   */
  minutes: number[] = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];


  /**
   * Lifecycle hook that is called after data-bound properties are initialized.
   * It initializes the hours array based on the maxHours input.
   */
  ngOnInit() {
    this.hours = Array.from({ length: this.maxHours + 1 }, (_, i) => i);
  }


  /**
   * @param event The change event from the hours dropdown.
   * Updates the selected hours and calls updateFormValue to propagate changes.
   */
  onHoursChange(event: Event): void {
    this.selectedHours = (event.target as HTMLSelectElement).value;
    this.updateFormValue();
  }


  /**
   * @param event The change event from the minutes dropdown.
   * Updates the selected minutes and calls updateFormValue to propagate changes.
   */
  onMinutesChange(event: Event): void {
    this.selectedMinutes = (event.target as HTMLSelectElement).value;
    this.updateFormValue();
  }

  
    onPeriodChange(event: Event): void {
    this.selectedPeriod = (event.target as HTMLSelectElement).value;
    this.updateFormValue();
  }


  /**
   * Method to update the value of the control and notify the parent form.
   * @param formattedDuration The formatted duration string in 'Xh Ym' format.
   * If the selected hours and minutes are valid, it formats the duration as 'Xh Ym'
   * and calls the base class method to update the value.
   * If any of the selected values are empty, it resets the value to an empty string
   * and calls the base class method to update the value.
   * This method is used to ensure that the form control's value is updated correctly
   */
  private updateFormValue(): void {
    if (this.selectedHours !== '' && this.selectedMinutes !== '') {
      // Format as "Xh Ym" (e.g., "2h 30m")
      const formattedDuration = `${this.selectedHours}h ${this.selectedMinutes}m`;
      this.updateValue(formattedDuration);
    } else {
      this.updateValue('');
    }
  }


  
  /**
   * Required implementation from base class.
   * Writes a value to the control.
   * @param value The value to write to the control in 'Xh Ym' format.
   * If the value is empty, it resets the selected hours and minutes.
   * If the value is provided, it parses the hours and minutes,
   * and updates the selectedHours and selectedMinutes accordingly.
   */
  writeValue(value: string): void {
    if (value) {
      // Parse "Xh Ym" format
      const hourMatch = value.match(/(\d+)h/);
      const minuteMatch = value.match(/(\d+)m/);
      
      this.selectedHours = hourMatch ? hourMatch[1] : '';
      this.selectedMinutes = minuteMatch ? minuteMatch[1] : '';
    } else {
      this.selectedHours = '';
      this.selectedMinutes = '';
    }
  }
}
