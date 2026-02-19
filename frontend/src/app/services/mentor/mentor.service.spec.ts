import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { MentorService } from './mentor.service';
import { MentorDTO } from 'src/app/models/Mentor';

describe('MentorService', () => {
  let service: MentorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MentorService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(MentorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Garante que não há requisições pendentes
  });

  it('deve montar a requisição POST adequadamente com o MentorDTO', () => {
    const mockMentor: MentorDTO = {
      specialty: 'Angular',
      experienceYears: 5,
      bio: 'Desenvolvedor focado em testes de unidade.',
      skills: ['Jest', 'TypeScript', 'Angular']
    };

    service.createProfile(mockMentor).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/mentores');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockMentor);

    req.flush({}); // Simula uma resposta de sucesso
  });
});
