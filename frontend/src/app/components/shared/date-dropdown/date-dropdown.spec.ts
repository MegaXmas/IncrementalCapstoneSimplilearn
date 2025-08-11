import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateDropdown } from './date-dropdown';

describe('DateDropdown', () => {
  let component: DateDropdown;
  let fixture: ComponentFixture<DateDropdown>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateDropdown]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DateDropdown);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
