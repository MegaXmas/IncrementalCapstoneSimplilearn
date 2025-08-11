import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBusStationFormComponent } from './admin-bus-station-form';

describe('AdminBusStationForm', () => {
  let component: AdminBusStationFormComponent;
  let fixture: ComponentFixture<AdminBusStationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminBusStationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBusStationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
