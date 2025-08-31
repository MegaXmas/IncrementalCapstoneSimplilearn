import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeDropdownComponent } from './time-dropdown';

describe('TimeDropdown', () => {
  let component: TimeDropdownComponent;
  let fixture: ComponentFixture<TimeDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimeDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TimeDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
