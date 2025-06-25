package br.ifsp.auth.dto;

import br.ifsp.auth.model.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponseDTO {

    private Long id;

    private RoleName roleName;
}
