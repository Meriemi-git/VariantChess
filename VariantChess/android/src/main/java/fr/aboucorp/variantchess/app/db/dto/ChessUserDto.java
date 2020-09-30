package fr.aboucorp.variantchess.app.db.dto;

import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;

public class ChessUserDto {
    public static ChessUser fromUserToChessUser(User user) {
        ChessUser chessUser = new ChessUser();
        chessUser.userId = user.getId();
        chessUser.username = user.getUsername();
        chessUser.metadata = user.getMetadata();
        return chessUser;
    }
}
