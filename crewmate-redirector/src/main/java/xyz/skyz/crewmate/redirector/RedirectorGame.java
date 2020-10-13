package xyz.skyz.crewmate.redirector;

import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.server.base.ServerEndpoint;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.base.game.GameManager;

public class RedirectorGame extends Game {

    public RedirectorGame(GameManager gameManager, int gameCode, ServerEndpoint serverEndpoint, GameOptionsData gameOptionsData) {
        super(gameManager, gameCode, serverEndpoint, gameOptionsData);
    }
}
