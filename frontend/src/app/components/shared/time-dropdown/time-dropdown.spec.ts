import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeDropdown } from './time-dropdown';

describe('TimeDropdown', () => {
  let component: TimeDropdown;
  let fixture: ComponentFixture<TimeDropdown>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimeDropdown]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TimeDropdown);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
