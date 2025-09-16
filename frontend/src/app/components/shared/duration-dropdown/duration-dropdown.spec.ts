import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DurationDropdownComponent} from './duration-dropdown';

describe('DurationDropdown', () => {
  let component: DurationDropdownComponent;
  let fixture: ComponentFixture<DurationDropdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DurationDropdownComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DurationDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
