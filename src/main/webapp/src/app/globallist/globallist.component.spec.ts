import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GloballistComponent } from './globallist.component';

describe('GloballistComponent', () => {
  let component: GloballistComponent;
  let fixture: ComponentFixture<GloballistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GloballistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GloballistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
