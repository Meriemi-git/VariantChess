package fr.aboucorp.variantchess.app.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.GameRules;

public class GameRulesAdapter extends ArrayAdapter<GameRules> {
    private Context context;
    private List<GameRules> allGameRules;

    public GameRulesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }

    @Nullable
    @Override
    public GameRules getItem(int position) {
        return this.allGameRules.get(position);
    }

    @Override
    public int getCount() {
        if (allGameRules == null) {
            return 0;
        }
        return allGameRules.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // TODO add check if res exists
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.game_rule_item_layout, null);
        ImageView imgGamerules = convertView.findViewById(R.id.img_gamerules);
        int imageIdent = context.getResources().getIdentifier(this.allGameRules.get(position).icon, "drawable", context.getPackageName());
        imgGamerules.setImageDrawable(ContextCompat.getDrawable(context, imageIdent));
        TextView name = convertView.findViewById(R.id.txt_gamerules_name);
        name.setTextColor(ContextCompat.getColor(context, R.color.blue_400));
        name.setText(getContext().getResources().getIdentifier(this.allGameRules.get(position).name, "string", context.getPackageName()));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.game_rule_item_layout, null);
        ImageView imgGamerules = convertView.findViewById(R.id.img_gamerules);
        int imageIdent = context.getResources().getIdentifier(this.allGameRules.get(position).icon, "drawable", context.getPackageName());
        imgGamerules.setImageDrawable(ContextCompat.getDrawable(context, imageIdent));
        TextView name = convertView.findViewById(R.id.txt_gamerules_name);
        name.setTextColor(ContextCompat.getColor(context, R.color.blue_400));
        name.setText(getContext().getResources().getIdentifier(this.allGameRules.get(position).name, "string", context.getPackageName()));

        return convertView;
    }

    public void setGameRules(List<GameRules> allGameRules) {
        this.allGameRules = allGameRules;
        notifyDataSetChanged();
    }
}
