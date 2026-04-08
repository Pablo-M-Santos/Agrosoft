import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'cadastro',
    canActivate: [guestGuard],
    loadComponent: () => import('./pages/auth/register/register').then((m) => m.Register),
  },
  {
    path: 'usuarios',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/usuario/usuario').then((m) => m.Usuario),
      },
    ],
  },
  {
    path: 'funcionarios',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/funcionarios/funcionarios').then((m) => m.Funcionario),
      },
    ],
  },
  {
    path: 'maquinas',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/maquinas/maquinas').then((m) => m.Maquinas),
      },
    ],
  },
  {
    path: 'animais',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/animais/animais').then((m) => m.Animals),
      },
    ],
  },
  {
    path: 'financeiro',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./pages/financeiro/financeiro').then((m) => m.Financeiro),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
