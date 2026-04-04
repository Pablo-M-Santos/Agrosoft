import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { PaginationComponent } from '../../shared/components/pagination/pagination';
import { FinancialService } from '../../core/services/financeiro.service';
import { Financial } from '../../core/models/financeiro.model';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { FinancialFormDialog } from '../../shared/components/financial-form-dialog/financial-form-dialog';
import { FinancialDetailsDialog } from '../../shared/components/financial-details-dialog/financial-details-dialog';

type FinancialListItem = Financial;

@Component({
  selector: 'app-financeiro',
  standalone: true,
  imports: [CommonModule, Card, DataTable, PaginationComponent, MatSnackBarModule],
  templateUrl: './financeiro.html',
  styleUrls: ['./financeiro.css'],
})
export class Financeiro implements OnInit {
  columns: TableColumn<Financial>[] = [
    { key: 'description', label: 'DESCRIÇÃO', type: 'text' },
    { key: 'transactionDate', label: 'DATA', type: 'text' },
    { key: 'amount', label: 'VALOR', type: 'text' },

    {
      key: 'typeText',
      label: 'TIPO',
      type: 'status',
      colorKey: 'typeColor',
      textColorKey: 'typeTextColor',
    },
    {
      key: 'categoryText',
      label: 'CATEGORIA',
      type: 'status',
      colorKey: 'categoryColor',
      textColorKey: 'categoryTextColor',
    },
    { key: 'actions', label: 'AÇÕES', type: 'actions' },
  ];

  transactions: FinancialListItem[] = [];
  allTransactions: FinancialListItem[] = [];
  totalElements = 0;
  currentPage = 0;
  pageSize = 9;

  statusTranslation: { [key: string]: string } = {
    REVENUE: 'Receita',
    EXPENSE: 'Despesa',
  };

  categoryTranslation: Record<string, string> = {
    PURCHASE: 'Compra',
    SALE: 'Venda',
    SALARY: 'Salário',
  };

  statusMap = this.statusTranslation;

  typeColors: Record<string, { background: string; text: string }> = {
    REVENUE: { background: '#DCFCE7', text: '#16A34A' },
    EXPENSE: { background: '#FEE2E2', text: '#DC2626' },
  };

  categoryColors: Record<string, { background: string; text: string }> = {
    PURCHASE: { background: '#DBEAFE', text: '#1E40AF' },
    SALE: { background: '#FEF9C3', text: '#854D0E' },
    SALARY: { background: '#F3E8FF', text: '#6B21A8' },
  };

  cards = {
    total: 0,
    revenues: 0,
    expenses: 0,
    balance: 0,
  };

  constructor(
    private financialService: FinancialService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadTransactions();
    this.loadStats();
  }

  private loadTransactions(): void {
    this.financialService.list().subscribe({
      next: (data) => {
        this.allTransactions = data
          .map((tx) => {
            const normalizedType = this.normalizeType(tx.type);
            const normalizedCategory = this.normalizeCategory(tx.category);

            return {
              ...tx,

              amount: new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(
                Number(tx.amount),
              ),
              transactionDate: new Date(tx.transactionDate).toLocaleDateString('pt-BR'),

              typeText: this.statusTranslation[normalizedType] || normalizedType,
              typeColor: this.typeColors[normalizedType]?.background || '#F3F4F6',
              typeTextColor: this.typeColors[normalizedType]?.text || '#1F2937',

              categoryText: this.categoryTranslation[normalizedCategory] || normalizedCategory,
              categoryColor: this.categoryColors[normalizedCategory]?.background || '#F3F4F6',
              categoryTextColor: this.categoryColors[normalizedCategory]?.text || '#1F2937',
            } as any;
          })
          .sort((a, b) => {
            const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
            const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
            return dateB - dateA;
          });

        this.totalElements = this.allTransactions.length;
        this.currentPage = 0;
        this.updateVisibleTransactions();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar transações', err);
        this.showNotification('Erro ao carregar transações.', true);
      },
    });
  }

  private loadStats(): void {
    this.financialService.getStats().subscribe({
      next: (stats) => {
        this.cards = {
          revenues: stats.revenues,
          expenses: stats.expenses,
          total: stats.total,
          balance: stats.balance,
        };
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar estatísticas', err);
        this.showNotification('Erro ao carregar estatísticas.', true);
      },
    });
  }

  onView(tx: Financial): void {
    this.dialog.open(FinancialDetailsDialog, {
      width: '100%',
      maxWidth: '600px',
      data: tx,
      panelClass: 'custom-dialog-container',
    });
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(FinancialFormDialog, {
      width: '100%',
      maxWidth: '980px',
      data: null,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.financialService.create(result).subscribe({
        next: () => {
          this.showNotification('Transação cadastrada com sucesso!');
          this.loadTransactions();
          this.loadStats();
        },
        error: (err) => this.handleSaveError(err, 'cadastrar transação'),
      });
    });
  }

  onEdit(tx: Financial): void {
    const dialogRef = this.dialog.open(FinancialFormDialog, {
      width: '100%',
      maxWidth: '980px',
      data: tx,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.financialService.update(tx.id, result).subscribe({
        next: () => {
          this.showNotification('Transação atualizada com sucesso!');
          this.loadTransactions();
          this.loadStats();
        },
        error: (err) => this.handleSaveError(err, 'atualizar transação'),
      });
    });
  }

  onDelete(tx: Financial): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '650px',
      data: {
        title: 'Excluir Transação',
        itemName: tx.description,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.financialService.delete(tx.id).subscribe({
        next: () => {
          this.showNotification('Transação excluída com sucesso!');
          this.loadTransactions();
          this.loadStats();
        },
        error: (err) => {
          console.error('Erro ao excluir transação', err);
          this.showNotification('Erro ao excluir transação.', true);
        },
      });
    });
  }

  onPageChange(pageIndex: number): void {
    this.currentPage = pageIndex;
    this.updateVisibleTransactions();
  }

  private updateVisibleTransactions(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.transactions = this.allTransactions.slice(startIndex, endIndex);
  }

  private showNotification(message: string, isError = false): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 4000,
      panelClass: [isError ? 'snackbar-error' : 'snackbar-success'],
    });
  }

  private handleSaveError(err: any, action: string): void {
    console.error(`Erro ao ${action}`, err);

    if (err instanceof HttpErrorResponse && err.error?.errors) {
      const errorMessages = Object.values(err.error.errors).flat();
      const message = (errorMessages as string[]).join(', ') || `Erro ao ${action}.`;
      this.showNotification(message, true);
    } else {
      this.showNotification(`Erro ao ${action}.`, true);
    }
  }

  private normalizeType(type: string): string {
    const normalized = (type || '').toUpperCase();
    return normalized === 'REVENUES' ? 'REVENUE' : normalized;
  }

  private normalizeCategory(category: string): string {
    return (category || '').toUpperCase();
  }
}
