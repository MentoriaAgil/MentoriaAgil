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
    httpMock.verify();
  });

  it('deve montar a requisição POST adequadamente com o MentorDTO', () => {
    const mockMentor: MentorDTO = {
      specialty: 'Angular',
      experienceYears: 5,
      bio: 'Desenvolvedor focado em testes de unidade.',
      skills: ['Jest', 'TypeScript', 'Angular']
    };

    service.createProfile(mockMentor).subscribe();

    const req = httpMock.expectOne(req => req.url.endsWith('/api/mentors'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockMentor);

    req.flush({});
  });
});