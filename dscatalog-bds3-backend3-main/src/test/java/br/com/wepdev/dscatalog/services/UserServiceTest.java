package br.com.wepdev.dscatalog.services;

import br.com.wepdev.dscatalog.domain.Role;
import br.com.wepdev.dscatalog.domain.User;
import br.com.wepdev.dscatalog.dto.RoleDTO;
import br.com.wepdev.dscatalog.dto.UserDTO;
import br.com.wepdev.dscatalog.dto.UserInsertDTO;
import br.com.wepdev.dscatalog.dto.UserUpdateDTO;
import br.com.wepdev.dscatalog.repositories.RoleRepository;
import br.com.wepdev.dscatalog.repositories.UserRepository;
import br.com.wepdev.dscatalog.services.exceptions.DatabaseException;
import br.com.wepdev.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    private User criarUsuario() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Maria");
        user.setLastName("Silva");
        user.setEmail("maria@email.com");
        user.setPassword("senhaCodificada");
        user.getRoles().add(criarRole());
        return user;
    }

    private Role criarRole() {
        Role role = new Role();
        role.setId(1L);
        role.setAuthority("ROLE_USER");
        return role;
    }


    @Test
    void deveBuscarUsuariosPaginados() {
        Page<User> page = new PageImpl<>(List.of(criarUsuario()));
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(pageable)).thenReturn(page);

        Page<UserDTO> result = service.findAllPaged(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void deveBuscarUsuarioPorIdQuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(criarUsuario()));

        UserDTO dto = service.findById(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getEmail()).isEqualTo("maria@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveInserirUsuario() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setFirstName("Maria");
        dto.setLastName("Silva");
        dto.setEmail("maria@email.com");
        dto.setPassword("123456");
        dto.setRoles(Set.of(new RoleDTO(1L, null)));

        User savedUser = criarUsuario();

        when(passwordEncoder.encode("123456")).thenReturn("senhaCodificada");
        when(repository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = service.insert(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("maria@email.com");
    }

    @Test
    void deveAtualizarUsuarioQuandoExiste() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setFirstName("Novo");
        dto.setLastName("Nome");
        dto.setEmail("novo@email.com");
        dto.setRoles(Set.of(new RoleDTO(1L, null)));

        User entity = criarUsuario();

        when(repository.getReferenceById(1L)).thenReturn(entity);
        when(repository.save(any(User.class))).thenReturn(entity);

        UserDTO result = service.update(1L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deveLancarExcecaoAoAtualizarUsuarioInexistente() {
        when(repository.getReferenceById(99L)).thenThrow(EntityNotFoundException.class);

        UserUpdateDTO dto = new UserUpdateDTO();

        assertThatThrownBy(() -> service.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveExcluirUsuarioQuandoExiste() {
        doNothing().when(repository).deleteById(1L);

        assertThatCode(() -> service.delete(1L))
                .doesNotThrowAnyException();
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExisteAoExcluir() {
        doThrow(EmptyResultDataAccessException.class)
                .when(repository).deleteById(99L);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveLancarExcecaoQuandoViolacaoDeIntegridadeAoExcluir() {
        doThrow(DataIntegrityViolationException.class)
                .when(repository).deleteById(1L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(DatabaseException.class);
    }


    @Test
    void deveRetornarUserDetailsQuandoEmailExiste() {
        when(repository.findByEmail("maria@email.com")).thenReturn(criarUsuario());

        var result = service.loadUserByUsername("maria@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("maria@email.com");
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        when(repository.findByEmail("x@email.com")).thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("x@email.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
