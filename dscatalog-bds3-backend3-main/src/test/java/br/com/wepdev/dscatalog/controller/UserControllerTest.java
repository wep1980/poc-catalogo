package br.com.wepdev.dscatalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.wepdev.dscatalog.controller.exceptions.ResourceExceptionHandler;
import br.com.wepdev.dscatalog.dto.RoleDTO;
import br.com.wepdev.dscatalog.dto.UserDTO;
import br.com.wepdev.dscatalog.dto.UserInsertDTO;
import br.com.wepdev.dscatalog.dto.UserUpdateDTO;
import br.com.wepdev.dscatalog.repositories.UserRepository;
import br.com.wepdev.dscatalog.services.UserService;
import br.com.wepdev.dscatalog.services.exceptions.ResourceNotFoundException;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ResourceExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService service;

    @MockBean
    private UserRepository userRepository;


    private UserDTO criarUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setFirstName("Maria");
        dto.setLastName("Silva");
        dto.setEmail("maria@email.com");
        dto.setRoles(Set.of(new RoleDTO(1L, "ROLE_OPERATOR")));
        return dto;
    }

    private UserInsertDTO criarUserInsertDTO() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setFirstName("Maria");
        dto.setLastName("Silva");
        dto.setEmail("maria@email.com");
        dto.setPassword("123456");
        dto.setRoles(Set.of(new RoleDTO(1L, "ROLE_OPERATOR")));
        return dto;
    }

    private UserUpdateDTO criarUserUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setFirstName("Novo");
        dto.setLastName("Nome");
        dto.setEmail("novo@email.com");
        dto.setRoles(Set.of(new RoleDTO(1L, "ROLE_OPERATOR")));
        return dto;
    }

    @Test
    void deveBuscarUsuarios() throws Exception {
        Page<UserDTO> page = new PageImpl<>(List.of(criarUserDTO()));
        when(service.findAllPaged(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void deveBuscarUsuarioPorId() throws Exception {
        when(service.findById(1L)).thenReturn(criarUserDTO());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveRetornar404QuandoBuscarUsuarioInexistente() throws Exception {
        when(service.findById(99L))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveInserirUsuario() throws Exception {
        when(service.insert(any(UserInsertDTO.class)))
                .thenReturn(criarUserDTO());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarUserInsertDTO())))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        when(service.update(eq(1L), any(UserUpdateDTO.class)))
                .thenReturn(criarUserDTO());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarUserUpdateDTO())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deveRetornar404AoAtualizarUsuarioInexistente() throws Exception {
        when(service.update(eq(99L), any(UserUpdateDTO.class)))
                .thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(put("/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarUserUpdateDTO())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveExcluirUsuario() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoExcluirUsuarioInexistente() throws Exception {

        doThrow(ResourceNotFoundException.class)
                .when(service).delete(99L);

        mockMvc.perform(delete("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar422QuandoInserirUsuarioInvalido() throws Exception {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("email-invalido"); // inválido
        // firstName ausente → NotBlank

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].fieldName").exists());
    }



}
