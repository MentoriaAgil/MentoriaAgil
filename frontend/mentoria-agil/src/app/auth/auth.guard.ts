import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const allowedRoles = route.data?.['roles'] as string[] | undefined;

  if (allowedRoles && allowedRoles.length > 0) {
    const hasPermission = allowedRoles.some(role =>
      authService.hasRole(role)
    );

    if (!hasPermission) {
      return router.createUrlTree(['/forbidden']);
    }
  }

  return true;
};
