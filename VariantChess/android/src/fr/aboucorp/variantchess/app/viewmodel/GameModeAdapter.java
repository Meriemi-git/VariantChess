package fr.aboucorp.variantchess.app.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.entities.GameMode;

public class GameModeAdapter extends ArrayAdapter implements SpinnerAdapter {
    private Context mContext;
    private List<GameMode> moviesList;

    public GameModeAdapter(Context context, List<GameMode> list) {
        super(context, 0 , list);
        mContext = context;
        moviesList = list;
    }

    @Override
    public View getView(int position, View convertView,  ViewGroup parent) {
        View view = convertView;
        if(view == null)
            view = LayoutInflater.from(mContext).inflate(R.layout.game_mode_list_item,parent,false);

        GameMode currentGameMode = moviesList.get(position);

        TextView name = view.findViewById(R.id.gamemode_lbl_name);
        name.setText(currentGameMode.getName());

        TextView desc = view.findViewById(R.id.gamemode_lbl_description);
        desc.setText(currentGameMode.getDescription());

        return view;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.game_mode_list_item,parent,false);
        GameMode currentGameMode = moviesList.get(position);

        TextView name = view.findViewById(R.id.gamemode_lbl_name);
        name.setText(currentGameMode.getName());

        TextView desc = view.findViewById(R.id.gamemode_lbl_description);
        desc.setText(currentGameMode.getDescription());
        return view;
    }

    @Override
    public int getCount() {
        return moviesList.size();
    }

    @Override
    public Object getItem(int position) {
        return moviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

