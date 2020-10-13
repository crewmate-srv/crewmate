package xyz.skyz.crewmate.server.packet.registry;

import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.Packet;
import xyz.skyz.crewmate.server.packet.annotation.RegistryPacketInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class PacketRegistry {

    public Map<Integer, Class<? extends Packet>> packetMap = new HashMap<>();
    public Map<Integer, Class<? extends PacketRegistry>> packetRegistryMap = new HashMap<>();

    public void registerPacket(Packet packet) {
        RegistryPacketInfo packetInfo = packet.getClass().getAnnotation(RegistryPacketInfo.class);
        if (packetInfo == null) {
            throw new IllegalArgumentException("Packet does not contain PacketInfo annotation.");
        }
        packetMap.put(packetInfo.typeId(), packet.getClass());
    }

    public void registerPacketRegistry(int typeId, Class<? extends PacketRegistry> packetRegistry) {
        packetRegistryMap.put(typeId, packetRegistry);
    }

    public abstract Integer getPayloadId(MessageReader reader);

    public void handle(MessageReader reader, NetServer netServer, Connection connection) {
        Integer payloadId = getPayloadId(reader);
        Class<? extends Packet> packetClass;
        if (payloadId == null || !packetMap.containsKey(payloadId)) {
            if (!packetRegistryMap.containsKey(payloadId)) {
                System.out.println("Unknown type id " + payloadId + " passed to handler.");
                return;
            }
            netServer.getPacketManager().getPacketRegistry(packetRegistryMap.get(payloadId)).handle(reader, netServer, connection);
            return;
        } else {
            packetClass = packetMap.get(payloadId);
        }
        Packet packet;
        try {
            packet = packetClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }
        packet.deserialize(reader);
        packet.handle(netServer, connection);
    }
}
