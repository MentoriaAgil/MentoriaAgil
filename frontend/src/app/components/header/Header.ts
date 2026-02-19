import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html',
})
export class Header {
  isMenuOpen = signal(false);

  constructor(
    private router: Router,
    private authService: AuthService,
  ) {}

  toggleMenu() {
    this.isMenuOpen.set(!this.isMenuOpen());
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
