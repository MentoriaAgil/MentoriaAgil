import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

export const AuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  const requiredRole = route.data['role'];
  if (requiredRole && !authService.hasRole(requiredRole)) {
    const user = JSON.parse(localStorage.getItem('auth_user') || '{}');
    if (user.role === 'ESTUDANTE') {
      router.navigate(['/mentores']);
    } else {
      router.navigate(['/']);
    }
    return false;
  }

  return true;
};
