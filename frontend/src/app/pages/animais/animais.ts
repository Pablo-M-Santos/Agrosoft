import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { PaginationComponent } from '../../shared/components/pagination/pagination';
import { Animal } from '../../core/models/animal.model';
import { AnimalService, AnimalStats } from '../../core/services/animal.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { AnimalFormDialog } from '../../shared/components/animal-form-dialog/animal-form-dialog';
import { HttpErrorResponse } from '@angular/common/http';

type AnimalListItem = Animal & {
  statusText?: string;
  statusColor?: string;
  statusTextColor?: string;
  responsibleEmployeeName?: string;
};

@Component({
  selector: 'app-animals',
  standalone: true,
  imports: [CommonModule, Card, DataTable, PaginationComponent, MatSnackBarModule],
  templateUrl: './animais.html',
  styleUrls: ['./animais.css'],
})
export class Animals implements OnInit {
  columns: TableColumn<Animal>[] = [
    { key: 'name', label: 'NOME', type: 'text' },
    { key: 'animalTypeName', label: 'TIPO', type: 'text' },
    { key: 'weight', label: 'PESO (KG)', type: 'text' },
    { key: 'responsibleEmployeeName', label: 'RESPONSÁVEL', type: 'text' },
    { key: 'statusText', label: 'STATUS', type: 'status' },
    { key: 'actions', label: 'AÇÕES', type: 'actions' },
  ];

  animals: AnimalListItem[] = [];
  allAnimals: AnimalListItem[] = [];
  totalElements = 0;
  currentPage = 0;
  pageSize = 9;

  statusTranslation: { [key: string]: string } = {
    ACTIVE: 'Na Fazenda',
    SOLD: 'Vendido',
    UNDER_CARE: 'Em Tratamento',
  };

  statusMap = this.statusTranslation;

  statusColors: { [key: string]: { background: string; text: string } } = {
    ACTIVE: { background: '#DCFCE7', text: '#16A34A' },
    SOLD: { background: '#FEE2E2', text: '#DC2626' },
    UNDER_CARE: { background: '#FEF3C7', text: '#D97706' },
  };

  cards: AnimalStats = {
    total: 0,
    active: 0,
    sold: 0,
    underCare: 0,
  };

  constructor(
    private animalService: AnimalService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadAnimals();
    this.loadStats();
  }

  onEdit(animal: Animal): void {
    console.log('Editar animal', animal);
  }

  onDelete(animal: Animal): void {
    console.log('Excluir animal', animal);
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(AnimalFormDialog, {
      width: '100%',
      maxWidth: '980px',
      data: null,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.animalService.create(result).subscribe({
        next: () => {
          this.showNotification('Animal cadastrado com sucesso!');
          this.loadAnimals();
          this.loadStats();
        },
        error: (err) => this.handleSaveError(err, 'cadastrar animal'),
      });
    });
  }

  onPageChange(pageIndex: number): void {
    this.currentPage = pageIndex;
    this.updateVisibleAnimals();
  }

  private loadAnimals(): void {
    this.animalService.list().subscribe({
      next: (data) => {
        this.allAnimals = data
          .map((animal) => ({
            ...animal,
            statusText: this.statusTranslation[animal.status] || animal.status,
            statusColor: this.statusColors[animal.status]?.background || '#E5E7EB',
            statusTextColor: this.statusColors[animal.status]?.text || '#374151',
            responsibleEmployeeName: animal.responsibleEmployeeName ?? 'Sem vínculo',
          }))
          .sort((a, b) => {
            const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
            const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
            return dateB - dateA;
          });
        this.totalElements = this.allAnimals.length;
        this.currentPage = 0;
        this.updateVisibleAnimals();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar animais', err);
        this.showNotification('Erro ao carregar animais.', true);
      },
    });
  }

  private loadStats(): void {
    this.animalService.getStats().subscribe({
      next: (stats) => {
        this.cards = stats;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar estatísticas', err);
        this.showNotification('Erro ao carregar estatísticas dos animais.', true);
      },
    });
  }

  private updateVisibleAnimals(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.animals = this.allAnimals.slice(startIndex, endIndex);
  }

  private showNotification(message: string, isError = false): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3500,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: isError ? ['snackbar-error'] : ['snackbar-success'],
    });
  }

  private handleSaveError(error: unknown, action: string): void {
    const httpError = error as HttpErrorResponse;
    const fieldErrors = httpError?.error?.fieldErrors;

    if (fieldErrors && typeof fieldErrors === 'object') {
      const messages = Object.values(fieldErrors).filter(Boolean);
      const message = messages.length ? messages.join(' ') : `Erro ao ${action}.`;
      this.showNotification(message, true);
      return;
    }

    const apiMessage = httpError?.error?.message;
    this.showNotification(apiMessage || `Erro ao ${action}.`, true);
  }
}
