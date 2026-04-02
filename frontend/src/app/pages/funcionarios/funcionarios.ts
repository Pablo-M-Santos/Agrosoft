import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { EmployeeService } from '../../core/services/employee.service';
import { Employee } from '../../core/models/employee.model';
import { MatDialog } from '@angular/material/dialog';
import { EmployeeFormDialog } from '../../shared/components/employee-form-dialog/employee-form-dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-funcionario',
  standalone: true,
  imports: [CommonModule, Card, DataTable, MatSnackBarModule, MatPaginatorModule, MatIconModule],
  templateUrl: './funcionarios.html',
  styleUrls: ['./funcionarios.css']
})
export class Funcionario implements OnInit {

  columns: TableColumn<Employee>[] = [
    { key: 'fullName', label: 'NOME', type: 'text' },
    { key: 'workArea', label: 'ÁREA', type: 'text' },
    { key: 'email', label: 'E-MAIL', type: 'text' },
    { key: 'phone', label: 'TELEFONE', type: 'phone' },
    { key: 'contractType', label: 'CONTRATO', type: 'text' },
    { key: 'statusText', label: 'STATUS', type: 'status' },
    { key: 'actions', label: 'AÇÕES', type: 'actions' }
  ];

  funcionarios: Employee[] = [];

  statusTranslation: { [key: string]: string } = {
    ACTIVE: 'Ativo',
    INACTIVE: 'Inativo',
    ON_LEAVE: 'Em Licença',
    TERMINATED: 'Rescindido'
  };

  statusMap = this.statusTranslation;

  statusClass: { [key: string]: string } = {
    ACTIVE: 'ativo',
    INACTIVE: 'inativo',
    ON_LEAVE: 'em-licenca',
    TERMINATED: 'terminado'
  };

  statusColors: { [key: string]: { background: string, text: string } } = {
    ACTIVE: { background: '#DCFCE7', text: '#16A34A' },
    INACTIVE: { background: '#FEE2E2', text: '#DC2626' },
    ON_LEAVE: { background: '#FEF3C7', text: '#D97706' },
    TERMINATED: { background: '#F3F4F6', text: '#6B7280' }
  };


  cards = {
    total: 0,
    active: 0,
    inactive: 0,
    onLeave: 0
  };

  totalElements = 0;
  currentPage = 0;
  pageSize = 9;

  constructor(private employeeService: EmployeeService,
    private cdr: ChangeDetectorRef, private dialog: MatDialog, private snackBar: MatSnackBar) { }


  ngOnInit(): void {

    this.loadEmployees();


    this.employeeService.getStats().subscribe({
      next: (stats) => {
        this.cards = stats;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erro ao carregar estatísticas', err)
    });
  }

  openDialog(employee?: Employee): void {
    const dialogRef = this.dialog.open(EmployeeFormDialog, {
      width: '100%',
      maxWidth: '1450px',
      data: employee ? { ...employee } : null,
      panelClass: 'custom-dialog-container'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if (result.id) {
          this.employeeService.update(result.id, result).subscribe({
            next: () => {
              this.showNotification('Funcionário atualizado com sucesso!');
              this.refreshData();
            },
            error: (err) => console.error('Erro ao atualizar', err)
          });
        } else {
          this.employeeService.create(result).subscribe({
            next: () => {
              this.showNotification('Funcionário cadastrado com sucesso!');
              this.refreshData();
            },
            error: (err) => console.error('Erro ao cadastrar', err)
          });
        }
      }
    });
  }

  onEdit(employee: Employee): void {
    console.log('Dados do funcionário para edição:', employee);
    this.openDialog(employee);
  }
  onDelete(employee: Employee): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '650px',
      data: {
        title: 'Deletar Funcionário',
        itemName: employee.fullName
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.employeeService.delete(employee.id!).subscribe({
          next: () => {
            this.showNotification('Funcionário excluído com sucesso!');
            this.refreshData();
          },
          error: (err) => {
            console.error('Erro ao excluir', err);
            this.showNotification('Erro ao excluir funcionário.', true);
          }
        });
      }
    });
  }

  private showNotification(message: string, isError: boolean = false): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 3500,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: isError ? ['snackbar-error'] : ['snackbar-success']
    });
  }

  private refreshData(): void {
    this.loadEmployees();

    this.employeeService.getStats().subscribe({
      next: (stats) => {
        this.cards = stats;
        this.cdr.detectChanges();
      }
    });
  }

  private loadEmployees(): void {

    this.employeeService.list(this.currentPage, this.pageSize).subscribe({
      next: (response: any) => {

        this.funcionarios = response.content.map((emp: any) => ({
          ...emp,
          statusText: this.statusTranslation[emp.status] || emp.status,
          statusColor: this.statusColors[emp.status]?.background || '#E5E7EB',
          statusTextColor: this.statusColors[emp.status]?.text || '#374151'
        }));

        this.totalElements = response.totalElements;
        this.cdr.detectChanges();
      }
    });
  }



  get pageNumbers(): number[] {
    const totalPages = Math.ceil(this.totalElements / this.pageSize);
    return Array.from({ length: totalPages }, (_, i) => i);
  }


  goToPage(pageIndex: number): void {
    this.currentPage = pageIndex;
    this.loadEmployees();
  }
  onPageChange(pageIndex: number): void {
    this.currentPage = pageIndex;
    this.loadEmployees();
  }

}