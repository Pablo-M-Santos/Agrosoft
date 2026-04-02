import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from '../../shared/components/card/card';
import { DataTable, TableColumn } from '../../shared/components/data-table/data-table';
import { Animal } from '../../core/models/animal.model';
import { AnimalService, AnimalStats } from '../../core/services/animal.service';

@Component({
  selector: 'app-animals',
  standalone: true,
  imports: [CommonModule, Card, DataTable],
  templateUrl: './animais.html',
  styleUrls: ['./animais.css']
})
export class Animals implements OnInit {

  columns: TableColumn<Animal>[] = [
    { key: 'name', label: 'NOME', type: 'text' },
    { key: 'animalTypeName', label: 'TIPO', type: 'text' },
    { key: 'weight', label: 'PESO (KG)', type: 'text' },
    { key: 'responsibleEmployeeName', label: 'RESPONSÁVEL', type: 'text' },
    { key: 'statusText', label: 'STATUS', type: 'status' },
    { key: 'actions', label: 'AÇÕES', type: 'actions' }
  ];

  animals: Animal[] = [];

  statusTranslation: { [key: string]: string } = {
    ACTIVE: 'Na Fazenda',
    SOLD: 'Vendido',
    UNDER_CARE: 'Em Tratamento'
  };

  statusMap = this.statusTranslation;

  statusColors: { [key: string]: { background: string, text: string } } = {
    ACTIVE: { background: '#DCFCE7', text: '#16A34A' },
    SOLD: { background: '#FEE2E2', text: '#DC2626' },
    UNDER_CARE: { background: '#FEF3C7', text: '#D97706' }
  };

  cards: AnimalStats = {
    total: 0,
    active: 0,
    sold: 0,
    underCare: 0
  };

  constructor(
    private animalService: AnimalService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {

    this.animalService.list().subscribe({
      next: data => {
        this.animals = data.map(animal => ({
          ...animal,
          statusText: this.statusTranslation[animal.status] || animal.status,
          statusColor: this.statusColors[animal.status]?.background || '#E5E7EB',
          statusTextColor: this.statusColors[animal.status]?.text || '#374151',
          responsibleEmployeeName: animal.responsibleEmployeeName ?? 'Sem vínculo'
        }));
        this.cdr.detectChanges();
      },
      error: err => console.error('Erro ao carregar animais', err)
    });

    this.animalService.getStats().subscribe({
      next: stats => {
        this.cards = stats;
        this.cdr.detectChanges();
      },
      error: err => console.error('Erro ao carregar estatísticas', err)
    });
  }

  onEdit(animal: Animal): void {
    console.log('Editar animal', animal);
  }

  onDelete(animal: Animal): void {
    console.log('Excluir animal', animal);
  }
}
