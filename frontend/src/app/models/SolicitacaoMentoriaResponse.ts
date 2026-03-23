export interface SolicitacaoMentoriaResponse {
  id: number;
  mentoradoId: number;
  mentoradoNome: string;
  mentoradoEmail: string;
  mensagem: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  dataSolicitacao: string;
  dataHoraProposta: string;
  formato: 'ONLINE' | 'PRESENCIAL';
  linkReuniao?: string;
  endereco?: string;
}
