package xyz.skyz.crewmate.server.packet.registry.types.data;

import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.server.packet.enums.GameDataPayloadType;
import xyz.skyz.crewmate.server.packet.registry.PacketRegistry;
import xyz.skyz.crewmate.server.packet.registry.types.rpc.RpcRegistry;

public class GameDataRegistry extends PacketRegistry {

    public GameDataRegistry() {
        registerPacketRegistry(GameDataPayloadType.DATA.getTypeId(), GameDataRegistry.class);
        registerPacketRegistry(GameDataPayloadType.RPC.getTypeId(), RpcRegistry.class);
    }

    @Override
    public Integer getPayloadId(MessageReader reader) {
        reader.byteBuf.readerIndex(4);
        return (int) reader.readInt16();
    }
}
