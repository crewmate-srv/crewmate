package xyz.skyz.crewmate.redirector;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.skyz.crewmate.common.data.DisconnectReason;
import xyz.skyz.crewmate.common.data.GameKeywords;
import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.server.base.CrewmateServer;
import xyz.skyz.crewmate.server.base.Player;
import xyz.skyz.crewmate.server.base.ServerEndpoint;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.base.game.GameCode;
import xyz.skyz.crewmate.server.base.game.GameManager;
import xyz.skyz.crewmate.server.connection.Connection;
import xyz.skyz.crewmate.server.packet.registry.types.payload.packets.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RedirectorGameManager extends GameManager {

    private String webApiUrl;

    public RedirectorGameManager(CrewmateServer crewmateServer, String webApiUrl) {
        super(crewmateServer);
        this.webApiUrl = webApiUrl;
    }

    @Override
    public void handleHostGame(Connection connection, GameOptionsData gameOptionsData) {
        ServerEndpoint serverEndpoint = findServerEndpoint();
        // Send packet
        RedirectPacket redirectPacket = new RedirectPacket();
        try {
            redirectPacket.setEndpointHost(InetAddress.getByName(serverEndpoint.getHost()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
            joinGameErrorPacket.setDisconnectReason(DisconnectReason.ERROR);
            connection.sendReliablePacket(joinGameErrorPacket);
            return;
        }
        redirectPacket.setEndpointPort(serverEndpoint.getPort());
        connection.sendReliablePacket(redirectPacket);
    }

    @Override
    public void handleJoinGame(Connection connection, int gameCode) {
        HttpClient httpClient = HttpClient.newHttpClient();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", GameCode.gameCodeV2NumberToString(gameCode));
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(webApiUrl + "/find-code"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
            joinGameErrorPacket.setDisconnectReason(DisconnectReason.GAME_MISSING);
            connection.sendReliablePacket(joinGameErrorPacket);
            return;
        }
        JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
        if (responseBody.get("status").getAsInt() != 200) {
            JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
            joinGameErrorPacket.setDisconnectReason(DisconnectReason.GAME_MISSING);
            connection.sendReliablePacket(joinGameErrorPacket);
            return;
        }
        RedirectPacket redirectPacket = new RedirectPacket();
        try {
            redirectPacket.setEndpointHost(InetAddress.getByName(responseBody.get("endpointHost").getAsString()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        redirectPacket.setEndpointPort(responseBody.get("endpointPort").getAsInt());
        connection.sendReliablePacket(redirectPacket);
    }

    @Override
    public void handleRemovePlayer(Connection connection, int gameCode) {
        JoinGameErrorPacket joinGameErrorPacket = new JoinGameErrorPacket();
        joinGameErrorPacket.setDisconnectReason(DisconnectReason.ERROR);
        connection.sendReliablePacket(joinGameErrorPacket);
    }

    @Override
    public void removeGame(int gameCode) {
        // Not needed
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
        HttpClient httpClient = HttpClient.newHttpClient();
        JsonObject jsonObject = new JsonObject();
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(webApiUrl + "/get-node"))
                .GET().build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ServerEndpoint("127.0.0.1", 22024);
        }
        JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
        return new ServerEndpoint(responseBody.get("host").getAsString(), responseBody.get("port").getAsInt());
    }

    @Override
    public void searchForGames(Connection connection, GameOptionsData requestedOptions) {
        HttpClient httpClient = HttpClient.newHttpClient();
        JsonObject jsonObject = new JsonObject();
        System.out.println("map " + requestedOptions.getMapIdRaw());
        jsonObject.addProperty("map", requestedOptions.getMapIdRaw());
        jsonObject.addProperty("impostorCount", requestedOptions.getNumberImpostors());
        jsonObject.addProperty("language", GameKeywords.getKeywordBitmask(requestedOptions.getKeywords()));
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(webApiUrl + "/search"))
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString())).build();
        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return;
        }
        JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
        if (responseBody.get("status").getAsInt() != 200) {
            return;
        }

        List<Game> gameList = new ArrayList<>();
        for (JsonElement gameObjectElement : responseBody.get("results").getAsJsonArray()) {
            JsonObject gameObject = gameObjectElement.getAsJsonObject();
            ServerEndpoint gameEndpoint = new ServerEndpoint(gameObject.get("endpointHost").getAsString(), gameObject.get("endpointPort").getAsInt());
            GameOptionsData gameOptions = new GameOptionsData();
            gameOptions.setMapIdRaw(gameObject.get("map").getAsByte());
            gameOptions.setNumberImpostors(gameObject.get("impostorCount").getAsInt());
            gameOptions.setMaxPlayers(gameObject.get("maxPlayerCount").getAsShort());
            RedirectorGame game = new RedirectorGame(this, GameCode.gameCodeV2StringToNumber(gameObject.get("code").getAsString()), gameEndpoint, gameOptions);
            UUID hostUuid = UUID.randomUUID();
            game.setHostUuid(hostUuid);
            game.getPlayerMap().put(hostUuid, new Player(game, null, gameList.size(), gameObject.get("hostName").getAsString()));
            for (int i = 1; i < gameObject.get("playerCount").getAsInt(); i++) {
                UUID generatedUuid = UUID.randomUUID();
                while (game.getPlayerMap().containsKey(generatedUuid)) {
                    generatedUuid = UUID.randomUUID();
                }
                game.getPlayerMap().put(generatedUuid, new Player(game, null, i, ""));
            }
            gameList.add(game);
        }
        GetGameListV2Packet getGameListV2Packet = new GetGameListV2Packet();
        getGameListV2Packet.setGameList(gameList);
        getGameListV2Packet.setSkeldCount(responseBody.get("skeld").getAsInt());
        getGameListV2Packet.setMiraHqCount(responseBody.get("miraHq").getAsInt());
        getGameListV2Packet.setPolusCount(responseBody.get("polus").getAsInt());
        connection.sendReliablePacket(getGameListV2Packet);
    }
}
