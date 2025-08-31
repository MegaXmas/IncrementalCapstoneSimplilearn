import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTrainStationFormComponent } from './admin-train-station-form';

describe('AdminTrainStationForm', () => {
  let component: AdminTrainStationFormComponent;
  let fixture: ComponentFixture<AdminTrainStationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTrainStationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTrainStationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
