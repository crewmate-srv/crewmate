package xyz.skyz.crewmate.server.packet.registry.types.payload.packets;

import io.netty.buffer.ByteBuf;
import xyz.skyz.crewmate.common.message.MessageReader;
import xyz.skyz.crewmate.common.message.MessageWriter;
import xyz.skyz.crewmate.server.NetServer;
import xyz.skyz.crewmate.server.base.Player;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.annotation.PacketInfo;
import xyz.skyz.crewmate.server.packet.annotation.RegistryPacketInfo;
import xyz.skyz.crewmate.server.packet.enums.NetPacketType;
import xyz.skyz.crewmate.server.packet.enums.PayloadType;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

import java.util.ArrayList;
import java.util.List;

@PacketInfo(packetType = NetPacketType.RELIABLE)
@RegistryPacketInfo(typeId = 7)
public class JoinedGamePacket extends ReliablePacket {

    private int gameCode;
    private int playerId;
    private int hostId;
    private List<Player> otherPlayers = new ArrayList<>();

    @Override
    public ByteBuf serialize(MessageWriter writer) {
        setPacketTypeId((short) NetPacketType.RELIABLE.getTypeId());
        writer.startMessage((short) PayloadType.JOINED_GAME.getTypeId());
        writer.writeInt32(gameCode);
        writer.writeInt32(playerId);
        writer.writeInt32(hostId);
        writer.writePackedInt32(otherPlayers.size());
        for (Player player : otherPlayers) {
            writer.writePackedInt32(player.getPlayerId());
        }
        writer.endMessage();

        /*writer.startMessage((short) PayloadType.ALTER_GAME.getTypeId());
        writer.writeInt32LE(gameCode);
        writer.endMessage();*/
        return writer.getByteBuf();
    }

    @Override
    public void deserialize(MessageReader reader) {

    }

    @Override
    public void handle(NetServer netServer, Connection connection) {

    }

    public void setOtherPlayersFromGame(Game game, Player player){
        for (Player otherPlayer : game.getPlayerMap().values()) {
            if (otherPlayer.getPlayerId() != player.getPlayerId()) {
                otherPlayers.add(otherPlayer);
            }
        }
    }

    public int getGameCode() {
        return gameCode;
    }

    public void setGameCode(int gameCode) {
        this.gameCode = gameCode;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public List<Player> getOtherPlayers() {
        return otherPlayers;
    }

    public void setOtherPlayers(List<Player> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }
}
