import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { FinancialService } from '../../core/services/financeiro.service';
import { Financial } from '../../core/models/financeiro.model';

@Component({
  selector: 'app-financeiro',
  standalone: true,
  imports: [CommonModule, Card, DataTable],
  templateUrl: './financeiro.html',
  styleUrls: ['./financeiro.css']
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
      textColorKey: 'typeTextColor'
    },
    {
      key: 'categoryText',
      label: 'CATEGORIA',
      type: 'status',
      colorKey: 'categoryColor',
      textColorKey: 'categoryTextColor'
    },
    { key: 'actions', label: 'AÇÕES', type: 'actions' }
  ];


  transactions: Financial[] = [];


  statusTranslation: { [key: string]: string } = {
    REVENUES: 'Receita',
    REVENUE: 'Receita',
    EXPENSE: 'Despesa'
  };

  categoryTranslation: Record<string, string> = {
    PURCHASE: 'Compra',
    SALE: 'Venda',
    SALARY: 'Salário'
  };

  statusMap = this.statusTranslation;

  typeColors: Record<string, { background: string; text: string }> = {
    REVENUE: { background: '#DCFCE7', text: '#16A34A' },
    EXPENSE: { background: '#FEE2E2', text: '#DC2626' }
  };

  categoryColors: Record<string, { background: string; text: string }> = {
    PURCHASE: { background: '#DBEAFE', text: '#1E40AF' },
    SALE: { background: '#FEF9C3', text: '#854D0E' },
    SALARY: { background: '#F3E8FF', text: '#6B21A8' }
  };

  cards = {
    total: 0,
    revenues: 0,
    expenses: 0,
    balance: 0
  };

  constructor(
    private financialService: FinancialService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loadTransactions();
    this.loadStats();
  }

  private loadTransactions(): void {
    this.financialService.list().subscribe({
      next: data => {
        this.transactions = data.map(tx => {
          return {
            ...tx,

            amount: new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(Number(tx.amount)),
            transactionDate: new Date(tx.transactionDate).toLocaleDateString('pt-BR'),

            typeText: this.statusTranslation[tx.type] || tx.type,
            typeColor: this.typeColors[tx.type]?.background || '#F3F4F6',
            typeTextColor: this.typeColors[tx.type]?.text || '#1F2937',

            categoryText: this.categoryTranslation[tx.category] || tx.category,
            categoryColor: this.categoryColors[tx.category]?.background || '#F3F4F6',
            categoryTextColor: this.categoryColors[tx.category]?.text || '#1F2937',
          } as any;
        });
        this.cdr.detectChanges();
      }
    });
  }

  private loadStats(): void {
  this.financialService.getStats().subscribe({
    next: stats => {
      this.cards = {
        revenues: stats.revenues,
        expenses: stats.expenses,
        
        
        total: stats.total, 
        
        balance: stats.balance
      };
      this.cdr.detectChanges();
    },
    error: err => console.error('Erro ao carregar estatísticas', err)
  });
}
  onEdit(tx: Financial): void {
    console.log('Editar transação', tx);
  }

  onDelete(tx: Financial): void {
    console.log('Excluir transação', tx);
  }
}
