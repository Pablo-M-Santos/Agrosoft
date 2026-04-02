import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { Machine } from '../../core/models/machine.model';
import { MachineService, MachineStats } from '../../core/services/machine.service';

@Component({
  selector: 'app-maquina',
  standalone: true,
  imports: [CommonModule, Card, DataTable],
  templateUrl: './maquinas.html',
  styleUrls: ['./maquinas.css']
})
export class Maquinas implements OnInit {

  columns: TableColumn<Machine>[] = [
    { key: 'name', label: 'NOME', type: 'text' },
    { key: 'type', label: 'TIPO', type: 'text' },
    { key: 'brand', label: 'MARCA', type: 'text' },
    { key: 'model', label: 'MODELO', type: 'text' },
    { key: 'assignedEmployeeName', label: 'FUNCIONÁRIO', type: 'text' },
    { key: 'statusText', label: 'STATUS', type: 'status' },
    { key: 'actions', label: 'AÇÕES', type: 'actions' }
  ];

  machines: Machine[] = [];

  statusTranslation: { [key: string]: string } = {
    OPERATIONAL: 'Operacional',
    UNDER_MAINTENANCE: 'Em Manutenção',
    OUT_OF_SERVICE: 'Fora de Serviço',
    INACTIVE: 'Inativa'
  };

  statusMap = this.statusTranslation;

  statusClass: { [key: string]: string } = {
    OPERATIONAL: 'operacional',
    UNDER_MAINTENANCE: 'em-manutenção',
    OUT_OF_SERVICE: 'fora-de-serviço',
    INACTIVE: 'inativa'
  };

  statusColors: { [key: string]: { background: string, text: string } } = {
    OPERATIONAL: { background: '#DCFCE7', text: '#16A34A' },      
    UNDER_MAINTENANCE: { background: '#FEF3C7', text: '#D97706' }, 
    OUT_OF_SERVICE: { background: '#FEE2E2', text: '#DC2626' },  
    INACTIVE: { background: '#FEE2E2', text: '#DC2626' }           
  };


  cards: MachineStats = {
    total: 0,
    operational: 0,
    underMaintenance: 0,
    inactive: 0
  };

  constructor(private machineService: MachineService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.machineService.list().subscribe({
      next: data => {
        this.machines = data.map(machine => ({
          ...machine,
          statusText: this.statusTranslation[machine.status] || machine.status,
          statusColor: this.statusColors[machine.status]?.background || '#E5E7EB',
          statusTextColor: this.statusColors[machine.status]?.text || '#374151',
          assignedEmployeeName: machine.assignedEmployeeName ?? 'Sem vínculo'
        }));
        this.cdr.detectChanges();
      },
      error: err => console.error('Erro ao carregar máquinas', err)
    });

    this.machineService.getStats().subscribe({
      next: stats => {
        this.cards = stats;
        this.cdr.detectChanges();
      },
      error: err => console.error('Erro ao carregar estatísticas', err)
    });
  }

  onEdit(machine: Machine): void {
    console.log('Editar máquina', machine);
  }

  onDelete(machine: Machine): void {
    console.log('Excluir máquina', machine);
  }
}
