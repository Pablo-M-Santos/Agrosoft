import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { Employee } from '../../../core/models/employee.model';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-employee-form-dialog',
  standalone: true,
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' },
    provideNativeDateAdapter(),
    provideNgxMask()
  ],
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
    NgxMaskDirective
  ],
  templateUrl: './employee-form-dialog.html',
  styleUrls: ['./employee-form-dialog.css']
})
export class EmployeeFormDialog implements OnInit {
  form: FormGroup;
  isEdit: boolean;
  categoriesCNH = ['A', 'B', 'C', 'D', 'E', 'AB', 'AC', 'AD', 'AE'];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EmployeeFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Employee
  ) {
    this.isEdit = !!data;
    this.form = this.fb.group({
      id: [data?.id || null],
      fullName: [this.data?.fullName || '', [Validators.required, Validators.minLength(3)]],
      email: [this.data?.email || '', [Validators.required, Validators.email, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
      cpf: [this.data?.cpf || '', Validators.required],
      rg: [data?.rg || ''],
      phone: [data?.phone || '', [Validators.required, Validators.minLength(10), Validators.maxLength(15)]],
      birthDate: [data?.birthDate || null, [Validators.required]],
      workArea: [data?.workArea || ''],
      salary: [data?.salary || 0, [Validators.required, Validators.min(0)]],
      contractType: [data?.contractType || 'CLT', [Validators.required]],
      status: [data?.status || 'ACTIVE', [Validators.required]],
      driverLicenseCategory: [data?.driverLicenseCategory || null],
      hireDate: [data?.hireDate || new Date(), [Validators.required]],
      address: [data?.address || '', [Validators.required]]
    });
  }

  ngOnInit(): void { }

 save(): void {
    if (this.form.valid) {
      const payload = { ...this.form.value };
      

      if (payload.birthDate) {
        payload.birthDate = this.formatDate(payload.birthDate);
      }
      if (payload.hireDate) {
        payload.hireDate = this.formatDate(payload.hireDate);
      }

      this.dialogRef.close(payload);
    }
  }

  private formatDate(date: any): string {
    const d = new Date(date);
    return d.toISOString().split('T')[0]; 
  }

  close(): void {
    this.dialogRef.close();
  }
}