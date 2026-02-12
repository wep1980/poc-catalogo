package br.com.wepdev.dscatalog.repositories;

import br.com.wepdev.dscatalog.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private User criarUsuario() {
        User user = new User();
        user.setFirstName("Maria");
        user.setEmail("maria@email.com");
        user.setPassword("123456");
        return user;
    }

    @Test
    void deveSalvarUsuario() {
        User user = criarUsuario();

        User salvo = repository.save(user);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getEmail()).isEqualTo("maria@email.com");
    }

    @Test
    void deveBuscarUsuarioPorId() {
        User user = repository.save(criarUsuario());

        Optional<User> result = repository.findById(user.getId());

        assertThat(result).isPresent();
    }

    @Test
    void deveRetornarVazioAoBuscarIdInexistente() {
        Optional<User> result = repository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void deveBuscarTodosUsuarios() {
        repository.deleteAll(); // 🔥 garante isolamento

        User u1 = criarUsuario();
        u1.setEmail("user1@email.com");

        User u2 = criarUsuario();
        u2.setEmail("user2@email.com");

        repository.save(u1);
        repository.save(u2);

        var result = repository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void deveExcluirUsuario() {
        User user = repository.save(criarUsuario());

        repository.deleteById(user.getId());

        Optional<User> result = repository.findById(user.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void deveBuscarUsuarioPorEmail() {
        repository.save(criarUsuario());

        User result = repository.findByEmail("maria@email.com");

        assertThat(result).isNotNull();
    }

    @Test
    void deveRetornarNullQuandoEmailNaoExiste() {
        User result = repository.findByEmail("naoexiste@email.com");

        assertThat(result).isNull();
    }
}
