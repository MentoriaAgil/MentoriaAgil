import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { PerfilMentorService } from './perfil-mentor.service';
import { expect, it, describe, beforeEach, afterEach } from 'vitest';

describe('PerfilMentorService', () => {
  let service: PerfilMentorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PerfilMentorService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(PerfilMentorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve montar URL com parâmetros corretamente', () => {
    service.buscarMentores({
      areaPrincipal: 'TI',
      tipoMentoria: 'ACADEMICA'
    }).subscribe();

    // Ajustado para bater com a URL real: http://localhost:8080/api/mentors
    const req = httpMock.expectOne(req => 
      req.url.includes('/api/mentors') &&
      req.params.get('areaPrincipal') === 'TI' &&
      req.params.get('tipoMentoria') === 'ACADEMICA'
    );

    expect(req.request.method).toBe('GET');
    req.flush([]);
  });
});