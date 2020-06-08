package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.api.User;

import java.util.ArrayList;
import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.adapters.GameModeAdapter;
import fr.aboucorp.variantchess.app.db.entities.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.MatchListener;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.app.views.activities.BoardActivity;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.GameMode;
import fr.aboucorp.variantchess.entities.Player;

public class GameModeFragment extends VariantChessFragment implements MatchListener {
    private Spinner new_game_spinner;
    private Button gamemode_btn_launch;
    private Button gamemode_btn_cancel;
    private ProgressBar gamemode_progress_circle;
    private SessionManager sessionManager;
    private boolean isOnline;
    private Match match;
    private String matchmakingTicket;
    private ChessMatch chessMatch;

    @Override
    protected void bindViews() {
        this.new_game_spinner = this.getView().findViewById(R.id.new_game_spinner);
        this.gamemode_btn_launch = this.getView().findViewById(R.id.new_game_btn_launch);
        this.gamemode_btn_cancel = this.getView().findViewById(R.id.gamemode_btn_cancel);
        this.gamemode_progress_circle = this.getView().findViewById(R.id.new_game_progress_bar);
    }

    @Override
    protected void bindListeners() {
        this.gamemode_btn_launch.setOnClickListener(v -> this.launchGame());
    }

    @Override
    public void onError(Error error) {
        Log.e("fr.aboucorp.variantchess", "onError " + error.getMessage());
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        Log.e("fr.aboucorp.variantchess", "onMatchmakerMatched " + matched);
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected void executeAsync() throws Exception {
                Match match = GameModeFragment.this.sessionManager.joinMatchByToken(matched.getToken());
                List<User> users = GameModeFragment.this.sessionManager.getUsersFromMatched(matched);
                Player white = new Player(users.get(0).getDisplayName(), ChessColor.WHITE, users.get(0).getId());
                Player black = new Player(users.get(1).getDisplayName(), ChessColor.BLACK, users.get(1).getId());
                GameModeFragment.this.chessMatch = new ChessMatch();
                GameModeFragment.this.chessMatch.setWhitePlayer(white);
                GameModeFragment.this.chessMatch.setBlackPlayer(black);
                GameModeFragment.this.chessMatch.setMatchId(match.getMatchId());
            }

            @Override
            protected void callbackOnUI() {
                Bundle args = new Bundle();
                args.putSerializable("chessMatch", GameModeFragment.this.chessMatch);
                Intent intent = new Intent(GameModeFragment.this.getActivity(), BoardActivity.class);
                intent.putExtras(args);
                GameModeFragment.this.startActivity(intent);
            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                Toast.makeText(GameModeFragment.this.getContext(), R.string.matchmaking_cannot_join_match, Toast.LENGTH_LONG).show();
                GameModeFragment.this.cancelMatchMaking(GameModeFragment.this.matchmakingTicket);
            }
        };
        handler.start();
    }

    @Override
    public void onMatchData(MatchData data) {
        Log.e("fr.aboucorp.variantchess", "onMatchData : " + data.getOpCode());
    }

    @Override
    public void setArguments(@androidx.annotation.Nullable Bundle args) {
        super.setArguments(args);
        this.isOnline = args != null && args.getBoolean(ONLINE_ARGS_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_mode_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance(this.getActivity());
        UserViewModel userViewModel = new ViewModelProvider(this.getActivity()).get(UserViewModel.class);
        List<GameMode> modes = new ArrayList<>();
        modes.add(new GameMode("Classic", "Normal gamemode with classic rules and bla and bla and bla and many test."));
        modes.add(new GameMode("Random", "Normal gamemode with classic rules but pieces and randmly set at the start of the game"));
        GameModeAdapter adpater = new GameModeAdapter(this.getActivity(), modes);
        this.new_game_spinner.setAdapter(adpater);
    }

    private void launchGame() {
        if (this.isOnline) {
            GameModeFragment.this.gamemode_progress_circle.setVisibility(View.VISIBLE);
            AsyncHandler handler = new AsyncHandler() {
                @Override
                protected void executeAsync() throws Exception {
                    GameModeFragment.this.matchmakingTicket = GameModeFragment.this.sessionManager.launchMatchMaking((GameMode) GameModeFragment.this.new_game_spinner.getSelectedItem(), GameModeFragment.this);
                }

                @Override
                protected void callbackOnUI() {
                    GameModeFragment.this.gamemode_btn_cancel.setVisibility(View.VISIBLE);
                    GameModeFragment.this.gamemode_btn_launch.setVisibility(View.INVISIBLE);
                    GameModeFragment.this.gamemode_btn_cancel.setOnClickListener((v) -> GameModeFragment.this.cancelMatchMaking(GameModeFragment.this.matchmakingTicket));
                }

                @Override
                protected void error(Exception ex) {
                    super.error(ex);
                    Toast.makeText(GameModeFragment.this.getContext(), R.string.matchmaking_failed, Toast.LENGTH_LONG).show();
                }
            };
            handler.start();
        } else {
            ChessMatch chessMatch = new ChessMatch();
            chessMatch.setBlackPlayer(new Player("BlackPlayer", ChessColor.BLACK, null));
            chessMatch.setWhitePlayer(new Player("WhitePlayer", ChessColor.WHITE, null));
            Bundle args = new Bundle();
            args.putSerializable("chessMatch", chessMatch);
            Intent intent = new Intent(this.getActivity(), BoardActivity.class);
            intent.putExtras(args);
            this.startActivity(intent);
        }
    }

    private void cancelMatchMaking(String ticket) {
        AsyncHandler handler = new AsyncHandler() {
            @Override
            protected void executeAsync() {
                GameModeFragment.this.sessionManager.cancelMatchMaking(ticket);
            }

            @Override
            protected void callbackOnUI() {
                Toast.makeText(GameModeFragment.this.getContext(), R.string.matchmaking_cancelled, Toast.LENGTH_LONG).show();
                GameModeFragment.this.matchmakingTicket = null;
                GameModeFragment.this.gamemode_btn_launch.setVisibility(View.VISIBLE);
                GameModeFragment.this.gamemode_btn_cancel.setVisibility(View.INVISIBLE);
                GameModeFragment.this.gamemode_progress_circle.setVisibility(View.INVISIBLE);
            }
        };
        handler.start();
    }


}
