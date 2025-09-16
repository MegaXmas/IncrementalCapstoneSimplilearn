import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StationSearchComponent} from './station-search';

describe('StationSearch', () => {
  let component: StationSearchComponent;
  let fixture: ComponentFixture<StationSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StationSearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StationSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
