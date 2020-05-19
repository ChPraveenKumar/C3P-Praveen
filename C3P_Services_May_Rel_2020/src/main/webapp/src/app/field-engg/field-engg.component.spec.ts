import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FieldEnggComponent } from './field-engg.component';

describe('FieldEnggComponent', () => {
  let component: FieldEnggComponent;
  let fixture: ComponentFixture<FieldEnggComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FieldEnggComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldEnggComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
