package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.MentoriaRequestDTO;
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

    @BeforeEach
    void setUp() {
        mentorado = new User("João", "joao@email.com", "senha123");
        mentorado.setId(1L);
        mentorado.setRole(Role.USER); // mentorado comum

        mentor = new User("Maria", "maria@email.com", "senha456");
        mentor.setId(2L);
        mentor.setRole(Role.MENTOR); // mentor

        dto = new MentoriaRequestDTO();
        dto.setMentorId(2L);
        dto.setMessage("Quero ser mentorado por você.");
    }

        @Test
    void deveCriarSolicitacaoComSucesso() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentor));
        when(requestRepository.existsByMentoradoAndMentorAndStatus(mentorado, mentor, MentoriaStatus.PENDING))
                .thenReturn(false);
        when(requestRepository.save(any(MentoriaRequest.class))).thenAnswer(invocation -> {
            MentoriaRequest request = invocation.getArgument(0);
            request.setId(10L); // simula ID gerado
            return request;
        });

        MentoriaRequest result = requestService.createRequest(mentorado, dto);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(mentorado, result.getMentorado());
        assertEquals(mentor, result.getMentor());
        assertEquals(dto.getMessage(), result.getMessage());
        assertEquals(MentoriaStatus.PENDING, result.getStatus());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(userRepository, times(1)).findById(2L);
        verify(requestRepository, times(1)).existsByMentoradoAndMentorAndStatus(mentorado, mentor, MentoriaStatus.PENDING);
        verify(requestRepository, times(1)).save(any(MentoriaRequest.class));
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
        dto.setMentorId(1L); // mesmo ID do mentorado
        when(userRepository.findById(1L)).thenReturn(Optional.of(mentorado)); // mentorado encontrado, mas é o mesmo

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
        usuarioComum.setRole(Role.USER); // não é mentor

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
                .thenReturn(true); // já existe pendente

        BusinessException exception = assertThrows(BusinessException.class,
                () -> requestService.createRequest(mentorado, dto));
        assertEquals("Já existe uma solicitação pendente para este mentor", exception.getMessage());

        verify(userRepository, times(1)).findById(2L);
        verify(requestRepository, times(1)).existsByMentoradoAndMentorAndStatus(mentorado, mentor, MentoriaStatus.PENDING);
        verify(requestRepository, never()).save(any());
    }
}
