import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
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
import { Financial } from '../../../../core/models/financeiro.model';

@Component({
  selector: 'app-financial-form-dialog',
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
  templateUrl: './financial-form-dialog.html',
  styleUrls: ['./financial-form-dialog.css'],
})
export class FinancialFormDialog {
  form: FormGroup;
  maxTransactionDate = new Date();

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<FinancialFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: Financial | null,
  ) {
    this.form = this.fb.group({
      description: [
        data?.description || '',
        [Validators.required, Validators.minLength(3), Validators.maxLength(150)],
      ],
      amount: [
        typeof data?.amount === 'string' ? this.parseCurrency(data.amount) : (data?.amount ?? null),
        [Validators.required, Validators.min(0.01)],
      ],
      type: [data?.type || 'REVENUE', [Validators.required]],
      category: [data?.category || 'SALE', [Validators.required]],
      transactionDate: [
        data?.transactionDate ? this.parseDate(data.transactionDate) : new Date(),
        [Validators.required],
      ],
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = {
      ...this.form.value,
      amount: Number(this.form.value.amount),
      transactionDate: this.formatDate(this.form.value.transactionDate),
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

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private parseDate(value: string): Date {
    const parts = value.split('/');
    if (parts.length === 3) {
      const day = Number(parts[0]);
      const month = Number(parts[1]) - 1;
      const year = Number(parts[2]);
      return new Date(year, month, day);
    }

    return new Date(value);
  }

  private parseCurrency(value: string): number {
    const normalized = value
      .replace(/\./g, '')
      .replace(',', '.')
      .replace(/[^\d.-]/g, '');
    return Number(normalized) || 0;
  }
}
