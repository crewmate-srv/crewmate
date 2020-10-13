package xyz.skyz.crewmate.server.packet.registry.types.payload;

import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.server.packet.registry.PacketRegistry;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.*;

public class PayloadRegistry extends PacketRegistry {

    public PayloadRegistry() {
        registerPacket(new SpecialRelayOnlyPacket());
        registerPacket(new HostGamePacket());
        registerPacket(new JoinGamePacket());
        registerPacket(new StartGamePacket());
        registerPacket(new GameDataPacket());
        registerPacket(new GameDataToPacket());
        registerPacket(new EndGamePacket());
        registerPacket(new GetGameListV2Packet());
    }

    @Override
    public Integer getPayloadId(MessageReader reader) {
        short packetTypeId = reader.readByte(); // Read packet type
        if (packetTypeId == 0) {
            return -1;
        } else if (packetTypeId == 1) {
            short nonce = reader.readInt16(); // Nonce
            //short length = reader.readByte(); // Length
            //short payloadId = reader.readInt16();

            short length = reader.readInt16(); // Length
            short payloadId = reader.readByte();

            reader.byteBuf.readerIndex(0);
            return (int) payloadId;
        }
        return null;
    }
}
