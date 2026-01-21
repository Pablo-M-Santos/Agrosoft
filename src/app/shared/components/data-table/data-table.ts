import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

export interface TableColumn<T = any> {
  key: string;
  colorKey?: string;
  textColorKey?: string;
  label: string;
  type?: 'text' | 'status' | 'actions' | 'phone' | 'custom';
}

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './data-table.html',
  styleUrls: ['./data-table.css']
})
export class DataTable implements OnChanges {

  @Input() columns: TableColumn[] = [];
  @Input() dataSource: any[] = [];
  @Input() statusMap: { [key: string]: string } = {};


  @Output() edit = new EventEmitter<any>();
  @Output() delete = new EventEmitter<any>();

  displayedColumns: string[] = [];

  ngOnChanges(changes: SimpleChanges): void {

    if (changes['columns'] && this.columns.length) {
      this.displayedColumns = this.columns.map(col => col.key);
    }


    if (changes['dataSource'] && this.dataSource) {

    }
  }


  formatStatusClass(status: string): string {
    if (!status) return '';
    const key = status.toUpperCase().replace(/\s+/g, '_');
    return this.statusMap[key] || 'default-status';
  }

  formatPhone(phone: string): string {
    if (!phone) return '';


    const digits = phone.replace(/\D/g, '');


    if (digits.length === 11) {
      return `(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}`;
    }


    return phone;
  }


}
