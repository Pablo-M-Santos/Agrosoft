import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { UpdateUserPayload, User } from '../../../../core/models/user.model';

interface UserFormDialogData {
  user: User | null;
}

@Component({
  selector: 'app-user-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
  ],
  templateUrl: './user-form-dialog.html',
  styleUrls: ['./user-form-dialog.css'],
})
export class UserFormDialog {
  form: FormGroup;
  isEdit: boolean;

  accessLevels = [
    { value: 'ADMIN', label: 'Administrador' },
    { value: 'MANAGER', label: 'Gerente' },
    { value: 'OPERATOR', label: 'Operador' },
    { value: 'FINANCIAL', label: 'Financeiro' },
    { value: 'VIEWER', label: 'Visualizador' },
  ];

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<UserFormDialog>,
    @Inject(MAT_DIALOG_DATA) public data: UserFormDialogData,
  ) {
    this.isEdit = !!data?.user;

    this.form = this.fb.group({
      email: [data?.user?.email || '', [Validators.required, Validators.email]],
      accessLevel: [data?.user?.accessLevel || 'VIEWER', [Validators.required]],
      password: [
        '',
        this.isEdit ? [Validators.minLength(6)] : [Validators.required, Validators.minLength(6)],
      ],
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: UpdateUserPayload = {
      email: this.form.value.email,
      accessLevel: this.form.value.accessLevel,
    };

    const password = String(this.form.value.password || '').trim();
    if (password) {
      payload.password = password;
    }

    this.dialogRef.close(payload);
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
