import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { PaginationComponent } from '../../shared/components/pagination/pagination';
import { Machine } from '../../core/models/machine.model';
import { MachineService, MachineStats } from '../../core/services/machine.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { MachineFormDialog } from '../../shared/components/machine-form-dialog/machine-form-dialog';
import { MachineDetailsDialog } from '../../shared/components/machine-details-dialog/machine-details-dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { HttpErrorResponse } from '@angular/common/http';

type MachineListItem = Machine & {
  statusText?: string;
  statusColor?: string;
  statusTextColor?: string;
};

@Component({
  selector: 'app-maquina',
  standalone: true,
  imports: [CommonModule, Card, DataTable, PaginationComponent, MatSnackBarModule],
  templateUrl: './maquinas.html',
  styleUrls: ['./maquinas.css'],
})
export class Maquinas implements OnInit {
  columns: TableColumn<Machine>[] = [
    { key: 'name', label: 'NOME', type: 'text' },
    { key: 'type', label: 'TIPO', type: 'text' },
    { key: 'brand', label: 'MARCA', type: 'text' },
    { key: 'assignedEmployeeName', label: 'RESPONSÁVEL', type: 'text' },
    { key: 'statusText', label: 'STATUS', type: 'status' },
    { key: 'actions', label: 'AÇÕES', type: 'actions' },
  ];

  machines: MachineListItem[] = [];
  allMachines: MachineListItem[] = [];
  totalElements = 0;
  currentPage = 0;
  pageSize = 9;

  statusTranslation: { [key: string]: string } = {
    OPERATIONAL: 'Operacional',
    UNDER_MAINTENANCE: 'Em Manutenção',
    OUT_OF_SERVICE: 'Fora de Serviço',
    INACTIVE: 'Inativa',
  };

  statusMap = this.statusTranslation;

  statusColors: { [key: string]: { background: string; text: string } } = {
    OPERATIONAL: { background: '#DCFCE7', text: '#16A34A' },
    UNDER_MAINTENANCE: { background: '#FEF3C7', text: '#D97706' },
    OUT_OF_SERVICE: { background: '#FEE2E2', text: '#DC2626' },
    INACTIVE: { background: '#FEE2E2', text: '#DC2626' },
  };

  cards: MachineStats = {
    total: 0,
    operational: 0,
    underMaintenance: 0,
    inactive: 0,
  };

  constructor(
    private machineService: MachineService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
  ) {}

  ngOnInit(): void {
    this.loadMachines();
    this.loadStats();
  }

  onView(machine: Machine): void {
    this.dialog.open(MachineDetailsDialog, {
      width: '100%',
      maxWidth: '600px',
      data: machine,
      panelClass: 'custom-dialog-container',
    });
  }

  onEdit(machine: Machine): void {
    const dialogRef = this.dialog.open(MachineFormDialog, {
      width: '100%',
      maxWidth: '980px',
      data: machine,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.machineService.update(machine.id, result).subscribe({
        next: () => {
          this.showNotification('Máquina atualizada com sucesso!');
          this.loadMachines();
          this.loadStats();
        },
        error: (err) => this.handleSaveError(err, 'atualizar máquina'),
      });
    });
  }

  onDelete(machine: Machine): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '650px',
      data: {
        title: 'Desativar Máquina',
        itemName: machine.name,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.machineService.deactivate(machine.id).subscribe({
          next: () => {
            this.showNotification('Máquina desativada com sucesso!');
            this.loadMachines();
            this.loadStats();
          },
          error: (err) => {
            console.error('Erro ao desativar', err);
            this.showNotification('Erro ao desativar máquina.', true);
          },
        });
      }
    });
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(MachineFormDialog, {
      width: '100%',
      maxWidth: '980px',
      data: null,
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.machineService.create(result).subscribe({
        next: () => {
          this.showNotification('Máquina cadastrada com sucesso!');
          this.loadMachines();
          this.loadStats();
        },
        error: (err) => this.handleSaveError(err, 'cadastrar máquina'),
      });
    });
  }

  onPageChange(pageIndex: number): void {
    this.currentPage = pageIndex;
    this.updateVisibleMachines();
  }

  private loadMachines(): void {
    this.machineService.list().subscribe({
      next: (data) => {
        this.allMachines = data
          .map((machine) => ({
            ...machine,
            statusText: this.statusTranslation[machine.status] || machine.status,
            statusColor: this.statusColors[machine.status]?.background || '#E5E7EB',
            statusTextColor: this.statusColors[machine.status]?.text || '#374151',
            assignedEmployeeName: machine.assignedEmployeeName ?? 'Sem vínculo',
          }))
          .sort((a, b) => {
            const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
            const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
            return dateB - dateA;
          });
        this.totalElements = this.allMachines.length;
        this.currentPage = 0;
        this.updateVisibleMachines();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar máquinas', err);
        this.showNotification('Erro ao carregar máquinas.', true);
      },
    });
  }

  private loadStats(): void {
    this.machineService.getStats().subscribe({
      next: (stats) => {
        this.cards = stats;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar estatísticas', err);
        this.showNotification('Erro ao carregar estatísticas das máquinas.', true);
      },
    });
  }

  private updateVisibleMachines(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.machines = this.allMachines.slice(startIndex, endIndex);
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
}
