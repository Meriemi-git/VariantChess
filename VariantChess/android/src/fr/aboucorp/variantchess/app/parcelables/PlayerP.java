package fr.aboucorp.variantchess.app.parcelables;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Player;

import android.os.Parcel;
import android.os.Parcelable;

public class PlayerP extends Player implements Parcelable {

    public PlayerP(String name,ChessColor color) {
        super(name,color);
    }

    protected PlayerP(Parcel in) {
        super.color = Enum.valueOf(ChessColor.class,in.readString());
        super.name = in.readString();
    }

    public static final Creator<PlayerP> CREATOR = new Creator<PlayerP>() {
        @Override
        public PlayerP createFromParcel(Parcel in) {
            return new PlayerP(in);
        }

        @Override
        public PlayerP[] newArray(int size) {
            return new PlayerP[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(super.color.name());
        dest.writeString(super.name);
    }
}
