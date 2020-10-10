package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.UserPresence;
import com.heroiclabs.nakama.api.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.GameRules;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchmakingListener;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Player;

public class MatchmakingFragment extends VariantChessFragment implements MatchmakingListener {
    /**
     * Widgets
     */
    private ProgressBar progress_bar;
    private Chronometer matchmaking_chrono;
    private Button btn_cancel;
    private Button btn_retry;
    private ImageView img_warning;
    /**
     * Nakama multiplayer session manager
     */
    private SessionManager sessionManager;
    /**
     * Nakama multiplayer session manager
     */
    private String matchmakingTicket;

    private GameRules gameRules;
    private ChessMatch chessMatch;
    private ChessUser chessUser;
    private UserViewModel userViewModel;

    @Override
    protected void bindViews() {
        this.progress_bar = this.getView().findViewById(R.id.progress_bar);
        this.matchmaking_chrono = this.getView().findViewById(R.id.matchmaking_chrono);
        this.btn_cancel = this.getView().findViewById(R.id.btn_cancel);
        this.btn_retry = this.getView().findViewById(R.id.btn_retry);
        this.img_warning = this.getView().findViewById(R.id.img_warning);
    }

    @Override
    protected void bindListeners() {
        this.btn_cancel.setOnClickListener(v -> cancelMatchMaking(matchmakingTicket));
        this.btn_retry.setOnClickListener(v -> launchMatchmaking());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.matchmaking_layout, container, false);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("gamerules");
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.gameRules = (GameRules) savedInstanceState.getSerializable("gamerules");
        }
        this.sessionManager = SessionManager.getInstance();
        this.sessionManager.setMatchmakingListener(this);
        this.bindViews();
        this.bindListeners();
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.userViewModel.getConnected().observe(this.getViewLifecycleOwner(), connected -> {
            chessUser = connected;
            this.launchMatchmaking();
        });
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        this.gameRules = (GameRules) args.getSerializable("gamerules");
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        Log.i("fr.aboucorp.variantchess", "onMatchmakerMatched " + matched);
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected void executeAsync() throws Exception {
                // TODO need entire refocto
                Match match = sessionManager.joinMatchByToken(matched.getToken());
                List<User> users = sessionManager.getUsersFromMatched(matched);
                Player white = new Player(users.get(0).getUsername(), ChessColor.WHITE, users.get(0).getId());
                Player black = new Player(users.get(1).getUsername(), ChessColor.BLACK, users.get(1).getId());
                chessMatch = new ChessMatch();
                chessMatch.setWhitePlayer(white);
                chessMatch.setBlackPlayer(black);
                chessMatch.setMatchId(match.getMatchId());
            }

            @Override
            protected void callbackOnUI() {
                matchmaking_chrono.stop();
                NavDirections action = MatchmakingFragmentDirections.actionMatchmakingFragmentToBoardActivity(true, chessMatch, chessUser, gameRules);
                Navigation.findNavController(getView()).navigate(action);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(getContext(), R.string.matchmaking_cannot_join_match, Toast.LENGTH_LONG).show();
                cancelMatchMaking(matchmakingTicket);
                matchmaking_chrono.stop();
                img_warning.setVisibility(View.VISIBLE);
            }
        };
        handler.start();
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        for (UserPresence userPresence : matchPresence.getJoins()
        ) {
            Log.i("fr.aboucorp.variantchess", String.format("User with username : %s, status : %s", userPresence.getUsername(), userPresence.getStatus()));
        }


    }

    private void launchMatchmaking() {
        this.matchmaking_chrono.start();
        this.btn_cancel.setVisibility(View.VISIBLE);
        this.btn_retry.setVisibility(View.GONE);
        this.img_warning.setVisibility(View.GONE);
        this.progress_bar.setVisibility(View.VISIBLE);
        try {
            this.sessionManager.launchMatchMaking(gameRules.name, this.matchmakingTicket);
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.e("fr.aboucorp.variantchess", "Error during matchmaking launch message" + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("fr.aboucorp.variantchess", "Error during matchmaking launch message" + e.getMessage());
        }
    }

    private void cancelMatchMaking(String ticket) {
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected void executeAsync() {
                sessionManager.cancelMatchMaking(ticket);
                btn_cancel.setEnabled(false);
            }

            @Override
            protected void callbackOnUI() {
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
}
