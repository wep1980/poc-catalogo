package br.com.wepdev.poc_catalogo.controller;

import br.com.wepdev.poc_catalogo.dto.CategoriaDTO;
import br.com.wepdev.poc_catalogo.exception.CategoriaNaoEncontradaException;
import br.com.wepdev.poc_catalogo.exception.DatabaseException;
import br.com.wepdev.poc_catalogo.exception.RestExceptionHandler;
import br.com.wepdev.poc_catalogo.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;


class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoriaService categoriaService;

    @InjectMocks
    private CategoriaController categoriaController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoriaController)
                .setControllerAdvice(new RestExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // ======================
    // CAMINHO FELIZ
    // ======================

    @Test
    void deveBuscarCategorias() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(1L);
        dto.setNome("Livros");

        Page<CategoriaDTO> page = new PageImpl<>(
                List.of(dto),
                PageRequest.of(0, 10),
                1
        );

        when(categoriaService.findAllPaged(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Livros"));
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(1L);
        dto.setNome("Livros");

        when(categoriaService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Livros"));
    }

    @Test
    void deveCriarCategoria() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(1L);
        dto.setNome("Games");

        when(categoriaService.adicionar(any())).thenReturn(dto);

        String json = """
            { "nome": "Games" }
            """;

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Games"));
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(1L);
        dto.setNome("Casa");

        when(categoriaService.atualizar(eq(1L), any())).thenReturn(dto);

        String json = """
            { "nome": "Casa" }
            """;

        mockMvc.perform(put("/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Casa"));
    }

    @Test
    void deveRemoverCategoria() throws Exception {
        doNothing().when(categoriaService).remover(1L);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent());
    }

    // ======================
    // CAMINHO NÃO FELIZ
    // ======================

    @Test
    void deveRetornar404QuandoCategoriaNaoExiste() throws Exception {
        when(categoriaService.findById(99L))
                .thenThrow(new CategoriaNaoEncontradaException("Categoria não encontrada"));

        mockMvc.perform(get("/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Categoria não encontrada"))
                .andExpect(jsonPath("$.message").value("Categoria não encontrada"))
                .andExpect(jsonPath("$.path").value("/categorias/99"));
    }

    @Test
    void deveRetornar400QuandoErroDeBancoAoCriar() throws Exception {
        when(categoriaService.adicionar(any()))
                .thenThrow(new DatabaseException("Violação de integridade"));

        String json = """
            { "nome": "Teste" }
            """;

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Database exception"))
                .andExpect(jsonPath("$.message").value("Violação de integridade"))
                .andExpect(jsonPath("$.path").value("/categorias"));
    }

    @Test
    void deveRetornar404QuandoAtualizarInexistente() throws Exception {
        when(categoriaService.atualizar(eq(99L), any()))
                .thenThrow(new CategoriaNaoEncontradaException("Categoria não encontrada"));

        String json = """
            { "nome": "Nova" }
            """;

        mockMvc.perform(put("/categorias/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Categoria não encontrada"));
    }

    @Test
    void deveRetornar404QuandoRemoverInexistente() throws Exception {

        doThrow(new CategoriaNaoEncontradaException("Categoria com id 99 não encontrada"))
                .when(categoriaService)
                .remover(99L);

        mockMvc.perform(delete("/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Categoria não encontrada"))
                .andExpect(jsonPath("$.path").value("/categorias/99"));
    }

}
