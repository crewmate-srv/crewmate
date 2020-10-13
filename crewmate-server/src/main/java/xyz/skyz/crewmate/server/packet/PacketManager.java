package xyz.skyz.crewmate.server.packet;

import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;
import xyz.skyz.crewmate.server.packet.registry.PacketRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PacketManager {

    private NetServer netServer;
    private Map<Class<? extends PacketRegistry>, PacketRegistry> packetRegistryMap = new HashMap<>();
    private Map<Integer, Class<? extends Packet>> packetMap = new HashMap<>();

    public PacketManager(NetServer netServer) {
        this.netServer = netServer;
    }

    public void registerPacket(Packet packet) {
        PacketInfo packetInfo = packet.getClass().getAnnotation(PacketInfo.class);
        if (packetInfo != null) {
            packetMap.put(packetInfo.packetType().getTypeId(), packet.getClass());
        }
    }

    public Packet createPacketFromId(NetPacketType netPacketType) {
        if (packetMap.containsKey(netPacketType.getTypeId())) {
            Class<? extends Packet> packet = packetMap.get(netPacketType.getTypeId());
            try {
                return packet.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    public void registerRegistry(PacketRegistry packetRegistry) {
        packetRegistryMap.put(packetRegistry.getClass(), packetRegistry);
    }

    public PacketRegistry getPacketRegistry(Class<? extends PacketRegistry> packetRegistryClass) {
        return packetRegistryMap.get(packetRegistryClass);
    }

    public NetServer getNetServer() {
        return netServer;
    }
}
