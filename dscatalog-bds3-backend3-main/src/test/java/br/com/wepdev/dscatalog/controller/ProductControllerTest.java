package br.com.wepdev.dscatalog.controller;

import br.com.wepdev.dscatalog.controller.exceptions.ResourceExceptionHandler;
import br.com.wepdev.dscatalog.dto.CategoryDTO;
import br.com.wepdev.dscatalog.dto.ProductDTO;
import br.com.wepdev.dscatalog.services.ProductService;
import br.com.wepdev.dscatalog.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProductService service;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ProductController controller = new ProductController(service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new ResourceExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()) // Pageable funciona aqui
                .setValidator(validator) // @Valid
                .build();
    }

    private ProductDTO criarDtoValido() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Produto Teste");
        dto.setDescription("Descricao do produto");
        dto.setPrice(100.0);
        dto.setImgUrl("img.png");
        dto.setDate(Instant.now());

        dto.setCategories(List.of(new CategoryDTO(1L, "Categoria 1")));
        return dto;
    }

    private ProductDTO criarDtoInvalidoSemCategoria() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Prod");
        dto.setDescription("");
        dto.setPrice(-10.0);
        dto.setDate(Instant.now());
        dto.setCategories(List.of());
        return dto;
    }

    @Test
    void deveBuscarProdutosPaginados_semFiltros() throws Exception {
        Page<ProductDTO> page = new PageImpl<>(
                List.of(criarDtoValido()),
                PageRequest.of(0, 20),
                1
        );

        when(service.findAllPaged(eq(0L), eq(""), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Produto Teste"));
    }

    @Test
    void deveBuscarProdutosPaginados_comFiltros() throws Exception {
        Page<ProductDTO> page = new PageImpl<>(
                List.of(criarDtoValido()),
                PageRequest.of(0, 12),
                1
        );

        when(service.findAllPaged(eq(1L), eq("tv"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/products")
                        .param("categoryId", "1")
                        .param("name", "tv")
                        .param("page", "0")
                        .param("size", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void deveBuscarProdutoPorId_quandoExistir() throws Exception {
        ProductDTO dto = criarDtoValido();
        dto.setId(1L);

        when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Produto Teste"));
    }

    @Test
    void deveRetornar404_quandoBuscarProdutoInexistente() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Id não encontrado"));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }


    @Test
    void deveInserirProduto_quandoDtoForValido() throws Exception {
        ProductDTO retorno = criarDtoValido();
        retorno.setId(1L);

        when(service.insert(any(ProductDTO.class))).thenReturn(retorno);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDtoValido())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Produto Teste"));
    }

    @Test
    void deveRetornar422_quandoInserirProdutoInvalido() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDtoInvalidoSemCategoria())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Erro de validação"));

        verifyNoInteractions(service);
    }

    @Test
    void deveAtualizarProduto_quandoExistirEDtoForValido() throws Exception {
        ProductDTO retorno = criarDtoValido();
        retorno.setId(1L);

        when(service.update(eq(1L), any(ProductDTO.class))).thenReturn(retorno);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDtoValido())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Produto Teste"));
    }

    @Test
    void deveRetornar404_quandoAtualizarProdutoInexistente_comDtoValido() throws Exception {
        when(service.update(eq(99L), any(ProductDTO.class)))
                .thenThrow(new ResourceNotFoundException("Id não encontrado"));

        mockMvc.perform(put("/products/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDtoValido())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }

    @Test
    void deveRetornar422_quandoAtualizarComDtoInvalido() throws Exception {
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarDtoInvalidoSemCategoria())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Erro de validação"));

        verify(service, never()).update(anyLong(), any());
    }

    @Test
    void deveExcluirProduto_quandoExistir() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void deveRetornar404_quandoExcluirProdutoInexistente() throws Exception {
        doThrow(new ResourceNotFoundException("Id não encontrado"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso não encontrado"));
    }
}
