import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.html'
})
export class HeaderComponent {
  // Controle dropdown perfil
  isMenuOpen = signal(false);

  constructor(private router: Router) {}

  toggleMenu() {
    this.isMenuOpen.set(!this.isMenuOpen());
  }

  logout() {
    // Redireciona para a view de logout
    this.router.navigate(['/logout']);
  }
}