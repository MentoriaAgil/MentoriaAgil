package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class NotificacaoServiceTest {

    @InjectMocks
    private NotificacaoService notificacaoService;

    private User mentor;
    private User mentorado;
    private Sessao sessaoOnline;
    private Sessao sessaoPresencial;

    @BeforeEach
    void setUp() {
        mentor = new User("Carlos", "carlos@email.com", "senha");
        mentor.setId(1L);

        mentorado = new User("João", "joao@email.com", "senha");
        mentorado.setId(2L);

        sessaoOnline = new Sessao();
        sessaoOnline.setMentor(mentor);
        sessaoOnline.setMentorado(mentorado);
        sessaoOnline.setDataHoraInicio(LocalDateTime.of(2025, 3, 20, 10, 0));
        sessaoOnline.setDataHoraFim(LocalDateTime.of(2025, 3, 20, 11, 0));
        sessaoOnline.setFormato(FormatoSessao.ONLINE);
        sessaoOnline.setLinkReuniao("https://meet.google.com/abc-xyz");

        sessaoPresencial = new Sessao();
        sessaoPresencial.setMentor(mentor);
        sessaoPresencial.setMentorado(mentorado);
        sessaoPresencial.setDataHoraInicio(LocalDateTime.of(2025, 3, 21, 14, 0));
        sessaoPresencial.setDataHoraFim(LocalDateTime.of(2025, 3, 21, 15, 0));
        sessaoPresencial.setFormato(FormatoSessao.PRESENCIAL);
        sessaoPresencial.setEndereco("Rua de Exemplo, 123");
    }

    @Test
    void deveLogarNotificacaoParaSessaoOnline() {
        notificacaoService.notificarAgendamento(mentor, mentorado, sessaoOnline);
        // Se chegou aqui, não houve exceção
    }

    @Test
    void deveLogarNotificacaoParaSessaoPresencial() {
        notificacaoService.notificarAgendamento(mentor, mentorado, sessaoPresencial);
    }
}
