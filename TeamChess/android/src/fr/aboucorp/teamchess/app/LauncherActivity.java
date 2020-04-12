package fr.aboucorp.teamchess.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.badlogic.gdx.backends.android.AndroidApplication;

import fr.aboucorp.teamchess.R;
import fr.aboucorp.teamchess.app.views.activities.BoardActivity;

public class LauncherActivity extends Activity {

    public Button launcherButton;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        this.bindViews();
        this.bindListeners();
        this.launcherButton = findViewById(R.id.btn_launch_game);
    }

    public void bindViews() {
        this.launcherButton = findViewById(R.id.btn_launch_game);
    }

    public void bindListeners() {
        launcherButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(LauncherActivity.this, BoardActivity.class);
                startActivity(intent);
            }
        });
    }
}