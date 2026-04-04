import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import {
  MAT_DATE_LOCALE,
  MatNativeDateModule,
  provideNativeDateAdapter,
} from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { Animal } from '../../../core/models/animal.model';
import { AnimalType } from '../../../core/models/animal-type.model';
import { AnimalTypeService } from '../../../core/services/animal-type.service';
import { EmployeeService } from '../../../core/services/employee.service';

interface EmployeeOption {
  id: string;
  fullName: string;
}

@Component({
  selector: 'app-animal-form-dialog',
  standalone: true,
  providers: [{ provide: MAT_DATE_LOCALE, useValue: 'pt-BR' }, provideNativeDateAdapter()],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
  ],
  templateUrl: './animal-form-dialog.html',
  styleUrls: ['./animal-form-dialog.css'],
})
export class AnimalFormDialog implements OnInit {
  form: FormGroup;
  maxEntryDate = new Date();
  animalTypes: AnimalType[] = [];
  employees: EmployeeOption[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AnimalFormDialog>,
    private animalTypeService: AnimalTypeService,
    private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: Animal | null,
  ) {
    this.form = this.fb.group({
      name: [
        data?.name || '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      ],
      animalTypeId: [data?.animalTypeId || null, [Validators.required]],
      weight: [data?.weight ?? null, [Validators.required, Validators.min(Number.EPSILON)]],
      entryDate: [
        data?.entryDate ? new Date(data.entryDate) : new Date(),
        [Validators.required, this.notFutureDateValidator()],
      ],
      status: [data?.status || 'ACTIVE', [Validators.required]],
      responsibleEmployeeId: [data?.responsibleEmployeeId || null],
    });
  }

  ngOnInit(): void {
    this.loadAnimalTypes();
    this.loadEmployees();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      ...this.form.value,
      entryDate: this.formatDate(this.form.value.entryDate),
      responsibleEmployeeId: this.form.value.responsibleEmployeeId || null,
    };

    this.dialogRef.close(payload);
  }

  close(): void {
    this.dialogRef.close();
  }

  hasError(controlName: string, errorName?: string): boolean {
    const control = this.form.get(controlName);
    if (!control) {
      return false;
    }

    if (!control.touched && !this.form.touched) {
      return false;
    }

    return errorName ? control.hasError(errorName) : control.invalid;
  }

  private loadAnimalTypes(): void {
    this.animalTypeService.list().subscribe({
      next: (types) => {
        this.animalTypes = [...types].sort((a, b) => a.name.localeCompare(b.name, 'pt-BR'));
        this.cdr.detectChanges();
      },
      error: () => {
        this.animalTypes = [];
      },
    });
  }

  private loadEmployees(): void {
    this.employeeService.list(0, 200).subscribe({
      next: (response) => {
        const content = response?.content || [];
        this.employees = content
          .map((employee: any) => ({ id: employee.id, fullName: employee.fullName }))
          .sort((a: EmployeeOption, b: EmployeeOption) =>
            a.fullName.localeCompare(b.fullName, 'pt-BR'),
          );
        this.cdr.detectChanges();
      },
      error: () => {
        this.employees = [];
      },
    });
  }

  private notFutureDateValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }

      const value = new Date(control.value);
      if (Number.isNaN(value.getTime())) {
        return { invalidDate: true };
      }

      const today = new Date();
      today.setHours(0, 0, 0, 0);
      value.setHours(0, 0, 0, 0);

      return value <= today ? null : { futureDate: true };
    };
  }

  private formatDate(date: Date): string {
    return new Date(date).toISOString().split('T')[0];
  }
}
