package fr.aboucorp.teamchess.app.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import fr.aboucorp.teamchess.R;

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