package fr.aboucorp.variantchess.app.parcelables;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.enums.PieceId;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.Duration;

public class TurnP extends Turn implements Parcelable {

    protected PlayerP player;


    protected TurnP(Parcel in) {
        super.deadPiece = Enum.valueOf(PieceId.class,in.readString());
        super.duration = Duration.ofSeconds(in.readLong());
        super.fen = in.readString();
        super.from = Location.fromString(in.readString());
        super.played = Enum.valueOf(PieceId.class,in.readString());
        this.player = (PlayerP) in.readValue(PlayerP.class.getClassLoader());
        super.to = Location.fromString(in.readString());
        super.turnNumber = in.readInt();
    }

    public static final Creator<TurnP> CREATOR = new Creator<TurnP>() {
        @Override
        public TurnP createFromParcel(Parcel in) {
            return new TurnP(in);
        }

        @Override
        public TurnP[] newArray(int size) {
            return new TurnP[size];
        }
    };

    public static final Creator<Turn> CREATORTurn = new Creator<Turn>() {
        @Override
        public Turn createFromParcel(Parcel in) {
            return new TurnP(in);

        }

        @Override
        public Turn[] newArray(int size) {
            return new Turn[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.deadPiece.name());
        dest.writeLong(super.duration.getSeconds());
        dest.writeString(super.fen);
        dest.writeString(super.from.getStringValue());
        dest.writeString(super.played.name());
        dest.writeValue(this.player);
        dest.writeString(super.to.getStringValue());
        dest.writeInt(super.turnNumber);
    }
}
