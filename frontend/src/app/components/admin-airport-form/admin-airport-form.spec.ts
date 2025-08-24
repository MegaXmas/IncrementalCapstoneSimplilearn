import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAirportFormComponent } from './admin-airport-form';

describe('AdminAirportForm', () => {
  let component: AdminAirportFormComponent;
  let fixture: ComponentFixture<AdminAirportFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminAirportFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminAirportFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
