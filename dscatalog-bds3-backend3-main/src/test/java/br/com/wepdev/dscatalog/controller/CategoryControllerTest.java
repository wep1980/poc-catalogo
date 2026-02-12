package br.com.wepdev.dscatalog.controller;

import br.com.wepdev.dscatalog.dto.CategoryDTO;
import br.com.wepdev.dscatalog.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CategoryService service;

    @InjectMocks
    private CategoryController controller;


    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .build();
    }


    @Test
    void deveBuscarCategoriasPaginadas() throws Exception {
        Page<CategoryDTO> page =
                new PageImpl<>(List.of(new CategoryDTO(1L, "Livros")));

        when(service.findAllPaged(any())).thenReturn(page);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Livros"));
    }

    @Test
    void deveBuscarCategoriaPorId() throws Exception {
        when(service.findById(1L))
                .thenReturn(new CategoryDTO(1L, "Livros"));

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Livros"));
    }

    @Test
    void deveInserirCategoria() throws Exception {
        CategoryDTO entrada = new CategoryDTO(null, "Nova Categoria");
        CategoryDTO retorno = new CategoryDTO(1L, "Nova Categoria");

        when(service.insert(any())).thenReturn(retorno);

        mockMvc.perform(post("/categories")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Nova Categoria"));
    }

    @Test
    void deveAtualizarCategoria() throws Exception {
        CategoryDTO entrada = new CategoryDTO(null, "Atualizada");
        CategoryDTO retorno = new CategoryDTO(1L, "Atualizada");

        when(service.update(eq(1L), any())).thenReturn(retorno);

        mockMvc.perform(put("/categories/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Atualizada"));
    }

    @Test
    void deveExcluirCategoria() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());
    }
}
