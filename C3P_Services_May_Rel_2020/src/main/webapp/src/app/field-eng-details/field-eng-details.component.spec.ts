import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldEngDetailsComponent } from './field-eng-details.component';

describe('FieldEngDetailsComponent', () => {
  let component: FieldEngDetailsComponent;
  let fixture: ComponentFixture<FieldEngDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FieldEngDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldEngDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
}); 

