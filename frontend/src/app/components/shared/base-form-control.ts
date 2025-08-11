import { ControlValueAccessor } from '@angular/forms';

export abstract class BaseFormControl implements ControlValueAccessor {

  /**
   * @param value The value of the form control.
   */
  protected value: any = '';


  /**
   * @param disable A boolean indicating whether the control is disabled.
   */
  protected disabled = false;


  /*
   * Function to call when the value changes.
   */
  protected onChange = (value: any) => {};


  /*
   * Function to call when the control is touched.
   */
  protected onTouched = () => {};


  /**
   * Method to write a value to the control.
   * @param value The value to write to the control.
   */
  abstract writeValue(value: any): void;


  /**
   * Method to register a callback function that should be called when the control's value changes.
   * @param fn The callback function to register.
   */
  registerOnChange(fn: (value: any) => void): void {
    this.onChange = fn;
  }


  /**
   * Method to register a callback function that should be called when the control is touched.
   * @param fn The callback function to register.
   */
  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }


  /**
   * Method to set the disabled state of the control.
   * @param isDisabled A boolean indicating whether the control should be disabled.
   */
  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  
  /**
   * Method to update the value of the control and notify the parent form.
   * @param newValue The new value to set in the control.
   */
  protected updateValue(newValue: any): void {
    this.value = newValue;
    this.onChange(newValue);
    this.onTouched();
  }
}