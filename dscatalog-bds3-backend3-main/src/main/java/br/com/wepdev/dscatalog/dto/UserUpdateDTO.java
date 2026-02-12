package br.com.wepdev.dscatalog.dto;

import br.com.wepdev.dscatalog.services.validation.UserUpdateValid;

import java.io.Serial;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO {
	@Serial
	private static final long serialVersionUID = 1L;

}
