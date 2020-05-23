package fr.aboucorp.variantchess.app.parcelables;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;

import fr.aboucorp.variantchess.entities.Match;

public class MatchP extends Match implements Parcelable {

    protected LinkedList<TurnP> turns = new LinkedList<>();

    public MatchP() {
    }

    protected MatchP(Parcel in) {
        in.readTypedList(this.turns,TurnP.CREATOR);
    }

    public static final Creator<MatchP> CREATOR = new Creator<MatchP>() {
        @Override
        public MatchP createFromParcel(Parcel in) {
            return new MatchP(in);
        }

        @Override
        public MatchP[] newArray(int size) {
            return new MatchP[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(turns);
    }
}
