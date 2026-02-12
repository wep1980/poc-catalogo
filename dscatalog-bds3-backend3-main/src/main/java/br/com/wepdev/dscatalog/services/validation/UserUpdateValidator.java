package br.com.wepdev.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.servlet.HandlerMapping;

import br.com.wepdev.dscatalog.dto.UserUpdateDTO;
import br.com.wepdev.dscatalog.domain.User;
import br.com.wepdev.dscatalog.repositories.UserRepository;
import br.com.wepdev.dscatalog.controller.exceptions.FieldMessage;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {
	

	private final HttpServletRequest request;

	private final UserRepository repository;

    public UserUpdateValidator(HttpServletRequest request, UserRepository repository) {
        this.request = request;
        this.repository = repository;
    }

    @Override
	public void initialize(UserUpdateValid ann) {
		// Método não requer inicialização para este validador customizado
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		@SuppressWarnings("unchecked")
		var uriVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		long userId = Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		User user = repository.findByEmail(dto.getEmail());
		if (user != null && userId != user.getId()) {
			list.add(new FieldMessage("email", "Email já existe"));
		}

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
