import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'registro',
    pathMatch: 'full'

  },
  {
    path: 'termos',
    loadComponent: () => import('./views/termos/Termos')
    .then(m => m.Termos)
  },
  {
    path: 'registro',
    loadComponent: () => import('./views/auth/registro/Registro')
    .then(m => m.Registro)
  },
  {
    path: 'login',
    loadComponent: () => import('./views/auth/login/Login')
    .then(m => m.Login)
  },
  {
    path: 'logout',
    loadComponent: () => import('./views/auth/logout/Logout')
    .then(m => m.Logout)
  },

  {
    path: '',
    loadComponent: () => import('./layouts/Layout')
    .then(m => m.Layout),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./views/dashboard/Dashboard')
        .then(m => m.Dashboard)
      },

 {
  path: 'mentores',
  loadComponent: () =>
    import('./views/mentores/mentor-list.component')
      .then(m => m.MentorListComponent),
  canActivate: [AuthGuard]
},
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },

  {
    path: '**',
    redirectTo: 'registro'
  }
];
