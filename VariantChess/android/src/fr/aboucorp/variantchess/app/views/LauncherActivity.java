package fr.aboucorp.variantchess.app.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import fr.aboucorp.variantchess.R;

public class LauncherActivity extends AbstractActivity {

    public Button btn_create;
    public Button btn_connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        this.bindViews();
        this.bindListeners();
    }
    @Override
    public void bindViews() {
        this.btn_create = findViewById(R.id.btn_create);
        this.btn_connect =  findViewById(R.id.btn_connect);
    }
    @Override
    public void bindListeners() {
        this.btn_create.setOnClickListener(v -> {
            Intent intent = new Intent(LauncherActivity.this, ConnexionActivity.class);
            startActivity(intent);
        });
        this.btn_connect.setOnClickListener(v -> {
            Intent intent = new Intent(LauncherActivity.this, ConnexionActivity.class);
            startActivity(intent);
        });
    }
}