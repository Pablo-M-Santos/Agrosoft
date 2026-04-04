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
import { Machine } from '../../../core/models/machine.model';
import { EmployeeService } from '../../../core/services/employee.service';

interface EmployeeOption {
  id: string;
  fullName: string;
}

@Component({
  selector: 'app-machine-form-dialog',
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
  templateUrl: './machine-form-dialog.html',
  styleUrls: ['./machine-form-dialog.css'],
})
export class MachineFormDialog implements OnInit {
  form: FormGroup;
  maxPurchaseDate = new Date();
  maxYear = new Date().getFullYear();
  employees: EmployeeOption[] = [];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<MachineFormDialog>,
    private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public data: Machine | null,
  ) {
    this.form = this.fb.group({
      name: [
        data?.name || '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      ],
      type: [data?.type || '', [Validators.required, Validators.maxLength(50)]],
      brand: [data?.brand || '', [Validators.required, Validators.maxLength(50)]],
      model: [data?.model || '', [Validators.required, Validators.maxLength(50)]],
      serialNumber: [data?.serialNumber || '', [Validators.maxLength(100)]],
      manufacturingYear: [
        data?.manufacturingYear ?? null,
        [Validators.min(1900), Validators.max(new Date().getFullYear())],
      ],
      purchaseValue: [data?.purchaseValue ?? null, [Validators.min(Number.EPSILON)]],
      purchaseDate: [
        data?.purchaseDate ? new Date(data.purchaseDate) : null,
        [this.notFutureDateValidator()],
      ],
      status: [data?.status || 'OPERATIONAL', [Validators.required]],
      assignedEmployeeId: [data?.assignedEmployeeId || null],
    });
  }

  ngOnInit(): void {
    this.loadEmployees();
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      ...this.form.value,
      purchaseDate: this.form.value.purchaseDate
        ? this.formatDate(this.form.value.purchaseDate)
        : null,
      assignedEmployeeId: this.form.value.assignedEmployeeId || null,
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
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      if (value > today) {
        return { futureDate: true };
      }

      return null;
    };
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
