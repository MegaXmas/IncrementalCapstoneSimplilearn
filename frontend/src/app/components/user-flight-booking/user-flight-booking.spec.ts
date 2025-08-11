import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserFlightBooking } from './user-flight-booking';

describe('UserFlightBooking', () => {
  let component: UserFlightBooking;
  let fixture: ComponentFixture<UserFlightBooking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserFlightBooking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserFlightBooking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
