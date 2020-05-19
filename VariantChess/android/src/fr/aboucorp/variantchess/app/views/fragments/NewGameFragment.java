package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.viewmodel.GameModeAdapter;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.GameMode;

public class NewGameFragment extends VariantChessFragment implements AdapterView.OnItemSelectedListener {
    private Spinner new_game_spinner;
    private Button new_game_btn_launch;
    private ProgressBar new_game_progress_bar;
    private RadioButton new_game_rdb_online;
    private RadioButton new_game_rdb_offline;


    private SessionManager sessionManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance((MainActivity) getActivity());
        this.new_game_spinner.setOnItemSelectedListener(this);
        List<GameMode> modes = new ArrayList<>();
        modes.add(new GameMode("Classic","Normal gamemode with classic rules and bla and bla and bla and many test."));
        modes.add(new GameMode("Random","Normal gamemode with classic rules but pieces and randmly set at the start of the game"));
        GameModeAdapter adpater = new GameModeAdapter(getActivity(),modes);
        this.new_game_spinner.setAdapter(adpater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_game_layout, container, false);
        return view;
    }
    @Override
    protected void bindViews() {
        this.new_game_spinner = getView().findViewById(R.id.new_game_spinner);
        this.new_game_rdb_online = getView().findViewById(R.id.new_game_rdb_online);
        this.new_game_rdb_offline = getView().findViewById(R.id.new_game_rdb_offline);
        this.new_game_btn_launch = getView().findViewById(R.id.new_game_btn_launch);
        this.new_game_progress_bar = getView().findViewById(R.id.new_game_progress_bar);
    }

    @Override
    protected void bindListeners() {
        new_game_btn_launch.setOnClickListener( v -> launchGame());
    }

    private void launchGame() {
        if(new_game_rdb_online.isChecked()){
/*            try {
                this.sessionManager.launchMatchMaking((GameMode)this.new_game_spinner.getSelectedItem(),this);
                this.new_game_progress_bar.setVisibility(View.VISIBLE);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(getActivity(),getActivity().getString(R.string.matchmaking_failed), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }*/
        }else if(new_game_rdb_offline.isChecked()){
            ((VariantChessActivity)getActivity()).setFragment(new AndroidBoardFragment(),"board");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
