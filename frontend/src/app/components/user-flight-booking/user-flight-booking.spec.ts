import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserFlightBookingComponent} from './user-flight-booking';

describe('UserFlightBooking', () => {
  let component: UserFlightBookingComponent;
  let fixture: ComponentFixture<UserFlightBookingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserFlightBookingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserFlightBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
