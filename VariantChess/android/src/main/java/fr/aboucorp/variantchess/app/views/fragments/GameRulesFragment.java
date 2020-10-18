package fr.aboucorp.variantchess.app.views.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.adapters.GameRulesAdapter;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.db.viewmodel.GameRulesViewModel;
import fr.aboucorp.variantchess.app.utils.ArgsKey;
import fr.aboucorp.variantchess.entities.ChessMatch;

@AndroidEntryPoint
public class GameRulesFragment extends VariantChessFragment implements AdapterView.OnItemSelectedListener {
    private Button btn_online;
    private Button btn_offline;
    private TextView txt_rule_description;
    private Spinner spinner_rules;
    private LinearLayout balance_layout;
    private LinearLayout difficulty_layout;
    private GameRulesViewModel gameRulesViewModel;
    private List<GameRules> allGameRules;
    private VariantUser variantUser;

    @Override
    protected void bindViews() {
        this.btn_online = this.getView().findViewById(R.id.btn_online);
        this.btn_offline = this.getView().findViewById(R.id.btn_offline);
        this.txt_rule_description = this.getView().findViewById(R.id.txt_rule_description);
        this.spinner_rules = this.getView().findViewById(R.id.spinner_rules);
        this.balance_layout = this.getView().findViewById(R.id.balance_layout);
        this.difficulty_layout = this.getView().findViewById(R.id.difficulty_layout);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_rules_layout, container, false);
        return view;
    }

    @Override
    protected void bindListeners() {
        GameRulesAdapter gameRulesAdapter = new GameRulesAdapter(getContext(), R.id.spinner_rules);
        this.spinner_rules.setAdapter(gameRulesAdapter);
        this.spinner_rules.setOnItemSelectedListener(this);
        this.gameRulesViewModel.getAllGameRules().observe(getViewLifecycleOwner(), allGameRules -> {
            this.allGameRules = allGameRules;
            gameRulesAdapter.setGameRules(allGameRules);
        });
        this.btn_online.setOnClickListener(v -> {
            if (this.variantUser != null) {
                GameRules selected = (GameRules) spinner_rules.getSelectedItem();
                NavDirections action = GameRulesFragmentDirections.actionGameRulesFragmentToMatchmakingFragment(selected);
                Navigation.findNavController(getView()).navigate(action);
            } else {
                this.showAuthentMessage();
            }
        });
        this.btn_offline.setOnClickListener(v -> {
            GameRules selected = (GameRules) spinner_rules.getSelectedItem();
            ChessMatch chessMatch = new ChessMatch();
            NavDirections action = GameRulesFragmentDirections.actionGameRulesFragmentToBoardFragment(false, selected, chessMatch);
            Navigation.findNavController(getView()).navigate(action);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.gameRulesViewModel = new GameRulesViewModel(getActivity().getApplication());
        this.bindViews();
        this.bindListeners();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        this.variantUser = (VariantUser) args.getSerializable(ArgsKey.VARIANT_USER);
    }

    private void showAuthentMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.warn_play_online_need_connection);
        builder.setPositiveButton(R.string.general_connect, (dialog, id) -> {
            NavDirections action = AuthentFragmentDirections.actionGlobalAuthentFragment();
            Navigation.findNavController(getView()).navigate(action);
        });
        builder.setNegativeButton(R.string.general_lbl_cancel, (dialog, id) -> {
            dialog.dismiss();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        GameRules gameRules = this.allGameRules.get(position);
        this.txt_rule_description.setText(getContext().getResources().getIdentifier(gameRules.description, "string", getActivity().getPackageName()));
        this.balance_layout.removeAllViews();
        this.difficulty_layout.removeAllViews();
        for (int i = 1; i <= 5; i++) {
            ImageView difficulty = new ImageView(getContext());
            if (gameRules.difficulty < i) {
                difficulty.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_chess_bq));
            } else {
                difficulty.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_chess_wq));
            }
            difficulty_layout.addView(difficulty);

            ImageView balance = new ImageView(getContext());
            if (gameRules.balance < i) {
                balance.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_chess_bq));
            } else {
                balance.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_chess_wq));
            }
            balance_layout.addView(balance);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

