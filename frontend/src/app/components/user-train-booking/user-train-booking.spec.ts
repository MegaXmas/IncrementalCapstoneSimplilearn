import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserTrainBooking } from './user-train-booking';

describe('UserTrainBooking', () => {
  let component: UserTrainBooking;
  let fixture: ComponentFixture<UserTrainBooking>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserTrainBooking]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserTrainBooking);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
