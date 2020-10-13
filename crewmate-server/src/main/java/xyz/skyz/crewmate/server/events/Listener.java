package xyz.skyz.crewmate.server.events;

import xyz.skyz.crewmate.server.events.types.Event;
import xyz.skyz.crewmate.server.events.types.PacketEvent;
import xyz.skyz.crewmate.server.events.types.packet.*;

public class Listener {

    public void on(Event event) {
    }

    public void onPacket(PacketEvent event) {
    }

    public void onPacketAcknowledgement(PacketAcknowledgementEvent event) {
    }

    public void onPacketDisconnect(PacketDisconnectEvent event) {
    }

    public void onPacketHello(PacketHelloEvent event) {
    }

    public void onPacketNormal(PacketNormalEvent event) {
    }

    public void onPacketPing(PacketPingEvent event) {
    }

    public void onPacketReliable(PacketReliableEvent event) {
    }

    public void handleEvent(Event event) {
        on(event);
        if (event instanceof PacketEvent) {
            onPacket((PacketEvent) event);
            if (event instanceof PacketAcknowledgementEvent) {
                onPacketAcknowledgement((PacketAcknowledgementEvent) event);
            } else if (event instanceof PacketDisconnectEvent) {
                onPacketDisconnect((PacketDisconnectEvent) event);
            } else if (event instanceof PacketHelloEvent) {
                onPacketHello((PacketHelloEvent) event);
            } else if (event instanceof PacketNormalEvent) {
                onPacketNormal((PacketNormalEvent) event);
            } else if (event instanceof PacketPingEvent) {
                onPacketPing((PacketPingEvent) event);
            } else if (event instanceof PacketReliableEvent) {
                onPacketReliable((PacketReliableEvent) event);
            }
        }
    }
}
