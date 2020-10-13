package xyz.skyz.crewmate.server.base.game;

import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.server.base.Player;
import xyz.skyz.crewmate.server.base.PlayerLimboState;
import xyz.skyz.crewmate.server.base.ServerEndpoint;
import xyz.skyz.crewmate.server.packet.Packet;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.JoinGamePacket;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.JoinedGamePacket;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.WaitForHostPacket;
import xyz.skyz.crewmate.server.packet.types.ReliablePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class Game {

    private GameManager gameManager;
    private int gameCode;
    private int nextPlayerId = 0;
    private ServerEndpoint serverEndpoint;
    private GameOptionsData gameOptionsData;
    private UUID hostUuid = null;
    private boolean isPublic = false;
    private GameState gameState = GameState.NOT_STARTED;
    private Map<UUID, Player> playerMap = new HashMap<>();

    public Game(GameManager gameManager, int gameCode, ServerEndpoint serverEndpoint, GameOptionsData gameOptionsData) {
        this.gameManager = gameManager;
        this.gameCode = gameCode;
        this.serverEndpoint = serverEndpoint;
        this.gameOptionsData = gameOptionsData;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public int getGameCode() {
        return gameCode;
    }

    public ServerEndpoint getServerEndpoint() {
        return serverEndpoint;
    }

    public GameOptionsData getGameOptionsData() {
        return gameOptionsData;
    }

    public UUID getHostUuid() {
        return hostUuid;
    }

    public void setHostUuid(UUID hostUuid) {
        this.hostUuid = hostUuid;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Map<UUID, Player> getPlayerMap() {
        return playerMap;
    }

    public void sendTo(Packet packet, UUID targetUuid) {
        if (!playerMap.containsKey(targetUuid)) {
            return;
        }
        Player player = playerMap.get(targetUuid);
        if (packet instanceof ReliablePacket) {
            player.getConnection().sendReliablePacket((ReliablePacket) packet);
        } else {
            player.getConnection().sendPacket(packet);
        }
    }

    public void sendToAllExcept(Packet packet, UUID senderUuid) {
        for (UUID uuid : playerMap.keySet()) {
            if (uuid == senderUuid) {
                continue;
            }
            sendTo(packet, uuid);
        }
    }

    public void sendToAll(Packet packet) {
        for (UUID uuid : playerMap.keySet()) {
            sendTo(packet, uuid);
        }
    }

    public int getNextPlayerId() {
        nextPlayerId = nextPlayerId + 1;
        return nextPlayerId;
    }

    public void addPlayer(UUID connectionUuid, Player player) {
        if (!playerMap.containsKey(connectionUuid)) {
            playerMap.put(connectionUuid, player);
        }
        if (!gameManager.getConnectionUuidGame().containsKey(connectionUuid)) {
            gameManager.getConnectionUuidGame().put(connectionUuid, this);
        }
        if (getGameState() == GameState.NOT_STARTED) {
            handleJoinGameNew(connectionUuid, player);
        } else if (getGameState() == GameState.ENDED) {
            handleJoinGameNext(connectionUuid, player);
        }
    }

    public void handleJoinGameNew(UUID connectionUuid, Player player) {

        // Broadcast player join
        JoinedGamePacket joinedGamePacket = new JoinedGamePacket();
        joinedGamePacket.setGameCode(gameCode);
        joinedGamePacket.setPlayerId(player.getPlayerId());
        if (hostUuid == null) {
            hostUuid = connectionUuid;
            joinedGamePacket.setHostId(player.getPlayerId());
        } else {
            joinedGamePacket.setHostId(playerMap.get(hostUuid).getPlayerId());
        }
        joinedGamePacket.setOtherPlayersFromGame(this, player);
        sendTo(joinedGamePacket, connectionUuid);

        // Join game packet
        JoinGamePacket joinGamePacket = new JoinGamePacket();
        joinGamePacket.setGameCode(gameCode);
        joinGamePacket.setPlayerId(player.getPlayerId());
        joinGamePacket.setHostId(playerMap.get(hostUuid).getPlayerId());
        sendToAllExcept(joinGamePacket, connectionUuid);
    }

    public void handleJoinGameNext(UUID connectionUuid, Player player) {
        if (hostUuid == connectionUuid) {
            setGameState(GameState.NOT_STARTED);
            handleJoinGameNew(connectionUuid, player);
            checkLimboPlayers();
            return;
        }
        // Set sender limbo state
        player.setPlayerLimboState(PlayerLimboState.WAITING_FOR_HOST);
        // Wait for host packet
        WaitForHostPacket waitForHostPacket = new WaitForHostPacket();
        waitForHostPacket.setGameCode(gameCode);
        waitForHostPacket.setPlayerId(player.getPlayerId());
        sendTo(waitForHostPacket, connectionUuid);
        // Join game packet
        JoinGamePacket joinGamePacket = new JoinGamePacket();
        joinGamePacket.setGameCode(gameCode);
        joinGamePacket.setPlayerId(player.getPlayerId());
        joinGamePacket.setHostId(playerMap.get(hostUuid).getPlayerId());
        sendToAllExcept(joinGamePacket, connectionUuid);
    }

    public void removePlayer(UUID connectionUuid) {
        if (playerMap.containsKey(connectionUuid)) {
            Player player = playerMap.get(connectionUuid);
            playerMap.remove(connectionUuid);
            // Broadcast player disconnect
            player.setPlayerLimboState(PlayerLimboState.PRE_SPAWN);
            if (playerMap.size() < 1) {
                gameManager.removeGame(gameCode);
            } else if (connectionUuid == hostUuid) {
                migrateHost();
            }
        }
        gameManager.getConnectionUuidGame().remove(connectionUuid);
    }

    public void migrateHost() {
        Optional<Map.Entry<UUID, Player>> newHostOptional = getPlayerMap().entrySet().stream().findFirst();
        if (newHostOptional.isEmpty()) {
            gameManager.removeGame(gameCode);
            return;
        }
        Map.Entry<UUID, Player> newHost = newHostOptional.get();
        Player oldHostPlayer = getPlayerMap().get(hostUuid);
        setHostUuid(newHost.getKey());
        if (gameState == GameState.ENDED && (oldHostPlayer.getPlayerLimboState() == PlayerLimboState.PRE_SPAWN ||
                oldHostPlayer.getPlayerLimboState() == PlayerLimboState.WAITING_FOR_HOST)) {
            setGameState(GameState.NOT_STARTED);
            int playerId = getNextPlayerId();
            Player newHostPlayer = new Player(this, getGameManager().getCrewmateServer().getNetServer().getConnectionByUuid(newHost.getKey()), playerId, newHost.getValue().getName());
            newHostPlayer.setPlayerLimboState(PlayerLimboState.NOT_LIMBO);
            addPlayer(newHost.getKey(), newHostPlayer);
            checkLimboPlayers();
        }
    }

    public void checkLimboPlayers() {
        for (Player player : playerMap.values()) {
            if (player.getPlayerLimboState() == PlayerLimboState.WAITING_FOR_HOST) {
                player.setPlayerLimboState(PlayerLimboState.NOT_LIMBO);

                JoinedGamePacket joinedGamePacket = new JoinedGamePacket();
                joinedGamePacket.setGameCode(gameCode);
                joinedGamePacket.setPlayerId(player.getPlayerId());
                joinedGamePacket.setHostId(playerMap.get(hostUuid).getPlayerId());
                joinedGamePacket.setOtherPlayersFromGame(this, player);
                sendTo(joinedGamePacket, player.getConnection().getConnectionUuid());
            }
        }
    }
}
