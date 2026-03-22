export interface SolicitacaoMentoriaResponse {
  id: number;
  mentoradoId: number;
  mentoradoNome: string;
  mentoradoEmail: string;
  mensagem: string;
  status: 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'CANCELLED';
  dataSolicitacao: string;
}