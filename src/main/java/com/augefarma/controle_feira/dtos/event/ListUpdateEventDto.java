package com.augefarma.controle_feira.dtos.event;

import com.augefarma.controle_feira.dtos.real_time.EntitiesListResponseDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ListUpdateEventDto extends ApplicationEvent{

    private final EntitiesListResponseDto updatedList;

    public ListUpdateEventDto(Object source, EntitiesListResponseDto updatedList) {
        super(source);
        this.updatedList = updatedList;
    }
}
