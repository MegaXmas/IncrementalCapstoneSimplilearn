import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateDropdownComponent } from './date-dropdown';

describe('DateDropdown', () => {
  let component: DateDropdownComponent;
  let fixture: ComponentFixture<DateDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DateDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
