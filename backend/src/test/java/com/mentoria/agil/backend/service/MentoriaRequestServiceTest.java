package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.MentoriaRequestDTO;
import com.mentoria.agil.backend.dto.MentoriaRequestUpdateDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.MentoriaRequest;
import com.mentoria.agil.backend.model.MentoriaStatus;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.MentoriaRequestRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentoriaRequestServiceTest {

    @Mock
    private MentoriaRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MentoriaRequestService requestService;

    private User mentorado;
    private User mentor;
    private MentoriaRequestDTO dto;
    private MentoriaRequest requestPendente;
    private MentoriaRequest requestAceita;

    @BeforeEach
    void setUp() {
        mentorado = new User("João", "joao@email.com", "senha123");
        mentorado.setId(1L);
        mentorado.setRole(Role.USER);

        mentor = new User("Maria", "maria@email.com", "senha456");
        mentor.setId(2L);
        mentor.setRole(Role.MENTOR);

        dto = new MentoriaRequestDTO();
        dto.setMentorId(2L);
        dto.setMessage("Quero ser mentorado por você.");

        requestPendente = new MentoriaRequest();
        requestPendente.setId(10L);
        requestPendente.setMentorado(mentorado);
        requestPendente.setMentor(mentor);
        requestPendente.setMessage("Quero mentoria");
        requestPendente.setStatus(MentoriaStatus.PENDING);
        requestPendente.setCreatedAt(LocalDateTime.now());
        requestPendente.setUpdatedAt(LocalDateTime.now());

        requestAceita = new MentoriaRequest();
        requestAceita.setId(20L);
        requestAceita.setMentorado(mentorado);
        requestAceita.setMentor(mentor);
        requestAceita.setMessage("Outra solicitação");
        requestAceita.setStatus(MentoriaStatus.ACCEPTED);
        requestAceita.setCreatedAt(LocalDateTime.now());
        requestAceita.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void deveCriarSolicitacaoComSucesso() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(requestRepository.existsByMentoradoAndMentorAndStatus(any(), any(), any()))
                .thenReturn(false);

        // Garantindo que o save retorne o objeto passado no argumento com um ID
        when(requestRepository.save(any(MentoriaRequest.class))).thenAnswer(invocation -> {
            MentoriaRequest request = invocation.getArgument(0);
            request.setId(10L);
            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());
            return request;
        });

        MentoriaRequest result = requestService.createRequest(mentorado, dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        verify(requestRepository).save(any(MentoriaRequest.class));
    }

    @Test
    void deveLancarExcecaoQuandoMentorNaoEncontrado() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        dto.setMentorId(99L);

        assertThrows(EntityNotFoundException.class, () -> requestService.createRequest(mentorado, dto));

        verify(userRepository, times(1)).findById(99L);
        verify(requestRepository, never()).existsByMentoradoAndMentorAndStatus(any(), any(), any());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoAutoMentoria() {
        dto.setMentorId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentorado));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestService.createRequest(mentorado, dto));
        assertEquals("Você não pode solicitar mentoria para si mesmo", exception.getMessage());

        verify(userRepository, times(1)).findById(1L);
        verify(requestRepository, never()).existsByMentoradoAndMentorAndStatus(any(), any(), any());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoForMentor() {
        User usuarioComum = new User("Pedro", "pedro@email.com", "senha");
        usuarioComum.setId(3L);
        usuarioComum.setRole(Role.USER);

        dto.setMentorId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(usuarioComum));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestService.createRequest(mentorado, dto));
        assertEquals("O usuário selecionado não é um mentor", exception.getMessage());

        verify(userRepository, times(1)).findById(3L);
        verify(requestRepository, never()).existsByMentoradoAndMentorAndStatus(any(), any(), any());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSolicitacaoPendenteJaExiste() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(requestRepository.existsByMentoradoAndMentorAndStatus(mentorado, mentor, MentoriaStatus.PENDING))
                .thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestService.createRequest(mentorado, dto));
        assertEquals("Já existe uma solicitação pendente para este mentor", exception.getMessage());

        verify(userRepository, times(1)).findById(2L);
        verify(requestRepository, times(1)).existsByMentoradoAndMentorAndStatus(mentorado, mentor,
                MentoriaStatus.PENDING);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveListarPendentes() {
        when(requestRepository.findByMentorAndStatusOrderByCreatedAtDesc(mentor, MentoriaStatus.PENDING))
                .thenReturn(List.of(requestPendente));

        List<MentoriaRequest> resultado = requestService.listarPendentes(mentor);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(requestPendente.getId(), resultado.get(0).getId());

        verify(requestRepository, times(1)).findByMentorAndStatusOrderByCreatedAtDesc(mentor, MentoriaStatus.PENDING);
    }

    @Test
    void deveRecusarSolicitacaoComJustificativa() {
        MentoriaRequestUpdateDTO dto = new MentoriaRequestUpdateDTO();
        dto.setStatus(MentoriaStatus.REJECTED);
        dto.setJustificativa("Mentor sem disponibilidade");

        when(requestRepository.findById(10L)).thenReturn(Optional.of(requestPendente));
        when(requestRepository.save(any(MentoriaRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        MentoriaRequest resultado = requestService.atualizarStatus(10L, mentor, dto);

        assertNotNull(resultado);
        assertEquals(MentoriaStatus.REJECTED, resultado.getStatus());
        assertEquals("Mentor sem disponibilidade", resultado.getJustificativaRecusa());

        verify(requestRepository, times(1)).findById(10L);
        verify(requestRepository, times(1)).save(requestPendente);
    }

    @Test
    void deveRecusarSolicitacaoSemJustificativa() {
        MentoriaRequestUpdateDTO dto = new MentoriaRequestUpdateDTO();
        dto.setStatus(MentoriaStatus.REJECTED);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(requestPendente));
        when(requestRepository.save(any(MentoriaRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        MentoriaRequest resultado = requestService.atualizarStatus(10L, mentor, dto);

        assertNotNull(resultado);
        assertEquals(MentoriaStatus.REJECTED, resultado.getStatus());
        assertNull(resultado.getJustificativaRecusa());

        verify(requestRepository, times(1)).findById(10L);
        verify(requestRepository, times(1)).save(requestPendente);
    }

    @Test
    void deveLancarExcecaoQuandoMentorNaoTemPermissao() {
        User outroMentor = new User("Outro", "outro@email.com", "senha");
        outroMentor.setId(99L);
        outroMentor.setRole(Role.MENTOR);

        MentoriaRequestUpdateDTO dto = new MentoriaRequestUpdateDTO();
        dto.setStatus(MentoriaStatus.ACCEPTED);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(requestPendente));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestService.atualizarStatus(10L, outroMentor, dto));
        assertEquals("Você não tem permissão para alterar esta solicitação", exception.getMessage());

        verify(requestRepository, times(1)).findById(10L);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSolicitacaoJaProcessada() {
        MentoriaRequestUpdateDTO dto = new MentoriaRequestUpdateDTO();
        dto.setStatus(MentoriaStatus.ACCEPTED);

        when(requestRepository.findById(20L)).thenReturn(Optional.of(requestAceita));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestService.atualizarStatus(20L, mentor, dto));
        assertEquals("Esta solicitação já foi processada", exception.getMessage());

        verify(requestRepository, times(1)).findById(20L);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSolicitacaoNaoEncontrada() {
        MentoriaRequestUpdateDTO dto = new MentoriaRequestUpdateDTO();
        dto.setStatus(MentoriaStatus.ACCEPTED);

        when(requestRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> requestService.atualizarStatus(999L, mentor, dto));

        verify(requestRepository, times(1)).findById(999L);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void deveAceitarSolicitacaoComSucesso() {
        MentoriaRequestUpdateDTO updateDto = new MentoriaRequestUpdateDTO();
        updateDto.setStatus(MentoriaStatus.ACCEPTED);

        when(requestRepository.findById(10L)).thenReturn(Optional.of(requestPendente));
        when(requestRepository.save(any(MentoriaRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        MentoriaRequest resultado = requestService.atualizarStatus(10L, mentor, updateDto);

        assertNotNull(resultado);
        assertEquals(MentoriaStatus.ACCEPTED, resultado.getStatus());
    }
}
