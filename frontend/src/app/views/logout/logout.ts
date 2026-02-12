import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-logout-view',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './logout.html'
})
export class LogoutView implements OnInit, OnDestroy {
  contador = 15;
  private timer: any;

  ngOnInit(): void {
    this.timer = setInterval(() => {
      if (this.contador > 0) this.contador--;
    }, 1000);
  }

  ngOnDestroy(): void {
    if (this.timer) clearInterval(this.timer);
  }
}