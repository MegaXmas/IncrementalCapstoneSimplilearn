import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DurationDropdown } from './duration-dropdown';

describe('DurationDropdown', () => {
  let component: DurationDropdown;
  let fixture: ComponentFixture<DurationDropdown>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DurationDropdown]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DurationDropdown);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
