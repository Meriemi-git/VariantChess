package fr.aboucorp.variantchess.app.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.listeners.GDXGestureListener;
import fr.aboucorp.variantchess.app.listeners.GDXInputAdapter;
import fr.aboucorp.variantchess.app.managers.MatchManager;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.managers.boards.ClassicBoardManager;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEvent;
import fr.aboucorp.variantchess.entities.rules.ClassicRuleSet;
import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class BoardFragment extends AndroidFragmentApplication  {

    private static Board3dManager board3dManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return InitLibGdx();
    }

    private View InitLibGdx(){
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View view = initializeForView(board3dManager, config);
        ((AndroidBoardFragment)getParentFragment()).startGame();
        return view;
    }

    public static void setBoard3dManager(Board3dManager board3dManager) {
        if(BoardFragment.board3dManager == null) {
            BoardFragment.board3dManager = board3dManager;
        }
    }
}
