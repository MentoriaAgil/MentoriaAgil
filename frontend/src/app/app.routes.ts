import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { DisponibilidadeComponent } from './services/disponibilidade/DisponibilidadeComponent';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./views/landing/Landing').then((m) => m.LandingPage),
    pathMatch: 'full',
  },
  {
    path: '',
    redirectTo: 'registro',
    pathMatch: 'full',
  },
  {
    path: 'termos',
    loadComponent: () => import('./views/termos/Termos').then((m) => m.Termos),
  },
  {
    path: 'registro',
    loadComponent: () => import('./views/auth/registro/Registro').then((m) => m.Registro),
  },
  {
    path: 'mentor/cadastro',
    loadComponent: () =>
      import('./views/mentor/mentor-form/MentorForm').then((m) => m.MentorFormComponent),
    canActivate: [AuthGuard],
    data: { role: 'MENTOR' },
  },
  {
    path: 'login',
    loadComponent: () => import('./views/auth/login/Login').then((m) => m.Login),
  },
  {
    path: 'logout',
    loadComponent: () => import('./views/auth/logout/Logout').then((m) => m.Logout),
  },

  {
    path: '',
    loadComponent: () => import('./layouts/Layout').then((m) => m.Layout),
    canActivate: [AuthGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./views/dashboard/mentor/DashboardMentor').then((m) => m.DashboardMentor),
        canActivate: [AuthGuard],
        data: { role: 'MENTOR' },
      },
      {
        path: 'mentores',
        loadComponent: () =>
          import('./views/mentores/mentor-list.component').then((m) => m.MentorListComponent),
        canActivate: [AuthGuard],
        data: { role: ['MENTOR', 'ESTUDANTE'] },
      },
      {
        path: 'dashboard/mentor/disponibilidade',
        component: DisponibilidadeComponent,
        canActivate: [AuthGuard],
      },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },

  {
    path: '**',
    redirectTo: 'registro',
  },
];
