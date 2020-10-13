package xyz.skyz.crewmate.server.packet.annotation;

import xyz.skyz.crewmate.server.packet.enums.NetPacketType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PacketInfo {
    NetPacketType packetType();
}
