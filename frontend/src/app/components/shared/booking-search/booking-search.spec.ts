import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BookingSearchComponent} from './booking-search';

describe('BookingSearch', () => {
  let component: BookingSearchComponent;
  let fixture: ComponentFixture<BookingSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingSearchComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookingSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
