package xyz.skyz.crewmate.server.packet.registry.types.payload.packets;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.data.GameMaps;
import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.annotation.RegistryPacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;
import xyz.skyz.crewmate.server.packet.enums.PayloadType;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.List;

@PacketInfo(packetType = NetPacketType.RELIABLE)
@RegistryPacketInfo(typeId = 16)
public class GetGameListV2Packet extends ReliablePacket {

    private GameOptionsData gameOptionsData;
    private List<Game> gameList;
    private int skeldCount;
    private int miraHqCount;
    private int polusCount;

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());

        writer.startMessage((short) PayloadType.GET_GAME_LIST_V2.getTypeId());

        writer.startMessage((short) 1);
        writer.writeInt32(skeldCount);
        writer.writeInt32(miraHqCount);
        writer.writeInt32(polusCount);
        writer.endMessage();

        writer.startMessage((short) 0);
        for (Game game : gameList) {
            writer.startMessage((short) 0);
            try {
                writer.writeBytes(Inet4Address.getByName(game.getServerEndpoint().getHost()).getAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            writer.writeUInt16(game.getServerEndpoint().getPort());
            writer.writeInt32(game.getGameCode());
            String hostName = game.getPlayerMap().get(game.getHostUuid()).getName();
            if (hostName == null) {
                hostName = Integer.toString(game.getPlayerMap().get(game.getHostUuid()).getPlayerId());
            }
            writer.writeString(hostName);
            writer.writeByte((short) game.getPlayerMap().size());
            writer.writePackedInt32(1); // Age
            writer.writeByte(game.getGameOptionsData().getMapIdRaw()); // Map
            writer.writeByte((byte) game.getGameOptionsData().getNumberImpostors());
            writer.writeByte((byte) game.getGameOptionsData().getMaxPlayers());
            writer.endMessage();
        }
        writer.endMessage();

        writer.endMessage();

        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {
        setPacketTypeId(reader.readByte()); // Packet type
        setNonce(reader.readInt16()); // Nonce

        reader.readByte(); // Length
        reader.readInt16(); // Payload id

        this.gameOptionsData = new GameOptionsData();
        this.gameOptionsData.deserialize(reader);
    }

    @Override
    public void handle(NetServer netServer, Connection connection) {
        netServer.getCrewmateServer().getGameManager().searchForGames(connection, this.gameOptionsData);
    }

    public GameOptionsData getGameOptionsData() {
        return gameOptionsData;
    }

    public void setGameOptionsData(GameOptionsData gameOptionsData) {
        this.gameOptionsData = gameOptionsData;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }

    public int getSkeldCount() {
        return skeldCount;
    }

    public void setSkeldCount(int skeldCount) {
        this.skeldCount = skeldCount;
    }

    public int getMiraHqCount() {
        return miraHqCount;
    }

    public void setMiraHqCount(int miraHqCount) {
        this.miraHqCount = miraHqCount;
    }

    public int getPolusCount() {
        return polusCount;
    }

    public void setPolusCount(int polusCount) {
        this.polusCount = polusCount;
    }
}
