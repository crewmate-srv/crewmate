package xyz.skyz.crewmate.game;

import com.google.gson.JsonObject;
import xyz.skyz.crewmate.common.data.DisconnectReason;
import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.server.base.CrewmateServer;
import xyz.skyz.crewmate.server.base.Player;
import xyz.skyz.crewmate.server.base.ServerEndpoint;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.base.game.GameCode;
import xyz.skyz.crewmate.server.base.game.GameManager;
import xyz.skyz.crewmate.server.base.game.GameState;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.GetGameListV2Packet;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.HostGamePacket;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.JoinGameErrorPacket;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.RemovePlayerPacket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class CrewmateGameManager extends GameManager {

    private ServerEndpoint serverEndpoint;
    private String webApiUrl;

    public CrewmateGameManager(CrewmateServer crewmateServer, ServerEndpoint serverEndpoint, String webApiUrl) {
        super(crewmateServer);
        this.serverEndpoint = serverEndpoint;
        this.webApiUrl = webApiUrl;
        HttpClient httpClient = HttpClient.newHttpClient();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("host", serverEndpoint.getHost());
        jsonObject.addProperty("port", serverEndpoint.getPort());
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(webApiUrl + "/add-node"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
    }

    public void updateApiGame(Game game) {
        HttpClient httpClient = HttpClient.newHttpClient();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", GameCode.gameCodeV2NumberToString(game.getGameCode()));
        jsonObject.addProperty("endpointHost", game.getServerEndpoint().getHost());
        jsonObject.addProperty("endpointPort", game.getServerEndpoint().getPort());
        jsonObject.addProperty("gameState", game.getGameState().name());
        if (game.getHostUuid() != null && game.getPlayerMap().containsKey(game.getHostUuid())) {
            jsonObject.addProperty("hostName", game.getPlayerMap().get(game.getHostUuid()).getName());
        } else {
            jsonObject.addProperty("hostName", "Unknown");
        }
        jsonObject.addProperty("impostorCount", game.getGameOptionsData().getNumberImpostors());
        jsonObject.addProperty("map", game.getGameOptionsData().getMapIdRaw());
        jsonObject.addProperty("maxPlayerCount", game.getGameOptionsData().getMaxPlayers());
        jsonObject.addProperty("playerCount", game.getPlayerMap().size());
        jsonObject.addProperty("isPublic", game.isPublic());
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(webApiUrl + "/update-game"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        try {
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleHostGame(Connection connection, GameOptionsData gameOptionsData) {
        int generatedCode = generateGameCode();
        CrewmateGame crewmateGame = new CrewmateGame(this, generatedCode, serverEndpoint, gameOptionsData);
        getGameMap().put(generatedCode, crewmateGame);

        // Send packet
        HostGamePacket hostGamePacket = new HostGamePacket();
        hostGamePacket.setGameCode(generatedCode);
        connection.sendReliablePacket(hostGamePacket);

        // Save in API
        updateApiGame(crewmateGame);
    }

    @Override
    public void handleJoinGame(Connection connection, int gameCode) {
        if (!isGameLocal(gameCode)) {
            JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
            joinGameErrorPacket.setDisconnectReason(DisconnectReason.GAME_MISSING);
            connection.sendReliablePacket(joinGameErrorPacket);
            return;
        }
        Game game = getGameMap().get(gameCode);
        if (game.getGameState() == GameState.STARTED) {
            JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
            joinGameErrorPacket.setDisconnectReason(DisconnectReason.GAME_STARTED);
            connection.sendReliablePacket(joinGameErrorPacket);
            return;
        }
        if (game.getGameState() == GameState.DESTROYED) {
            JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
            joinGameErrorPacket.setDisconnectReason(DisconnectReason.DESTROY);
            connection.sendReliablePacket(joinGameErrorPacket);
            return;
        }
        Player player;
        if (getConnectionUuidGame().containsKey(connection.getConnectionUuid())) {
            player = getConnectionUuidGame().get(connection.getConnectionUuid()).getPlayerMap().get(connection.getConnectionUuid());
        } else {
            int playerId = game.getNextPlayerId();
            player = new Player(game, connection, playerId, connection.getPlayerName());
        }
        game.addPlayer(connection.getConnectionUuid(), player);
        updateApiGame(game);
    }

    @Override
    public void handleRemovePlayer(Connection connection, int gameCode) {
        Game game = getGameMap().get(gameCode);
        if (game == null) {
            return;
        }
        Player player = game.getPlayerMap().get(connection.getConnectionUuid());
        if (player == null) {
            return;
        }
        game.removePlayer(connection.getConnectionUuid());
        if (game.getPlayerMap().size() < 2) {
            return;
        }
        RemovePlayerPacket removePlayerPacket = new RemovePlayerPacket();
        removePlayerPacket.setGameCode(gameCode);
        removePlayerPacket.setPlayerId(player.getPlayerId());
        removePlayerPacket.setHostId(game.getPlayerMap().get(game.getHostUuid()).getPlayerId());
        removePlayerPacket.setReason((short) DisconnectReason.EXIT_GAME.getTypeId());
        game.sendToAllExcept(removePlayerPacket, connection.getConnectionUuid());
        if (getGameMap().containsKey(gameCode)) {
            updateApiGame(game);
        }
    }

    @Override
    public void removeGame(int gameCode) {
        if (isGameLocal(gameCode)) {
            HttpClient httpClient = HttpClient.newHttpClient();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", GameCode.gameCodeV2NumberToString(gameCode));
            HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(webApiUrl + "/remove-game"))
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
            try {
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            getGameMap().get(gameCode).setGameState(GameState.DESTROYED);
            getGameMap().remove(gameCode);
        }
    }

    @Override
    public int generateGameCode() {
        int generatedCode = GameCode.gameCodeV2StringToNumber(GameCode.generateRandomGameCodeV2());
        while (isGameLocal(generatedCode)) {
            generatedCode = GameCode.gameCodeV2StringToNumber(GameCode.generateRandomGameCodeV2());
        }
        return generatedCode;
    }

    @Override
    public ServerEndpoint findServerEndpoint() {
        return serverEndpoint;
    }

    @Override
    public void searchForGames(Connection connection, GameOptionsData gameOptionsData) {
        GetGameListV2Packet getGameListV2Packet = new GetGameListV2Packet();
        getGameListV2Packet.setGameList(new ArrayList<>());
        getGameListV2Packet.setSkeldCount(0);
        getGameListV2Packet.setMiraHqCount(0);
        getGameListV2Packet.setPolusCount(0);
        connection.sendReliablePacket(getGameListV2Packet);
    }
}
