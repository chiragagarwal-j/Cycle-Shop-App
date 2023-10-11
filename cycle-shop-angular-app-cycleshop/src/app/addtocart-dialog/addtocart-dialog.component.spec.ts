import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddtocartDialogComponent } from './addtocart-dialog.component';

describe('AddtocartDialogComponent', () => {
  let component: AddtocartDialogComponent;
  let fixture: ComponentFixture<AddtocartDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddtocartDialogComponent]
    });
    fixture = TestBed.createComponent(AddtocartDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
