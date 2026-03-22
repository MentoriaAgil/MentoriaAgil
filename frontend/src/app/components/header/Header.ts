import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { toSignal } from '@angular/core/rxjs-interop'; // Importante para transformar Observable em Signal

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
})
export class Header {
  private readonly router = inject(Router);
  public readonly authService = inject(AuthService);

  isMenuOpen = signal(false);

  // TRANSFORMAÇÃO CORRETA: O Signal 'user' será atualizado automaticamente
  user = toSignal(this.authService.currentUser$);

  toggleMenu() {
    this.isMenuOpen.update(v => !v);
  }

  logout() {
    this.authService.logoutNoServidor().subscribe({
      next: () => this.limparERedirecionar(),
      error: () => this.limparERedirecionar()
    });
  }

  private limparERedirecionar() {
    this.authService.logout();
    this.router.navigate(['/logout']);
  }
}