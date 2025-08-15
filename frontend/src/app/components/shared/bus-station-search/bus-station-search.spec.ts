import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusStationSearch } from './bus-station-search';

describe('BusStationSearch', () => {
  let component: BusStationSearch;
  let fixture: ComponentFixture<BusStationSearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusStationSearch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BusStationSearch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
