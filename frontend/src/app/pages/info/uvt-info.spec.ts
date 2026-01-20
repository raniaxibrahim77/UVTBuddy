import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UvtInfo } from './uvt-info';

describe('UvtInfo', () => {
  let component: UvtInfo;
  let fixture: ComponentFixture<UvtInfo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UvtInfo],
    }).compileComponents();

    fixture = TestBed.createComponent(UvtInfo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
