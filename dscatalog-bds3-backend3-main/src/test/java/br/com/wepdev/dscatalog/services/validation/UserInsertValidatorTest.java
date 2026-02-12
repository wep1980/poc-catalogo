package br.com.wepdev.dscatalog.services.validation;

import br.com.wepdev.dscatalog.domain.User;
import br.com.wepdev.dscatalog.dto.UserInsertDTO;
import br.com.wepdev.dscatalog.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInsertValidatorTest {

    @Mock
    private UserRepository repository;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private UserInsertValidator validator;

    @BeforeEach
    void setup() {
        validator = new UserInsertValidator(repository);
    }

    @Test
    void deveRetornarTrueQuandoEmailNaoExiste() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("novo@email.com");

        when(repository.findByEmail("novo@email.com"))
                .thenReturn(null);

        boolean result = validator.isValid(dto, context);

        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void deveRetornarFalseQuandoEmailJaExiste() {
        UserInsertDTO dto = new UserInsertDTO();
        dto.setEmail("existente@email.com");

        when(repository.findByEmail("existente@email.com"))
                .thenReturn(new User());

        when(context.buildConstraintViolationWithTemplate(any()))
                .thenReturn(builder);

        when(builder.addPropertyNode(any()))
                .thenReturn(nodeBuilder);

        when(nodeBuilder.addConstraintViolation())
                .thenReturn(context);

        boolean result = validator.isValid(dto, context);

        assertFalse(result);
        verify(context).buildConstraintViolationWithTemplate(any());
    }
}
