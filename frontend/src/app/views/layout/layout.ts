import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../../components/header/header';
import { Component } from '@angular/core';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HeaderComponent],
  template: `
    <div class="min-h-screen bg-background-light">
      <app-header></app-header>
      
      <main class="max-w-[1440px] mx-auto p-8">
        <router-outlet></router-outlet>
      </main>
    </div>
  `
})
export class LayoutComponent {}