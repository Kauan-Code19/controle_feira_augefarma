package com.augefarma.controle_feira.dtos.client;

import com.augefarma.controle_feira.entities.client.ClientEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class ClientResponseDto {

    private Long id;

    private String fullName;

    private String cpf;

    private String cnpj;

    private String corporateReason;

    public ClientResponseDto(ClientEntity client) {
        id = client.getId();
        fullName = client.getFullName();
        cpf = client.getCpf();
        cnpj = client.getCnpj();
        corporateReason = client.getCorporateReason();
    }
}
