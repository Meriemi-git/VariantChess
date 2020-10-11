package fr.aboucorp.variantchess.app.managers.boards;

import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.exceptions.UnknownGameRulesException;
import fr.aboucorp.variantchess.app.utils.state.ClassicBoardStateBuilder;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class BoardManagerFactory {

    public static BoardManager getBoardManagerFromGameRules(GameRules gameRules, Board3dManager board3dManager, GameEventManager gameEventManager) throws UnknownGameRulesException {
        switch (gameRules.name.toLowerCase()) {
            case "random":
                // TODO implements radmoRules
            case "classic":
                ClassicBoard classicBoard = new ClassicBoard(gameEventManager);
                ClassicRuleSet classicRules = new ClassicRuleSet(classicBoard, gameEventManager);
                ClassicBoardStateBuilder classicBoardStateBuilder = new ClassicBoardStateBuilder(classicBoard, classicRules);
                return new ClassicBoardManager(board3dManager, classicBoard, classicRules, gameEventManager, classicBoardStateBuilder);
            default:
                throw new UnknownGameRulesException(String.format("Unknown Gamerules with the name : %s", gameRules.name));
        }
    }
}
