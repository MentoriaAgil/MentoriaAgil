package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.model.Sessao;
import com.mentoria.agil.backend.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    public void notificarAgendamento(User mentor, User mentorado, Sessao sessao) {

        String mensagemMentor = String.format(
                "Nova sessão agendada com %s em %s das %s às %s. Formato: %s. %s",
                mentorado.getName(),
                sessao.getDataHoraInicio().toLocalDate(),
                sessao.getDataHoraInicio().toLocalTime(),
                sessao.getDataHoraFim().toLocalTime(),
                sessao.getFormato(),
                obterDetalhesLocal(sessao)
        );

        String mensagemMentorado = String.format(
                "Sessão agendada com %s em %s das %s às %s. Formato: %s. %s",
                mentor.getName(),
                sessao.getDataHoraInicio().toLocalDate(),
                sessao.getDataHoraInicio().toLocalTime(),
                sessao.getDataHoraFim().toLocalTime(),
                sessao.getFormato(),
                obterDetalhesLocal(sessao)
        );

        // Simula envio de e-mail
        logger.info("Enviando notificação para mentor {}: {}", mentor.getEmail(), mensagemMentor);
        logger.info("Enviando notificação para mentorado {}: {}", mentorado.getEmail(), mensagemMentorado);
    }

    private String obterDetalhesLocal(Sessao sessao) {
        if (sessao.getFormato() == com.mentoria.agil.backend.model.FormatoSessao.ONLINE) {
            return "Link da reunião: " + sessao.getLinkReuniao();
        } else {
            return "Endereço: " + sessao.getEndereco();
        }
    }
}
