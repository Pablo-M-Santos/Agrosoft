import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { catchError, map, Observable, of } from 'rxjs';
import { EmployeeService } from '../../../../core/services/employee.service';
import { Employee } from '../../../../core/models/employee.model';
import { Machine } from '../../../../core/models/machine.model';
import { MachineService } from '../../../../core/services/machine.service';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-employee-form-dialog',
  standalone: true,
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'pt-BR' },
    provideNativeDateAdapter(),
    provideNgxMask(),
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatAutocompleteModule,
    NgxMaskDirective,
  ],
  templateUrl: './employee-form-dialog.html',
  styleUrls: ['./employee-form-dialog.css'],
})
export class EmployeeFormDialog implements OnInit {
  form: FormGroup;
  isEdit: boolean;
  categoriesCNH = ['A', 'B', 'C', 'D', 'E', 'AB', 'AC', 'AD', 'AE'];
  maxBirthDate = this.getMaxBirthDate();
  machineSearchControl = new FormControl('');
  allMachines: Machine[] = [];
  filteredMachines: MachineSelectionOption[] = [];
  selectedMachines: MachineSelectionOption[] = [];
  isMachinesLoading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<EmployeeFormDialog>,
    private employeeService: EmployeeService,
    private machineService: MachineService,
    @Inject(MAT_DIALOG_DATA) public data: Employee,
  ) {
    this.isEdit = !!data;
    this.form = this.fb.group({
      id: [data?.id || null],
      fullName: [this.data?.fullName || '', [Validators.required, Validators.minLength(3)]],
      email: [
        this.data?.email || '',
        [
          Validators.required,
          Validators.email,
          Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$'),
        ],
        [this.emailAvailabilityValidator()],
      ],
      cpf: [
        this.data?.cpf || '',
        [Validators.required, this.cpfValidator()],
        [this.cpfAvailabilityValidator()],
      ],
      rg: [data?.rg || ''],
      phone: [
        data?.phone || '',
        [Validators.required, Validators.minLength(10), Validators.maxLength(15)],
      ],
      birthDate: [data?.birthDate || null, [Validators.required, this.minimumAgeValidator(18)]],
      workArea: [data?.workArea || ''],
      relatedMachinery: [data?.relatedMachinery || ''],
      salary: [data?.salary || 0, [Validators.required, Validators.min(Number.EPSILON)]],
      contractType: [data?.contractType || 'CLT', [Validators.required]],
      status: [data?.status || 'ACTIVE', [Validators.required]],
      driverLicenseCategory: [data?.driverLicenseCategory || null],
      hireDate: [data?.hireDate || new Date(), [Validators.required]],
      address: [data?.address || '', []],
    });
  }

  ngOnInit(): void {
    this.loadMachines();
    this.machineSearchControl.valueChanges.subscribe(() => this.filterMachines());
  }

  save(): void {
    if (this.form.pending) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload = { ...this.form.value };
    payload.relatedMachinery = this.selectedMachines.map((machine) => machine.name).join(', ');

    if (payload.birthDate) {
      payload.birthDate = this.formatDate(payload.birthDate);
    }
    if (payload.hireDate) {
      payload.hireDate = this.formatDate(payload.hireDate);
    }

    this.dialogRef.close(payload);
  }

  minimumAgeValidator(minAge: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }

      const value = new Date(control.value);
      if (Number.isNaN(value.getTime())) {
        return { invalidDate: true };
      }

      const today = new Date();
      const minAllowedDate = new Date(
        today.getFullYear() - minAge,
        today.getMonth(),
        today.getDate(),
      );
      minAllowedDate.setHours(0, 0, 0, 0);
      value.setHours(0, 0, 0, 0);

      return value <= minAllowedDate ? null : { minimumAge: { minAge } };
    };
  }

  cpfValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const rawValue = control.value;

      if (!rawValue) {
        return null;
      }

      const cpf = String(rawValue).replace(/\D/g, '');

      if (cpf.length !== 11) {
        return { cpfInvalid: true };
      }

      if (/^(\d)\1{10}$/.test(cpf)) {
        return { cpfInvalid: true };
      }

      const firstDigit = this.calculateCpfDigit(cpf.substring(0, 9), 10);
      const secondDigit = this.calculateCpfDigit(cpf.substring(0, 9) + firstDigit, 11);

      return cpf === `${cpf.substring(0, 9)}${firstDigit}${secondDigit}`
        ? null
        : { cpfInvalid: true };
    };
  }

  cpfAvailabilityValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const cpf = this.normalizeCpf(control.value);

      if (
        !cpf ||
        cpf.length !== 11 ||
        control.hasError('required') ||
        control.hasError('cpfInvalid')
      ) {
        return of(null);
      }

      return this.employeeService.checkCpfAvailability(cpf, this.data?.id).pipe(
        map((isAvailable) => (isAvailable ? null : { cpfTaken: true })),
        catchError(() => of(null)),
      );
    };
  }

  selectMachine(machine: MachineSelectionOption): void {
    if (this.selectedMachines.some((selected) => selected.id === machine.id)) {
      return;
    }

    this.selectedMachines = [...this.selectedMachines, machine];
    this.machineSearchControl.setValue('');
    this.filterMachines();
  }

  removeMachine(machineId: string): void {
    this.selectedMachines = this.selectedMachines.filter((machine) => machine.id !== machineId);
    this.filterMachines();
  }

  trackByMachineId(_: number, machine: MachineSelectionOption): string {
    return machine.id;
  }

  private loadMachines(): void {
    this.isMachinesLoading = true;

    this.machineService.list().subscribe({
      next: (machines) => {
        this.allMachines = [...machines].sort((a, b) => a.name.localeCompare(b.name, 'pt-BR'));
        this.restoreSelectedMachines();
        this.filterMachines();
        this.isMachinesLoading = false;
      },
      error: () => {
        this.allMachines = [];
        this.filteredMachines = [];
        this.isMachinesLoading = false;
      },
    });
  }

  private restoreSelectedMachines(): void {
    const relatedMachinery = String(this.form.get('relatedMachinery')?.value || '');

    if (!relatedMachinery.trim()) {
      this.selectedMachines = [];
      return;
    }

    const selectedNames = relatedMachinery
      .split(',')
      .map((name) => name.trim())
      .filter((name) => !!name);

    const machineByName = new Map(
      this.allMachines.map((machine) => [machine.name.trim().toLowerCase(), machine]),
    );

    this.selectedMachines = selectedNames.map((name) => {
      const foundMachine = machineByName.get(name.toLowerCase());

      if (foundMachine) {
        return foundMachine;
      }

      return {
        id: `legacy-${name}`,
        name,
        type: 'Vinculada',
        brand: '-',
        model: '-',
        status: 'ACTIVE',
      } as MachineSelectionOption;
    });
  }

  private filterMachines(): void {
    const query = String(this.machineSearchControl.value || '')
      .trim()
      .toLowerCase();

    const selectedIds = new Set(this.selectedMachines.map((machine) => machine.id));

    this.filteredMachines = this.allMachines
      .filter((machine) => !selectedIds.has(machine.id))
      .filter((machine) => {
        if (!query) {
          return true;
        }

        const text =
          `${machine.name} ${machine.type} ${machine.brand} ${machine.model}`.toLowerCase();
        return text.includes(query);
      })
      .slice(0, 12);
  }

  emailAvailabilityValidator(): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      const email = control.value;

      if (
        !email ||
        control.hasError('required') ||
        control.hasError('email') ||
        control.hasError('pattern')
      ) {
        return of(null);
      }

      return this.employeeService.checkEmailAvailability(email, this.data?.id).pipe(
        map((isAvailable) => (isAvailable ? null : { emailTaken: true })),
        catchError(() => of(null)),
      );
    };
  }

  private calculateCpfDigit(base: string, weight: number): number {
    let sum = 0;

    for (let i = 0; i < base.length; i++) {
      sum += Number(base.charAt(i)) * (weight - i);
    }

    const remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  }

  private normalizeCpf(value: unknown): string {
    return String(value || '').replace(/\D/g, '');
  }

  private getMaxBirthDate(): Date {
    const today = new Date();
    return new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
  }

  private formatDate(date: any): string {
    const d = new Date(date);
    return d.toISOString().split('T')[0];
  }

  close(): void {
    this.dialogRef.close();
  }

  hasError(controlName: string, errorName?: string): boolean {
    const control = this.form.get(controlName);

    if (!control) {
      return false;
    }

    if (!control.touched && !this.form.touched) {
      return false;
    }

    return errorName ? control.hasError(errorName) : control.invalid;
  }
}

type MachineSelectionOption = Pick<Machine, 'id' | 'name' | 'type' | 'brand' | 'model' | 'status'>;
