import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAirportForm } from './admin-airport-form';

describe('AdminAirportForm', () => {
  let component: AdminAirportForm;
  let fixture: ComponentFixture<AdminAirportForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminAirportForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminAirportForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
