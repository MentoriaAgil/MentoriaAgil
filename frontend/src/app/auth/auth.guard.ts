import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

export const AuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    console.warn('Usuário não autenticado. Redirecionando para /login');
    return router.createUrlTree(['/login']);
  }

  const requiredRole = route.data?.['role'] as 'ADMIN' | 'USER' | undefined;

  if (requiredRole && !authService.hasRole(requiredRole)) {
    console.warn('Usuário não possui permissão suficiente.');
    return router.createUrlTree(['/unauthorized']);
  }

  return true;
};
