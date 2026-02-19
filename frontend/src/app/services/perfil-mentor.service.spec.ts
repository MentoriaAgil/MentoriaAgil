import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PerfilMentorService } from './perfil-mentor.service';

describe('PerfilMentorService', () => {

  let service: PerfilMentorService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(PerfilMentorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('deve montar URL com parâmetros corretamente', () => {

    service.buscarMentores({
      areaPrincipal: 'TI',
      tipoMentoria: 'ACADEMICA'
    }).subscribe();

    const req = httpMock.expectOne(req =>
      req.url.includes('/api/mentors') &&
      req.params.get('areaPrincipal') === 'TI' &&
      req.params.get('tipoMentoria') === 'ACADEMICA'
    );

    expect(req.request.method).toBe('GET');
  });

});