import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MentorListComponent } from './mentor-list.component';
import { PerfilMentorService } from '../../services/perfil-mentor.service';
import { of } from 'rxjs';

describe('MentorListComponent', () => {

  let component: MentorListComponent;
  let fixture: ComponentFixture<MentorListComponent>;

  const mockService = {
    buscarMentores: jasmine.createSpy().and.returnValue(of([
      {
        id: 1,
        name: 'Carlos',
        especializacao: 'TI',
        experiencias: '5 anos',
        areaPrincipal: 'TI',
        tipoMentoria: 'ACADEMICA',
        disponibilidade: 'DISPONIVEL',
        ativo: true
      },
      {
        id: 2,
        name: 'Ana',
        especializacao: 'Saúde',
        experiencias: '10 anos',
        areaPrincipal: 'SAUDE',
        tipoMentoria: 'PROFISSIONAL',
        disponibilidade: 'DISPONIVEL',
        ativo: true
      }
    ]))
  };

  beforeEach(async () => {

    await TestBed.configureTestingModule({
      imports: [MentorListComponent],
      providers: [
        { provide: PerfilMentorService, useValue: mockService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(MentorListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve renderizar 2 cards de mentores', () => {
    const compiled = fixture.nativeElement;
    const cards = compiled.querySelectorAll('.mentor-card');
    expect(cards.length).toBe(2);
  });

});