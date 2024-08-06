package com.augefarma.controle_feira.controllers.socket;

import com.augefarma.controle_feira.dtos.event.ListUpdateEventDto;
import com.augefarma.controle_feira.dtos.real_time.EntitiesListResponseDto;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class RealTimeUpdateController {

    private final RealTimeUpdateService realTimeUpdateService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor for RealTimeUpdateController.
     *
     * @param realTimeUpdateService service for handling real-time updates
     * @param messagingTemplate     template for sending messages to WebSocket clients
     */
    @Autowired
    public RealTimeUpdateController(@Lazy RealTimeUpdateService realTimeUpdateService,
                                    SimpMessagingTemplate messagingTemplate) {
        this.realTimeUpdateService = realTimeUpdateService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handles requests for initial data and sends it to WebSocket clients.
     *
     * @return an EntitiesListResponseDto containing the initial data for clients and laboratories
     */
    @MessageMapping("/get-initial-data")
    @SendTo("/topic/realtime")
    public EntitiesListResponseDto getInitialData() {
        // Retrieves the initial data from the service and sends it to WebSocket clients
        return realTimeUpdateService.getEntitiesListResponseDto();
    }

    /**
     * Handles list update events and sends the updated list to WebSocket clients.
     *
     * @param event the event containing the updated list of entities
     */
    @EventListener
    public void handleListUpdateEvent(ListUpdateEventDto event) {
        // Sends the updated list of entities to WebSocket clients
        messagingTemplate.convertAndSend("/topic/realtime", event.getUpdatedList());
    }
}
