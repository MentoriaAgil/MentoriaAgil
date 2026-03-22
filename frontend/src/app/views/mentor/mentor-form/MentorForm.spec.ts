import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MentorFormComponent } from './MentorForm';
import { MentorService } from '../../../services/mentor/mentor.service';
import { AuthService } from '../../../auth/auth.service';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { vi, describe, beforeEach, it, expect } from 'vitest';

describe('MentorFormComponent', () => {
  let component: MentorFormComponent;
  let fixture: ComponentFixture<MentorFormComponent>;
  let mentorServiceMock: any;
  let routerMock: any;
  let authServiceMock: any;

  beforeEach(async () => {
    mentorServiceMock = {
      createProfile: vi.fn().mockReturnValue(of({}))
    };
    
    routerMock = {
      navigate: vi.fn()
    };

    authServiceMock = {
      logout: vi.fn(),
      currentUser$: of(null)
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MentorFormComponent],
      providers: [
        { provide: MentorService, useValue: mentorServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: AuthService, useValue: authServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MentorFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve chamar o serviço com os dados mapeados ao submeter formulário válido', () => {
  component.mentorForm.setValue({
    specialty: 'Backend',
    experienceYears: 8,
    bio: 'Especialista em Java e Spring.',
    skills: 'Java, Spring Boot, PostgreSQL'
  });

  component.enviar();

  // Atualize a expectativa para o novo formato do payload
  expect(mentorServiceMock.createProfile).toHaveBeenCalledWith({
    especializacao: 'Backend',
    experiencias: '8 anos. Bio: Especialista em Java e Spring.',
    formacao: 'Java, Spring Boot, PostgreSQL'
  });
  
  expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
});

  it('não deve chamar o serviço se o formulário estiver inválido', () => {
    component.mentorForm.setValue({
      specialty: '',
      experienceYears: 0,
      bio: '',
      skills: ''
    });

    component.enviar();

    expect(mentorServiceMock.createProfile).not.toHaveBeenCalled();
  });
});