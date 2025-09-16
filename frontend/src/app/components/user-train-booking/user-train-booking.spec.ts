import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserTrainBookingComponent} from './user-train-booking';

describe('UserTrainBooking', () => {
  let component: UserTrainBookingComponent;
  let fixture: ComponentFixture<UserTrainBookingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserTrainBookingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserTrainBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
