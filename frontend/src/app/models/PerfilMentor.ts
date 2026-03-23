export interface PerfilMentor {
  id: number;
  userId: number;
  name: string;
  email: string;
  formacao: string;
  especializacao: string;
  experiencias: string;
  areaPrincipal: string;
  tipoMentoria: 'ACADEMICA' | 'PROFISSIONAL' | 'AMBAS';
  disponibilidade: 'DISPONIVEL' | 'OCUPADO';
  ativo: boolean;
}