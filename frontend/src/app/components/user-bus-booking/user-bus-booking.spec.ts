import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserBusBookingComponent } from './user-bus-booking';

describe('UserBusBooking', () => {
  let component: UserBusBookingComponent;
  let fixture: ComponentFixture<UserBusBookingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserBusBookingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserBusBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
