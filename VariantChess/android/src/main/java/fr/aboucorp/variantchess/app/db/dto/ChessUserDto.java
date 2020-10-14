package fr.aboucorp.variantchess.app.db.dto;

import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.app.db.entities.VariantUser;

public class ChessUserDto {
    public static VariantUser fromUserToChessUser(User user) {
        VariantUser variantUser = new VariantUser();
        variantUser.userId = user.getId();
        variantUser.username = user.getUsername();
        variantUser.metadata = user.getMetadata();
        return variantUser;
    }
}
