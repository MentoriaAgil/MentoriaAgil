package com.mentoria.agil.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mentoria.agil.backend.dto.UserRequestDTO;
import com.mentoria.agil.backend.exception.EmailJaCadastradoException;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.PerfilMentorRepository;
import com.mentoria.agil.backend.repository.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PerfilMentorRepository perfilMentorRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private Validator validator;
    private UserRequestDTO userRequestDTO;
    private User userMock;

    @BeforeEach
    void setUp() {
        // para testar as anotaçoes do dto
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        userRequestDTO = new UserRequestDTO("João Silva", "joao@email.com", "senha123", Role.ADMIN);

        userMock = new User();
        userMock.setName("João Silva");
        userMock.setEmail("joao@email.com");
        userMock.setPassword("senhaCriptografada");
        userMock.setAtivo(true);
        userMock.setAreaInteresse("TI");
        userMock.setTipoMentoria("Individual");
    }

    @Test
    @DisplayName("Deve salvar usuário com sucesso quando o e-mail não existir")
    void salvarUsuarioSucesso() {
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDTO.password())).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(userMock);

        userService.salvarUsuario(userRequestDTO);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User usuarioMapeado = userCaptor.getValue();

        assertNotNull(usuarioMapeado, "O usuário mapeado não deve ser nulo");
        assertEquals("João Silva", usuarioMapeado.getName());
        assertEquals("joao@email.com", usuarioMapeado.getEmail());
        assertEquals("senhaCriptografada", usuarioMapeado.getPassword(), "A senha deve estar criptografada");
        assertEquals(Role.ADMIN, usuarioMapeado.getRole());

        verify(userRepository, times(1)).existsByEmail(userRequestDTO.email());
        verify(passwordEncoder, times(1)).encode(userRequestDTO.password());
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando o e-mail já estiver cadastrado")
    void salvarUsuarioEmailExistente() {
        when(userRepository.existsByEmail(userRequestDTO.email())).thenReturn(true);

        EmailJaCadastradoException exception = assertThrows(EmailJaCadastradoException.class, () -> {
            userService.salvarUsuario(userRequestDTO);
        });

        assertEquals("Este e-mail já está cadastrado.", exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atribuir Role.VISITANTE por padrão se o role no DTO for nulo")
    void salvarUsuarioRoleNulo() {
        UserRequestDTO dtoSemRole = new UserRequestDTO("Maria", "maria@email.com", "senha123", null);
        User userVisitante = new User();
        userVisitante.setRole(Role.VISITANTE);

        when(userRepository.existsByEmail(dtoSemRole.email())).thenReturn(false);
        when(passwordEncoder.encode(dtoSemRole.password())).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(userVisitante);

        User resultado = userService.salvarUsuario(dtoSemRole);

        assertEquals(Role.VISITANTE, resultado.getRole(), "A role padrão deve ser VISITANTE");
    }

    @Test
    @DisplayName("Deve retornar o usuário quando buscar por um e-mail existente")
    void buscarPorEmailSucesso() {
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(userMock));

        User resultado = userService.buscarPorEmail("joao@email.com");

        assertNotNull(resultado);
        assertEquals("joao@email.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve retornar null quando buscar por um e-mail inexistente")
    void buscarPorEmailInexistente() {
        when(userRepository.findByEmail("naoexisto@email.com")).thenReturn(Optional.empty());

        User resultado = userService.buscarPorEmail("naoexisto@email.com");

        assertNull(resultado);
    }

    @Test
    @DisplayName("Não deve gerar violações para um DTO válido")
    void dtoValido() {
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(userRequestDTO);
        assertTrue(violations.isEmpty(), "Não deveria haver erros de validação em um DTO válido");
    }

    @Test
    @DisplayName("Deve falhar na validação se o nome estiver em branco")
    void validacaoNomeEmBranco() {
        UserRequestDTO dtoInvalido = new UserRequestDTO("", "joao@email.com", "senha123", Role.VISITANTE);
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dtoInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O nome é obrigatório")));
    }

    @Test
    @DisplayName("Deve falhar na validação se o e-mail for inválido")
    void validacaoEmailInvalido() {
        UserRequestDTO dtoInvalido = new UserRequestDTO("João", "email-invalido", "senha123", Role.VISITANTE);
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dtoInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("E-mail inválido")));
    }

    @Test
    @DisplayName("Deve falhar na validação se o e-mail estiver em branco")
    void validacaoEmailEmBranco() {
        UserRequestDTO dtoInvalido = new UserRequestDTO("João", "", "senha123", Role.VISITANTE);
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dtoInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("O e-mail é obrigatório")));
    }

    @Test
    @DisplayName("Deve falhar na validação se a senha tiver menos de 8 caracteres")
    void validacaoSenhaCurta() {
        UserRequestDTO dtoInvalido = new UserRequestDTO("João", "joao@email.com", "1234567", Role.VISITANTE);
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dtoInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A senha deve ter no mínimo 8 caracteres")));
    }

    @Test
    @DisplayName("Deve falhar na validação se a senha estiver em branco")
    void validacaoSenhaEmBranco() {
        UserRequestDTO dtoInvalido = new UserRequestDTO("João", "joao@email.com", "", Role.VISITANTE);
        Set<ConstraintViolation<UserRequestDTO>> violations = validator.validate(dtoInvalido);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("A senha é obrigatória")));
    }

    @Test
    @DisplayName("Deve listar mentores aplicando todos os filtros de busca")
    void listarMentoresComSucesso() {
        User user = new User();
        user.setName("Mentor ABC");
        user.setAtivo(true);
        user.setAreaInteresse("Tecnologia");
        user.setTipoMentoria("Individual");

        com.mentoria.agil.backend.model.PerfilMentor perfil = new com.mentoria.agil.backend.model.PerfilMentor();
        perfil.setUser(user);
        perfil.setEspecializacao("Java");

        when(perfilMentorRepository.findAll()).thenReturn(List.of(perfil));

        var resultado = userService.listarMentores("Java", "Tecnologia", "Individual");

        assertFalse(resultado.isEmpty());
        assertEquals("Mentor ABC", resultado.get(0).nome());
    }

    @Test
    @DisplayName("Deve testar todas as combinações de filtros nulos na listagem")
    void listarMentoresFiltrosVariados() {
        User user = new User();
        user.setAtivo(true);
        user.setName("Mentor Teste");
        user.setAreaInteresse("TI");
        user.setTipoMentoria("Carreira");

        com.mentoria.agil.backend.model.PerfilMentor perfil = new com.mentoria.agil.backend.model.PerfilMentor();
        perfil.setUser(user);
        perfil.setEspecializacao("Java");

        when(perfilMentorRepository.findAll()).thenReturn(List.of(perfil));

        assertFalse(userService.listarMentores(null, "TI", "Carreira").isEmpty());
        assertFalse(userService.listarMentores("Java", null, "Carreira").isEmpty());
        assertFalse(userService.listarMentores("Java", "TI", null).isEmpty());

        user.setAtivo(false);
        assertTrue(userService.listarMentores(null, null, null).isEmpty());
    }
}