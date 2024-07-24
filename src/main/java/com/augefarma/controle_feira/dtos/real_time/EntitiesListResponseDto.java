package com.augefarma.controle_feira.dtos.real_time;

import com.augefarma.controle_feira.dtos.client.ClientResponseDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class EntitiesListResponseDto {

    private List<ClientResponseDto> clients = new ArrayList<>();
    private List<LaboratoryResponseDto> laboratories = new ArrayList<>();

    public EntitiesListResponseDto(List<ClientResponseDto> clients, List<LaboratoryResponseDto> laboratories) {
        this.clients = clients;
        this.laboratories = laboratories;
    }

    public void addClient(ClientResponseDto client) {
        clients.add(client);
    }

    public void removeClient(ClientResponseDto client) {
        clients.remove(client);
    }

    public void addLaboratory(LaboratoryResponseDto laboratory) {
        laboratories.add(laboratory);
    }

    public void removeLaboratory(LaboratoryResponseDto laboratory) {
        laboratories.remove(laboratory);
    }
}
