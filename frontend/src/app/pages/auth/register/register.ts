import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../core/auth/auth.service';
import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatSnackBarModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class Register {
  form: FormGroup;
  loading = false;
  showPassword = false;
  showConfirmPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
    });
  }

  submit(): void {
    if (this.form.invalid || this.loading) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.form.value.password !== this.form.value.confirmPassword) {
      this.showNotification('As senhas não conferem.', true);
      return;
    }

    this.loading = true;

    const email = this.form.value.email;
    const password = this.form.value.password;

    this.authService
      .register({
        email,
        password,
      })
      .pipe(switchMap(() => this.authService.login({ email, password })))
      .subscribe({
        next: () => {
          this.showNotification('Cadastro realizado com sucesso!');
          this.router.navigate(['/usuarios']);
        },
        error: (err) => {
          const message = err?.error?.message || 'Falha ao cadastrar usuário.';
          this.showNotification(message, true);
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        },
      });
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

  get passwordsMatch(): boolean {
    const password = String(this.form.get('password')?.value || '');
    const confirm = String(this.form.get('confirmPassword')?.value || '');
    return !!password && !!confirm && password === confirm;
  }

  get showPasswordMismatch(): boolean {
    const password = String(this.form.get('password')?.value || '');
    const confirmControl = this.form.get('confirmPassword');
    const confirm = String(confirmControl?.value || '');

    if (!password || !confirm || !confirmControl) {
      return false;
    }

    return (confirmControl.touched || this.form.touched) && password !== confirm;
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  private showNotification(message: string, isError = false): void {
    this.snackBar.open(message, 'Fechar', {
      duration: 4000,
      panelClass: [isError ? 'snackbar-error' : 'snackbar-success'],
    });
  }
}
