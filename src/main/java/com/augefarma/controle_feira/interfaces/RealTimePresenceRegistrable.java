package com.augefarma.controle_feira.interfaces;

import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;

public interface RealTimePresenceRegistrable {
    void addToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService);
    void removeToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService);
}
