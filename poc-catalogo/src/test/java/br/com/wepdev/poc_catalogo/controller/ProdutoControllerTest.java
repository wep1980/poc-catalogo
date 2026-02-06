package br.com.wepdev.poc_catalogo.controller;

import br.com.wepdev.poc_catalogo.dto.ProdutoDTO;
import br.com.wepdev.poc_catalogo.exception.DatabaseException;
import br.com.wepdev.poc_catalogo.exception.ProdutoNaoEncontradoException;
import br.com.wepdev.poc_catalogo.exception.RestExceptionHandler;
import br.com.wepdev.poc_catalogo.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProdutoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProdutoService produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(produtoController)
                .setControllerAdvice(new RestExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    // ======================
    // CAMINHO FELIZ
    // ======================

    @Test
    void deveBuscarProdutosPaginados() throws Exception {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(1L);
        dto.setNome("Notebook");

        Page<ProdutoDTO> page = new PageImpl<>(
                List.of(dto),
                PageRequest.of(0, 10),
                1
        );

        when(produtoService.findAllPaged(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/produtos")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Notebook"));
    }

    @Test
    void deveBuscarProdutoPorId() throws Exception {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(1L);
        dto.setNome("Mouse");

        when(produtoService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/produtos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Mouse"));
    }

    @Test
    void deveCriarProduto() throws Exception {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(1L);
        dto.setNome("Teclado");

        when(produtoService.adicionar(any())).thenReturn(dto);

        String json = """
            { "nome": "Teclado" }
            """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Teclado"));
    }

    @Test
    void deveAtualizarProduto() throws Exception {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(1L);
        dto.setNome("Monitor");

        when(produtoService.atualizar(eq(1L), any())).thenReturn(dto);

        String json = """
            { "nome": "Monitor" }
            """;

        mockMvc.perform(put("/produtos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Monitor"));
    }

    @Test
    void deveRemoverProduto() throws Exception {
        doNothing().when(produtoService).remover(1L);

        mockMvc.perform(delete("/produtos/1"))
                .andExpect(status().isNoContent());
    }

    // ======================
    // CAMINHO NÃO FELIZ
    // ======================

    @Test
    void deveRetornar404QuandoProdutoNaoExiste() throws Exception {
        when(produtoService.findById(99L))
                .thenThrow(new ProdutoNaoEncontradoException("Produto não encontrado"));

        mockMvc.perform(get("/produtos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Produto não encontrado"))
                .andExpect(jsonPath("$.path").value("/produtos/99"));
    }

    @Test
    void deveRetornar400QuandoErroDeBancoAoCriar() throws Exception {
        when(produtoService.adicionar(any()))
                .thenThrow(new DatabaseException("Violação de integridade"));

        String json = """
            { "nome": "Produto Teste" }
            """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Database exception"))
                .andExpect(jsonPath("$.path").value("/produtos"));
    }

    @Test
    void deveRetornar404QuandoAtualizarInexistente() throws Exception {
        when(produtoService.atualizar(eq(99L), any()))
                .thenThrow(new ProdutoNaoEncontradoException("Produto não encontrado"));

        String json = """
            { "nome": "Novo Produto" }
            """;

        mockMvc.perform(put("/produtos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Produto não encontrado"));
    }

    @Test
    void deveRetornar404QuandoRemoverInexistente() throws Exception {
        doThrow(new ProdutoNaoEncontradoException("Produto não encontrado"))
                .when(produtoService)
                .remover(99L);

        mockMvc.perform(delete("/produtos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/produtos/99"));
    }
}
