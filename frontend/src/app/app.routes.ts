import { Routes } from '@angular/router';
import { MainLayout } from './layout/main-layout/main-layout';

export const routes: Routes = [
  {
    path: 'funcionarios',
    component: MainLayout,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/funcionarios/funcionarios')
            .then(m => m.Funcionario),
      }
    ]
  },
  {
    path: 'maquinas',
    component: MainLayout,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/maquinas/maquinas')
            .then(m => m.Maquinas),
      }
    ]
  },
  {
    path: 'animais',
    component: MainLayout,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/animais/animais')
            .then(m => m.Animals),
      }
    ]
  },
  {
    path: 'financeiro',
    component: MainLayout,
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./pages/financeiro/financeiro')
            .then(m => m.Financeiro),
      }
    ]
  },


];