import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-user-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './user-details-dialog.html',
  styleUrls: ['./user-details-dialog.css'],
})
export class UserDetailsDialog {
  constructor(
    private dialogRef: MatDialogRef<UserDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public user: User,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  getAccessLevelLabel(accessLevel: string): string {
    const labels: Record<string, string> = {
      ADMIN: 'Administrador',
      MANAGER: 'Gerente',
      OPERATOR: 'Operador',
      FINANCIAL: 'Financeiro',
      VIEWER: 'Visualizador',
    };

    return labels[accessLevel] || accessLevel;
  }

  getStatusLabel(active: boolean): string {
    return active ? 'Ativo' : 'Inativo';
  }

  formatLastLogin(lastLogin?: string | null): string {
    if (!lastLogin) {
      return 'Nunca acessou';
    }

    return new Date(lastLogin).toLocaleString('pt-BR');
  }
}
