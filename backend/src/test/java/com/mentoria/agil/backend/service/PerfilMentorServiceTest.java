package com.mentoria.agil.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mentoria.agil.backend.dto.PerfilMentorRequestDTO;
import com.mentoria.agil.backend.model.PerfilMentor;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.PerfilMentorRepository;
import com.mentoria.agil.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class PerfilMentorServiceTest {

    @Mock
    private PerfilMentorRepository perfilMentorRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PerfilMentorService perfilMentorService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private Validator validator;
    private User usuarioVisitante;
    private User usuarioMentor;
    private PerfilMentor perfilMentor;
    private PerfilMentorRequestDTO perfilMentorRequestDTO;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        perfilMentorRequestDTO = new PerfilMentorRequestDTO(
                "Desenvolvimento Backend", "5 anos no mercado", "Ciência da Computação");

        usuarioVisitante = new User();
        usuarioVisitante.setId(1L);
        usuarioVisitante.setRole(Role.VISITANTE);

        usuarioMentor = new User();
        usuarioMentor.setId(2L);
        usuarioMentor.setRole(Role.MENTOR);

        perfilMentor = new PerfilMentor("Java", "5 anos no mercado", usuarioMentor);
        perfilMentor.setId(1L);
        perfilMentor.setFormacao("Engenharia de Software");

        usuarioMentor.setPerfilMentor(perfilMentor);
    }

    @Test
    @DisplayName("Deve salvar perfil de mentor com sucesso e alterar a role do usuário")
    void criarPerfilMentorSucesso() {
        when(perfilMentorRepository.save(any(PerfilMentor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PerfilMentor resultado = perfilMentorService.criarPerfilMentor(
                usuarioVisitante, "Spring Boot", "Pleno", "Engenharia de Software");

        verify(userRepository, times(1)).save(userCaptor.capture());
        User usuarioSalvo = userCaptor.getValue();

        assertNotNull(resultado);
        assertEquals("Spring Boot", resultado.getEspecializacao());
        assertEquals(Role.MENTOR, usuarioSalvo.getRole());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException quando usuário já possuir perfil de mentor")
    void criarPerfilMentorJaExiste() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            perfilMentorService.criarPerfilMentor(usuarioMentor, "Python", "Junior", "Física");
        });

        assertEquals("Usuário já possui perfil de mentor", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve retornar o perfil do mentor quando buscar por um ID existente")
    void buscarPorIdSucesso() {
        when(perfilMentorRepository.findById(1L)).thenReturn(Optional.of(perfilMentor));

        PerfilMentor resultado = perfilMentorService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(perfilMentorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando buscar por um ID inexistente")
    void buscarPorIdInexistente() {
        when(perfilMentorRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            perfilMentorService.buscarPorId(99L);
        });

        assertEquals("Perfil de mentor não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista com todos os perfis de mentor")
    void listarMentores() {
        when(perfilMentorRepository.findAll()).thenReturn(Arrays.asList(perfilMentor));

        List<PerfilMentor> resultados = perfilMentorService.listarTodos();

        assertFalse(resultados.isEmpty());
        assertEquals(1, resultados.size());
        verify(perfilMentorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve deletar o perfil do mentor e restaurar a role do usuário para VISITANTE")
    void deletarPerfilEAtualizarRole() {
        when(perfilMentorRepository.findById(1L)).thenReturn(Optional.of(perfilMentor));

        perfilMentorService.deletar(1L);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User usuarioAtualizado = userCaptor.getValue();

        assertNull(usuarioAtualizado.getPerfilMentor(), "O perfil do usuário deve ser nulo após ser apagado");
        assertEquals(Role.VISITANTE, usuarioAtualizado.getRole(), "A role deve voltar para VISITANTE");
        verify(perfilMentorRepository, times(1)).delete(perfilMentor);
    }

    @Test
    @DisplayName("Deve atualizar o perfil do mentor com sucesso")
    void atualizarMentorSucesso() {
        when(perfilMentorRepository.save(any(PerfilMentor.class))).thenReturn(perfilMentor);

        perfilMentor.setEspecializacao("Java Avançado");
        PerfilMentor resultado = perfilMentorService.atualizar(usuarioMentor, perfilMentor);

        assertNotNull(resultado);
        assertEquals("Java Avançado", resultado.getEspecializacao());
        verify(userRepository, times(1)).save(usuarioMentor);
        verify(perfilMentorRepository, times(1)).save(perfilMentor);
    }

    @Test
    @DisplayName("Deve lançar IllegalStateException ao tentar atualizar um perfil que pertence a outro usuário")
    void atualizarPerfilDeOutroUsuarioSemPermissao() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            perfilMentorService.atualizar(usuarioVisitante, perfilMentor);
        });

        assertEquals("Perfil não pertence ao usuário", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(perfilMentorRepository, never()).save(any(PerfilMentor.class));
    }

    @Test
    @DisplayName("Não deve gerar violações para um DTO válido")
    void dtoValido() {
        Set<ConstraintViolation<PerfilMentorRequestDTO>> violations = validator.validate(perfilMentorRequestDTO);
        assertTrue(violations.isEmpty(), "Não deveria haver erros de validação em um DTO válido");
    }

    @Test
    @DisplayName("Deve falhar na validação se a especialização estiver em branco")
    void validacaoEspecializacaoEmBranco() {
        perfilMentorRequestDTO.setEspecializacao("");
        Set<ConstraintViolation<PerfilMentorRequestDTO>> violations = validator.validate(perfilMentorRequestDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Especialização é obrigatória")));
    }

    @Test
    @DisplayName("Deve falhar na validação se a experiência estiver em branco")
    void validacaoExperienciaEmBranco() {
        perfilMentorRequestDTO.setExperiencias("");
        Set<ConstraintViolation<PerfilMentorRequestDTO>> violations = validator.validate(perfilMentorRequestDTO);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Experiência é obrigatória")));
    }
}