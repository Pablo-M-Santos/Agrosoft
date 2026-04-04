import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { AnimalFormDialog } from './animal-form-dialog';
import { AnimalTypeService } from '../../../core/services/animal-type.service';
import { EmployeeService } from '../../../core/services/employee.service';

describe('AnimalFormDialog', () => {
  let component: AnimalFormDialog;
  let fixture: ComponentFixture<AnimalFormDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnimalFormDialog],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: null },
        { provide: MatDialogRef, useValue: { close: () => {} } },
        { provide: AnimalTypeService, useValue: { list: () => of([]) } },
        { provide: EmployeeService, useValue: { list: () => of({ content: [] }) } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AnimalFormDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
