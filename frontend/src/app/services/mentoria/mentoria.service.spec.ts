import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { MentoriaService } from './mentoria.service';
import { SolicitacaoMentoriaRequest } from '@app/models/SolicitacaoMentoriaRequest';
import { SolicitacaoMentoriaResponse } from '@app/models/SolicitacaoMentoriaResponse';

describe('MentoriaService', () => {
  let service: MentoriaService;
  let httpMock: HttpTestingController;
  const apiUrl = 'http://localhost:8080/api/mentorias/request';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [MentoriaService],
    });
    service = TestBed.inject(MentoriaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve chamar o endpoint POST com os dados corretos', () => {
    const mockRequest: SolicitacaoMentoriaRequest = {
      mentorId: 1,
      message: 'Quero aprender Java',
    };

    const mockResponse: SolicitacaoMentoriaResponse = {
      id: 1,
      mentoradoId: 5,
      mentoradoNome: 'João Silva',
      mentoradoEmail: 'joao@email.com',
      mensagem: 'Gostaria de uma mentoria sobre Angular',
      status: 'PENDING',
      dataSolicitacao: '2024-03-22T10:00:00',
      dataHoraProposta: '2024-03-25T14:00:00',
      formato: 'ONLINE', // Ou 'PRESENCIAL'
      linkReuniao: 'https://meet.google.com/abc-defg-hij',
    };

    service.solicitarMentoria(mockRequest).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResponse);
  });

  it('deve tratar erros corretamente', () => {
    const mockRequest: SolicitacaoMentoriaRequest = {
      mentorId: 1,
      message: 'Teste',
    };

    service.solicitarMentoria(mockRequest).subscribe({
      next: () => expect.fail('Esperava um erro, mas obteve sucesso'),
      error: (error) => {
        expect(error.status).toBe(500);
      },
    });

    const req = httpMock.expectOne(apiUrl);
    req.flush('Erro interno', { status: 500, statusText: 'Internal Server Error' });
  });
});
