import { Component, inject } from '@angular/core';
import { CommonModule, AsyncPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MentorService } from '../../../services/mentor/mentor.service';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-mentor-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, AsyncPipe],
  templateUrl: './mentor-form.html'
})
export class MentorFormComponent {
  private fb = inject(FormBuilder);
  private mentorService = inject(MentorService);
  private router = inject(Router);
  public authService = inject(AuthService);

  errorMessage: string | null = null;

  mentorForm = this.fb.group({
    specialty: ['', [Validators.required]],
    experienceYears: [0, [Validators.required, Validators.min(0)]],
    bio: ['', [Validators.required, Validators.maxLength(500)]],
    skills: ['', Validators.required]
  });

  onLogout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  enviar() {
    if (this.mentorForm.valid) {
      const formValue = this.mentorForm.value;
      const payload = {
        ...formValue,
        skills: typeof formValue.skills === 'string'
          ? formValue.skills.split(',').map(s => s.trim()).filter(s => s !== "")
          : []
      };

      this.mentorService.createProfile(payload as any).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: (err) => this.errorMessage = "Erro ao salvar perfil: " + (err.error?.message || "Servidor offline")
      });
    }
  }
}
