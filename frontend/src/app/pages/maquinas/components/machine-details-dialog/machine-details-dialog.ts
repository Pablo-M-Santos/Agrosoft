import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Machine } from '../../../../core/models/machine.model';

@Component({
  selector: 'app-machine-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './machine-details-dialog.html',
  styleUrls: ['./machine-details-dialog.css'],
})
export class MachineDetailsDialog {
  constructor(
    private dialogRef: MatDialogRef<MachineDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public machine: Machine,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  getStatusLabel(status: string): string {
    const statusLabels: { [key: string]: string } = {
      OPERATIONAL: 'Operacional',
      UNDER_MAINTENANCE: 'Em Manutenção',
      OUT_OF_SERVICE: 'Fora de Serviço',
      INACTIVE: 'Inativa',
    };
    return statusLabels[status] || status;
  }

  formatDate(date: string | Date): string {
    if (!date) return '-';
    const d = new Date(date);
    return new Intl.DateTimeFormat('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    }).format(d);
  }

  formatCurrency(value: number | undefined): string {
    if (!value) return '-';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(value);
  }
}
