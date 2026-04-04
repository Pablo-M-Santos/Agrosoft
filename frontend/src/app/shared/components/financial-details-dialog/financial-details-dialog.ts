import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Financial } from '../../../core/models/financeiro.model';

@Component({
  selector: 'app-financial-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './financial-details-dialog.html',
  styleUrls: ['./financial-details-dialog.css'],
})
export class FinancialDetailsDialog {
  constructor(
    private dialogRef: MatDialogRef<FinancialDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public transaction: Financial,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  getTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      REVENUE: 'Receita',
      REVENUES: 'Receita',
      EXPENSE: 'Despesa',
    };
    return labels[type] || type;
  }

  getCategoryLabel(category: string): string {
    const labels: { [key: string]: string } = {
      PURCHASE: 'Compra',
      SALE: 'Venda',
      SALARY: 'Salário',
    };
    return labels[category] || category;
  }

  formatDate(date: string): string {
    if (!date) {
      return '-';
    }

    if (date.includes('/')) {
      return date;
    }

    return new Date(date).toLocaleDateString('pt-BR');
  }

  formatAmount(amount: number | string): string {
    const value = typeof amount === 'string' ? this.parseCurrency(amount) : amount;
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(
      value || 0,
    );
  }

  private parseCurrency(value: string): number {
    const normalized = value
      .replace(/\./g, '')
      .replace(',', '.')
      .replace(/[^\d.-]/g, '');
    return Number(normalized) || 0;
  }
}
