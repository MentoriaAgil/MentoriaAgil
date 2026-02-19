export interface PerfilMentor {
  id: number;
  name: string;
  especializacao: string;
  experiencias: string;
  areaPrincipal: string;
  tipoMentoria: 'ACADEMICA' | 'PROFISSIONAL' | 'AMBAS';
  disponibilidade: 'DISPONIVEL' | 'OCUPADO';
  ativo: boolean;
}