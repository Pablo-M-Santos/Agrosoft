import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { PaginationComponent } from '../../shared/components/pagination/pagination';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { HttpErrorResponse } from '@angular/common/http';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { UserService } from '../../core/services/user.service';
import { UpdateUserPayload, User } from '../../core/models/user.model';
import { UserFormDialog } from './components/user-form-dialog/user-form-dialog';
import { UserDetailsDialog } from './components/user-details-dialog/user-details-dialog';
import { NavigationEnd, Router } from '@angular/router';
import { Subject, filter, takeUntil } from 'rxjs';

type UserListItem = User & {
  accessLevelText: string;
  statusText: string;
  statusColor: string;
  statusTextColor: string;
  lastLoginText: string;
};

@Component({
  selector: 'app-usuario',
  standalone: true,
  imports: [CommonModule, Card, DataTable, PaginationComponent, MatSnackBarModule],
  templateUrl: './usuario.html',
  styleUrls: ['./usuario.css'],
})
export class Usuario implements OnInit, OnDestroy {
  columns: TableColumn<UserListItem>[] = [
    { key: 'email', label: 'E-MAIL', type: 'text' },
    { key: 'accessLevelText', label: 'NÍVEL DE ACESSO', type: 'text' },
    { key: 'lastLoginText', label: 'ÚLTIMO LOGIN', type: 'text' },
    { key: 'statusText', label: 'STATUS', type: 'status' },
    { key: 'actions', label: 'AÇÕES', type: 'actions' },
  ];

  users: UserListItem[] = [];
  allUsers: UserListItem[] = [];
  totalElements = 0;
  currentPage = 0;
  pageSize = 9;

  cards = {
    total: 0,
    active: 0,
    admins: 0,
    managers: 0,
  };

  statusMap: Record<string, string> = {
    ATIVO: 'ativo',
    INATIVO: 'inativo',
  };

  private readonly destroy$ = new Subject<void>();

  constructor(
    private userService: UserService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadUsers();

    this.router.events
      .pipe(
        filter((event): event is NavigationEnd => event instanceof NavigationEnd),
        filter((event) => event.urlAfterRedirects === '/usuarios'),
        takeUntil(this.destroy$),
      )
      .subscribe(() => this.loadUsers());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  openDialog(user?: User): void {
    const dialogRef = this.dialog.open(UserFormDialog, {
      width: '100%',
      maxWidth: '760px',
      data: { user: user || null },
      panelClass: 'custom-dialog-container',
    });

    dialogRef.afterClosed().subscribe((result: UpdateUserPayload | undefined) => {
      if (!result) {
        return;
      }

      if (user?.id) {
        this.userService.update(user.id, result).subscribe({
          next: () => {
            this.showNotification('Usuário atualizado com sucesso!');
            this.loadUsers();
          },
          error: (err) => this.handleSaveError(err, 'atualizar usuário'),
        });
        return;
      }

      if (!result.password) {
        this.showNotification('Senha é obrigatória para cadastrar usuário.', true);
        return;
      }

      this.userService
        .create({
          email: result.email,
          accessLevel: result.accessLevel,
          password: result.password,
        })
        .subscribe({
          next: () => {
            this.showNotification('Usuário cadastrado com sucesso!');
            this.loadUsers();
          },
          error: (err) => this.handleSaveError(err, 'cadastrar usuário'),
        });
    });
  }

  onView(user: User): void {
    this.dialog.open(UserDetailsDialog, {
      width: '100%',
      maxWidth: '760px',
      data: user,
      panelClass: 'custom-dialog-container',
    });
  }

  onEdit(user: User): void {
    this.openDialog(user);
  }

  onDelete(user: User): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '650px',
      data: {
        title: 'Desativar Usuário',
        itemName: user.email,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (!result) {
        return;
      }

      this.userService.delete(user.id).subscribe({
        next: () => {
          this.showNotification('Usuário desativado com sucesso!');
          this.loadUsers();
        },
        error: (err) => {
          console.error('Erro ao desativar usuário', err);
          this.showNotification('Erro ao desativar usuário.', true);
        },
      });
    });
  }

  onPageChange(pageIndex: number): void {
    this.currentPage = pageIndex;
    this.updateVisibleUsers();
  }

  private loadUsers(): void {
    this.userService.list().subscribe({
      next: (data) => {
        this.allUsers = data
          .map((user) => ({
            ...user,
            accessLevelText: this.getAccessLevelLabel(user.accessLevel),
            statusText: user.active ? 'Ativo' : 'Inativo',
            statusColor: user.active ? '#DCFCE7' : '#FEE2E2',
            statusTextColor: user.active ? '#16A34A' : '#DC2626',
            lastLoginText: this.formatLastLogin(user.lastLogin),
          }))
          .sort((a, b) => {
            const dateA = this.getSortTimestamp(a);
            const dateB = this.getSortTimestamp(b);
            return dateB - dateA;
          });

        this.totalElements = this.allUsers.length;
        this.currentPage = 0;
        this.updateVisibleUsers();
        this.updateCards();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erro ao carregar usuários', err);
        this.users = [];
        this.allUsers = [];
        this.totalElements = 0;
        this.showNotification('Erro ao carregar usuários.', true);
        this.cdr.detectChanges();
      },
    });
  }

  private updateVisibleUsers(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.users = this.allUsers.slice(startIndex, endIndex);
  }

  private updateCards(): void {
    this.cards = {
      total: this.allUsers.length,
      active: this.allUsers.filter((user) => user.active).length,
      admins: this.allUsers.filter((user) => user.accessLevel === 'ADMIN').length,
      managers: this.allUsers.filter((user) => user.accessLevel === 'MANAGER').length,
    };
  }

  private showNotification(message: string, isError = false): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 4000,
      panelClass: [isError ? 'snackbar-error' : 'snackbar-success'],
    });
  }

  private handleSaveError(error: unknown, action: string): void {
    const httpError = error as HttpErrorResponse;

    if (httpError?.status === 404 || httpError?.status === 405) {
      this.showNotification('Atualização de usuário ainda não está disponível no backend.', true);
      return;
    }

    const apiMessage = httpError?.error?.message;
    const runtimeMessage = typeof httpError?.error === 'string' ? httpError.error : '';

    this.showNotification(apiMessage || runtimeMessage || `Erro ao ${action}.`, true);
  }

  private getAccessLevelLabel(accessLevel: string): string {
    const labels: Record<string, string> = {
      ADMIN: 'Administrador',
      MANAGER: 'Gerente',
      OPERATOR: 'Operador',
      FINANCIAL: 'Financeiro',
      VIEWER: 'Visualizador',
    };

    return labels[accessLevel] || accessLevel;
  }

  private formatLastLogin(lastLogin?: string | null): string {
    if (!lastLogin) {
      return 'Nunca acessou';
    }

    return new Date(lastLogin).toLocaleString('pt-BR');
  }

  private getSortTimestamp(user: User): number {
    if (user.updatedAt) {
      return new Date(user.updatedAt).getTime();
    }

    if (user.createdAt) {
      return new Date(user.createdAt).getTime();
    }

    if (user.lastLogin) {
      return new Date(user.lastLogin).getTime();
    }

    return 0;
  }
}
