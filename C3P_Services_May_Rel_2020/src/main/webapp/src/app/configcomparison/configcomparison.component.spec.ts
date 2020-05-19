import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfigcomparisonComponent } from './configcomparison.component';

describe('ConfigcomparisonComponent', () => {
  let component: ConfigcomparisonComponent;
  let fixture: ComponentFixture<ConfigcomparisonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfigcomparisonComponent ]
    })
    .compileComponents();
  }));
 
  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigcomparisonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
