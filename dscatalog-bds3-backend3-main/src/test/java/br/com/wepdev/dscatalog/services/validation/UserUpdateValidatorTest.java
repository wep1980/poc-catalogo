package br.com.wepdev.dscatalog.services.validation;

import br.com.wepdev.dscatalog.domain.User;
import br.com.wepdev.dscatalog.dto.UserUpdateDTO;
import br.com.wepdev.dscatalog.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidatorContext;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUpdateValidatorTest {

    @Mock
    private UserRepository repository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    private UserUpdateValidator validator;

    @BeforeEach
    void setup() {
        validator = new UserUpdateValidator(request, repository);
    }

    @Test
    void deveRetornarTrueQuandoEmailNaoPertenceOutroUsuario() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("novo@email.com");

        Map<String, String> uriVars = new HashMap<>();
        uriVars.put("id", "1");

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(uriVars);

        when(repository.findByEmail("novo@email.com"))
                .thenReturn(null);

        boolean result = validator.isValid(dto, context);

        assertTrue(result);
        verify(context, never()).buildConstraintViolationWithTemplate(any());
    }

    @Test
    void deveRetornarFalseQuandoEmailPertenceOutroUsuario() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("email@email.com");

        Map<String, String> uriVars = new HashMap<>();
        uriVars.put("id", "1");

        User user = new User();
        user.setId(2L);

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(uriVars);

        when(repository.findByEmail("email@email.com"))
                .thenReturn(user);

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
