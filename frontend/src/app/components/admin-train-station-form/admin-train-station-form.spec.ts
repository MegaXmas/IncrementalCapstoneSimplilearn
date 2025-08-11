import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTrainStationForm } from './admin-train-station-form';

describe('AdminTrainStationForm', () => {
  let component: AdminTrainStationForm;
  let fixture: ComponentFixture<AdminTrainStationForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminTrainStationForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTrainStationForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
