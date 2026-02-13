import { Routes } from '@angular/router';

export const routes: Routes = [
  { 
    path: 'cadastro', 
    loadComponent: () => import('./cadastro/cadastro').then(m => m.CadastroComponent) 
  },
  { 
    path: 'login', 
    loadComponent: () => import('./login/login').then(m => m.LoginComponent)
  },
  { 
    path: 'logout', 
    loadComponent: () => import('./views/logout/logout').then(m => m.LogoutView) 
  },
  
  {
    path: '',
    loadComponent: () => import('./views/layout/layout').then(m => m.LayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./views/dashboard/dashboard').then(m => m.DashboardView)
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  // Fallback: se não encontrar nada, manda para o cadastro
  { path: '**', redirectTo: 'cadastro' }
];