package com.mentoria.agil.backend.service;

import com.mentoria.agil.backend.dto.MaterialRequestDTO;
import com.mentoria.agil.backend.exception.BusinessException;
import com.mentoria.agil.backend.model.Material;
import com.mentoria.agil.backend.model.MaterialMentorado;
import com.mentoria.agil.backend.model.Role;
import com.mentoria.agil.backend.model.User;
import com.mentoria.agil.backend.repository.MaterialMentoradoRepository;
import com.mentoria.agil.backend.repository.MaterialRepository;
import com.mentoria.agil.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MaterialMentoradoRepository materialMentoradoRepository;

    @InjectMocks
    private MaterialService materialService;

    private User mentor;
    private User mentorado1;
    private User mentorado2;
    private MaterialRequestDTO dtoComMentorados;
    private MaterialRequestDTO dtoSemMentorados;

    @BeforeEach
    void setUp() {
        mentor = new User("Mentor", "mentor@email.com", "senha");
        mentor.setId(1L);
        mentor.setRole(Role.MENTOR);

        mentorado1 = new User("Mentorado1", "mentorado1@email.com", "senha");
        mentorado1.setId(2L);
        mentorado1.setRole(Role.ESTUDANTE);

        mentorado2 = new User("Mentorado2", "mentorado2@email.com", "senha");
        mentorado2.setId(3L);
        mentorado2.setRole(Role.ESTUDANTE);

        dtoComMentorados = new MaterialRequestDTO();
        dtoComMentorados.setTitulo("Título Teste");
        dtoComMentorados.setDescricao("Descrição Teste");
        dtoComMentorados.setConteudo("http://teste.com");
        dtoComMentorados.setTipo(com.mentoria.agil.backend.model.TipoMaterial.LINK);
        dtoComMentorados.setMentoradosIds(Arrays.asList(2L, 3L));

        dtoSemMentorados = new MaterialRequestDTO();
        dtoSemMentorados.setTitulo("Título Sem Mentorados");
        dtoSemMentorados.setDescricao("Descrição");
        dtoSemMentorados.setConteudo("http://exemplo.com");
        dtoSemMentorados.setTipo(com.mentoria.agil.backend.model.TipoMaterial.DOCUMENTO);
        dtoSemMentorados.setMentoradosIds(null);
    }

    @Test
    void deveCriarMaterialComSucessoComMentorados() {

        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material material = invocation.getArgument(0);
            material.setId(10L);
            return material;
        });

        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado1));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mentorado2));

        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(Material.class), eq(mentorado1)))
                .thenReturn(false);
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(Material.class), eq(mentorado2)))
                .thenReturn(false);

        Material resultado = materialService.criarMaterial(mentor, dtoComMentorados);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals(dtoComMentorados.getTitulo(), resultado.getTitulo());
        assertEquals(dtoComMentorados.getDescricao(), resultado.getDescricao());
        assertEquals(dtoComMentorados.getConteudo(), resultado.getConteudo());
        assertEquals(dtoComMentorados.getTipo(), resultado.getTipo());
        assertEquals(mentor, resultado.getMentor());

        verify(materialRepository, times(1)).save(any(Material.class));
        verify(userRepository, times(2)).findById(anyLong());
        verify(materialMentoradoRepository, times(2)).existsByMaterialAndMentorado(any(Material.class),
                any(User.class));
        verify(materialMentoradoRepository, times(2)).save(any(MaterialMentorado.class));
    }

    @Test
    void deveCriarMaterialComSucessoSemMentorados() {

        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material material = invocation.getArgument(0);
            material.setId(11L);
            return material;
        });

        Material resultado = materialService.criarMaterial(mentor, dtoSemMentorados);

        assertNotNull(resultado);
        assertEquals(11L, resultado.getId());
        assertEquals(dtoSemMentorados.getTitulo(), resultado.getTitulo());

        verify(materialRepository, times(1)).save(any(Material.class));
        verify(userRepository, never()).findById(anyLong());
        verify(materialMentoradoRepository, never()).existsByMaterialAndMentorado(any(), any());
        verify(materialMentoradoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoMentoradoNaoEncontrado() {
        dtoComMentorados.setMentoradosIds(java.util.List.of(3L));

        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material material = invocation.getArgument(0);
            material.setId(12L);
            return material;
        });

        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> materialService.criarMaterial(mentor, dtoComMentorados));

        assertTrue(exception.getMessage().contains("Mentorado não encontrado com ID: 3"));

        verify(materialRepository, times(1)).save(any(Material.class));
        verify(userRepository, times(1)).findById(3L);
        verify(materialMentoradoRepository, never()).existsByMaterialAndMentorado(any(), any());
        verify(materialMentoradoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoMentoradoNaoTemRoleEstudante() {
        User usuarioInvalido = new User("Invalido", "invalido@email.com", "senha");
        usuarioInvalido.setId(4L);
        usuarioInvalido.setRole(Role.USER);

        dtoComMentorados.setMentoradosIds(List.of(4L));

        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material material = invocation.getArgument(0);
            material.setId(13L);
            return material;
        });

        when(userRepository.findById(4L)).thenReturn(Optional.of(usuarioInvalido));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> materialService.criarMaterial(mentor, dtoComMentorados));

        assertTrue(exception.getMessage().contains("não é um estudante"));
        assertTrue(exception.getMessage().contains("ID 4"));

        verify(materialRepository, times(1)).save(any(Material.class));
        verify(userRepository, times(1)).findById(4L);
        // Agora o never() passará pois o loop interrompe na primeira falha
        verify(materialMentoradoRepository, never()).existsByMaterialAndMentorado(any(), any());
        verify(materialMentoradoRepository, never()).save(any());
    }

    @Test
    void naoDeveDuplicarAssociacaoQuandoJaExiste() {

        when(materialRepository.save(any(Material.class))).thenAnswer(invocation -> {
            Material material = invocation.getArgument(0);
            material.setId(14L);
            return material;
        });

        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado1));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mentorado2));

        // Simula que já existe associação para mentorado1
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(Material.class), eq(mentorado1)))
                .thenReturn(true);
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(Material.class), eq(mentorado2)))
                .thenReturn(false);

        Material resultado = materialService.criarMaterial(mentor, dtoComMentorados);

        assertNotNull(resultado);
        verify(materialMentoradoRepository, times(1)).save(any(MaterialMentorado.class)); // apenas para mentorado2
        verify(materialMentoradoRepository, never()).save(argThat(assoc -> assoc.getMentorado().equals(mentorado1)));
    }

    @Test
    void deveListarMateriaisPorMentorado() {

        Material material1 = new Material("Material1", "Desc1", com.mentoria.agil.backend.model.TipoMaterial.LINK,
                "url1", mentor);
        material1.setId(101L);
        Material material2 = new Material("Material2", "Desc2", com.mentoria.agil.backend.model.TipoMaterial.DOCUMENTO,
                "url2", mentor);
        material2.setId(102L);

        MaterialMentorado assoc1 = new MaterialMentorado(material1, mentorado1);
        MaterialMentorado assoc2 = new MaterialMentorado(material2, mentorado1);

        when(materialMentoradoRepository.findByMentorado(mentorado1)).thenReturn(Arrays.asList(assoc1, assoc2));

        List<Material> materiais = materialService.listarMateriaisPorMentorado(mentorado1);

        assertNotNull(materiais);
        assertEquals(2, materiais.size());
        assertTrue(materiais.contains(material1));
        assertTrue(materiais.contains(material2));
        verify(materialMentoradoRepository, times(1)).findByMentorado(mentorado1);
    }

    @Test
    void deveRetornarListaVaziaQuandoMentoradoNaoTemMateriais() {

        when(materialMentoradoRepository.findByMentorado(mentorado1)).thenReturn(List.of());

        List<Material> materiais = materialService.listarMateriaisPorMentorado(mentorado1);

        assertNotNull(materiais);
        assertTrue(materiais.isEmpty());
        verify(materialMentoradoRepository, times(1)).findByMentorado(mentorado1);
    }

    @Test
    @DisplayName("Deve processar lista de mentorados misturando associações novas e existentes")
    void deveProcessarMentoradosMistos() {
        // forca o loop a rodar para dois mentorados, um que ja existe e outro nao
        dtoComMentorados.setMentoradosIds(Arrays.asList(2L, 3L));

        when(materialRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado1));
        when(userRepository.findById(3L)).thenReturn(Optional.of(mentorado2));

        // mentorado 1 ja tem vinculo, branch false, Mentorado 2 nao tem, branch true
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(), eq(mentorado1))).thenReturn(true);
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(), eq(mentorado2))).thenReturn(false);

        materialService.criarMaterial(mentor, dtoComMentorados);

        // verifica que o save foi chamado apenas para o mentorado que nao tinha vinculo
        verify(materialMentoradoRepository, times(1)).save(argThat(m -> m.getMentorado().equals(mentorado2)));
    }

    @Test
    @DisplayName("Deve criar material quando a lista de mentoradosIds é null")
    void criarMaterialListaNull() {
        // configura dto com lista null para testar o curto-circuito do if
        dtoComMentorados.setMentoradosIds(null);
        when(materialRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Material result = materialService.criarMaterial(mentor, dtoComMentorados);

        assertNotNull(result);
        // verifica que o cod nao tentou iterar sobre a lista null
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve ignorar associação quando o mentorado já possui o material")
    void deveIgnorarAssociacaoExistente() {
        dtoComMentorados.setMentoradosIds(List.of(2L));
        when(materialRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado1));

        // simula que o vinculo ja existe, branch !exists como false
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(), eq(mentorado1))).thenReturn(true);

        materialService.criarMaterial(mentor, dtoComMentorados);

        verify(materialMentoradoRepository, never()).save(any(MaterialMentorado.class));
    }

    @Test
    @DisplayName("Deve ignorar associação quando o mentorado já possui o material (Branch coverage)")
    void deveIgnorarAssociacaoQuandoJaExiste() {
        // Configura um DTO com um mentorado
        dtoComMentorados.setMentoradosIds(List.of(2L));

        when(materialRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(2L)).thenReturn(Optional.of(mentorado1));

        // Simula que a associação JÁ EXISTE (fazendo o !exists ser false)
        when(materialMentoradoRepository.existsByMaterialAndMentorado(any(), eq(mentorado1))).thenReturn(true);

        Material resultado = materialService.criarMaterial(mentor, dtoComMentorados);

        assertNotNull(resultado);
        // Verifica que o save da associação NUNCA foi chamado
        verify(materialMentoradoRepository, never()).save(any(MaterialMentorado.class));
    }

    @Test
    @DisplayName("Deve criar material quando a lista de IDs é nula (Curto-circuito do if)")
    void deveCriarMaterialComListaIdsNula() {
        // Testa especificamente a primeira parte da condição: dto.getMentoradosIds() !=
        // null como false
        dtoComMentorados.setMentoradosIds(null);
        when(materialRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Material result = materialService.criarMaterial(mentor, dtoComMentorados);

        assertNotNull(result);
        // Garante que o loop nem sequer começou
        verify(userRepository, never()).findById(anyLong());
    }

@Test
@DisplayName("Deve criar material quando lista de mentoradosIds está vazia")
void criarMaterialListaVazia() {
    dtoComMentorados.setMentoradosIds(java.util.Collections.emptyList()); // Garante que é uma lista vazia, não null
    when(materialRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Material result = materialService.criarMaterial(mentor, dtoComMentorados);
    assertNotNull(result);
    verify(userRepository, never()).findById(anyLong());
}
}
