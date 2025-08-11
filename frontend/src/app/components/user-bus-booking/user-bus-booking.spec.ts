import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserBusBooking } from './user-bus-booking';

describe('UserBusBooking', () => {
  let component: UserBusBooking;
  let fixture: ComponentFixture<UserBusBooking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserBusBooking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserBusBooking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
