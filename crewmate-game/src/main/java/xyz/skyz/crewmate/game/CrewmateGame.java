package xyz.skyz.crewmate.game;

import xyz.skyz.crewmate.common.data.GameOptionsData;
import xyz.skyz.crewmate.server.base.ServerEndpoint;
import xyz.skyz.crewmate.server.base.game.Game;
import xyz.skyz.crewmate.server.base.game.GameState;

public class CrewmateGame extends Game {

    public CrewmateGame(CrewmateGameManager crewmateGameManager, int gameCode, ServerEndpoint serverEndpoint, GameOptionsData gameOptionsData) {
        super(crewmateGameManager, gameCode, serverEndpoint, gameOptionsData);
    }

    @Override
    public void setGameState(GameState gameState) {
        super.setGameState(gameState);
        getCrewmateGameManager().updateApiGame(this);
    }

    @Override
    public void migrateHost() {
        super.migrateHost();
        getCrewmateGameManager().updateApiGame(this);
    }

    @Override
    public void checkLimboPlayers() {
        super.checkLimboPlayers();
        getCrewmateGameManager().updateApiGame(this);
    }

    public CrewmateGameManager getCrewmateGameManager() {
        return (CrewmateGameManager) getGameManager();
    }
}
