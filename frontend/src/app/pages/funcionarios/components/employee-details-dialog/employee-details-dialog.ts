import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { Employee } from '../../../../core/models/employee.model';

@Component({
  selector: 'app-employee-details-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule],
  templateUrl: './employee-details-dialog.html',
  styleUrls: ['./employee-details-dialog.css'],
})
export class EmployeeDetailsDialog {
  constructor(
    private dialogRef: MatDialogRef<EmployeeDetailsDialog>,
    @Inject(MAT_DIALOG_DATA) public employee: Employee,
  ) {}

  close(): void {
    this.dialogRef.close();
  }

  get relatedMachineryList(): string[] {
    if (!this.employee?.relatedMachinery) {
      return [];
    }

    return this.employee.relatedMachinery
      .split(',')
      .map((item) => item.trim())
      .filter((item) => !!item);
  }
}
