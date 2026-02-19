import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MentorFormComponent } from './MentorForm';
import { MentorService } from 'src/app/services/mentor/mentor.service';
import { of } from 'rxjs';

describe('MentorFormComponent', () => {
  let component: MentorFormComponent;
  let fixture: ComponentFixture<MentorFormComponent>;
  let mentorServiceMock: any;

  beforeEach(async () => {
    // Criamos um mock para o serviço
    mentorServiceMock = {
      createProfile: vi.fn().mockReturnValue(of({}))
    };

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MentorFormComponent],
      providers: [
        { provide: MentorService, useValue: mentorServiceMock }
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
    // Preenche o formulário
    component.mentorForm.setValue({
      specialty: 'Backend',
      experienceYears: 8,
      bio: 'Especialista em Java e Spring.',
      skills: 'Java, Spring Boot, PostgreSQL'
    });

    component.enviar();

    // Verifica se a lógica de transformação de string para array funcionou no envio
    expect(mentorServiceMock.createProfile).toHaveBeenCalledWith({
      specialty: 'Backend',
      experienceYears: 8,
      bio: 'Especialista em Java e Spring.',
      skills: ['Java', 'Spring Boot', 'PostgreSQL']
    });
  });

  it('não deve chamar o serviço se o formulário estiver inválido', () => {
    component.mentorForm.setValue({
      specialty: '', // Inválido (obrigatório)
      experienceYears: null,
      bio: '',
      skills: ''
    });

    component.enviar();

    expect(mentorServiceMock.createProfile).not.toHaveBeenCalled();
  });
});
