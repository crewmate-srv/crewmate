package xyz.skyz.crewmate.server.packet.registry.types.rpc;

import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.server.packet.registry.PacketRegistry;

public class RpcRegistry extends PacketRegistry {

    public RpcRegistry() {
        //
    }

    @Override
    public Integer getPayloadId(MessageReader reader) {
        return null;
    }
}
