import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GlobalListMgmtComponent } from './global-list-mgmt.component';

describe('GlobalListMgmtComponent', () => {
  let component: GlobalListMgmtComponent;
  let fixture: ComponentFixture<GlobalListMgmtComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GlobalListMgmtComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GlobalListMgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
