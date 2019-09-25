package de.codebucket.mkkm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                launch(new Intent(SplashActivity.this, MainActivity.class));
            }
        }, 500L);
    }

    private void launch(final Intent intent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(0, android.R.anim.fade_out);
                finish();
            }
        });
    }
}
