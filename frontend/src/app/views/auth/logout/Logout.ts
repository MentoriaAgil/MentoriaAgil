import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './logout.html',
})
export class Logout implements OnInit, OnDestroy {
  contador = signal(15);
  private timer: any;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.timer = setInterval(() => {
      if (this.contador() > 0) {
        this.contador.update(val => val - 1);
      } else {
        this.irParaLogin();
      }
    }, 1000);
  }

  irParaLogin(): void {
    if (this.timer) clearInterval(this.timer);
    this.router.navigate(['/login'], { replaceUrl: true });
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
  }
}
