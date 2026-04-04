import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Animal } from '../../../core/models/animal.model';

@Component({
  selector: 'app-animal-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './animal-details-dialog.html',
  styleUrls: ['./animal-details-dialog.css'],
})
export class AnimalDetailsDialog {
  constructor(
    private dialogRef: MatDialogRef<AnimalDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public animal: Animal,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  getStatusLabel(status: string): string {
    const statusLabels: { [key: string]: string } = {
      ACTIVE: 'Na Fazenda',
      UNDER_CARE: 'Em Tratamento',
      SOLD: 'Vendido',
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
}
