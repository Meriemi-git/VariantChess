package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.api.User;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.db.viewmodel.VariantUserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchmakingListener;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;

import static fr.aboucorp.variantchess.app.utils.ArgsKey.GAME_RULES;

public class MatchmakingFragment extends VariantChessFragment implements MatchmakingListener {
    /**
     * Widgets
     */
    private ProgressBar progress_bar;
    private Chronometer matchmaking_chrono;
    private Button btn_cancel;
    private Button btn_retry;
    private ImageView img_warning;
    private TextView txt_status;
    /**
     * Nakama multiplayer session manager
     */
    @Inject
    public NakamaManager nakamaManager;
    /**
     * Nakama multiplayer session manager
     */
    private String matchmakingTicket;

    private GameRules gameRules;
    private VariantUser variantUser;
    private VariantUserViewModel variantUserViewModel;

    @Override
    protected void bindViews() {
        this.progress_bar = this.getView().findViewById(R.id.progress_bar);
        this.matchmaking_chrono = this.getView().findViewById(R.id.matchmaking_chrono);
        this.btn_cancel = this.getView().findViewById(R.id.btn_cancel);
        this.btn_retry = this.getView().findViewById(R.id.btn_retry);
        this.img_warning = this.getView().findViewById(R.id.img_warning);
        this.txt_status = this.getView().findViewById(R.id.txt_status);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matchmaking_layout, container, false);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("game_rules");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("game_rules");
        }
        this.nakamaManager.setMatchmakingListener(this);
        this.bindViews();
        this.bindListeners();
        this.variantUserViewModel = new ViewModelProvider(this).get(VariantUserViewModel.class);
        this.variantUserViewModel.getConnected().observe(this.getViewLifecycleOwner(), connected -> {
            variantUser = connected;
            this.launchMatchmaking();
        });
    }

    @Override
    protected void bindListeners() {
        this.btn_cancel.setOnClickListener(v -> cancelMatchMaking(matchmakingTicket));
        this.btn_retry.setOnClickListener(v -> launchMatchmaking());
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        Log.i("fr.aboucorp.variantchess", String.format("Match found ! id : %s", matched.getMatchId()));
        matchmaking_chrono.stop();
        AsyncHandler asyncHandler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                List<User> users = nakamaManager.getUsersFromMatched(matched);
                return users;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                super.callbackOnUI(arg);
                NavDirections action = MatchmakingFragmentDirections.actionMatchmakingFragmentToBoardFragment(true, matched.getMatchId(), variantUser, gameRules);
                Navigation.findNavController(getView()).navigate(action);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Log.e("fr.aboucorp.variantchess", String.format("Cannot retrieve players from matched"));
            }
        };
        asyncHandler.start();
    }

    private void launchMatchmaking() {
        this.matchmaking_chrono.start();
        this.btn_cancel.setVisibility(View.VISIBLE);
        this.btn_retry.setVisibility(View.GONE);
        this.img_warning.setVisibility(View.GONE);
        this.progress_bar.setVisibility(View.VISIBLE);
        this.txt_status.setText(R.string.matchmaking_in_progress);

        AsyncHandler asyncHandler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                matchmakingTicket = nakamaManager.launchMatchMaking(gameRules.name, matchmakingTicket);
                return null;
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_failed, Toast.LENGTH_LONG).show();
            }
        };
        asyncHandler.start();
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        this.gameRules = (GameRules) args.getSerializable(GAME_RULES);
    }



    private void cancelMatchMaking(String ticket) {
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected Object executeAsync() {
                nakamaManager.cancelMatchMaking(ticket);
                btn_cancel.setEnabled(false);
                return null;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                Toast.makeText(getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                matchmakingTicket = null;
                matchmaking_chrono.stop();
                btn_cancel.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                matchmakingTicket = null;
                matchmaking_chrono.stop();
                btn_cancel.setVisibility(View.GONE);
                progress_bar.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);
            }
        };
        handler.start();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
