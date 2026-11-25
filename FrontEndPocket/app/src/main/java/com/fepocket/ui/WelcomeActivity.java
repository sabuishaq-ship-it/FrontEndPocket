package com.fepocket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fepocket.R;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvAppName, tvWelcome, tvTagline;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTagline = findViewById(R.id.tvTagline);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        startAnimations();
    }

    private void startAnimations() {
        AlphaAnimation alpha0 = new AlphaAnimation(0f, 1f);
        alpha0.setDuration(800);

        TranslateAnimation logoSlide = new TranslateAnimation(0, 0, 100, 0);
        logoSlide.setDuration(800);
        logoSlide.setStartOffset(300);
        AlphaAnimation logoAlpha = new AlphaAnimation(0f, 1f);
        logoAlpha.setDuration(800);
        logoAlpha.setStartOffset(300);

        ivLogo.startAnimation(logoSlide);
        ivLogo.startAnimation(logoAlpha);

        TranslateAnimation nameSlide = new TranslateAnimation(-200, 0, 0, 0);
        nameSlide.setDuration(800);
        nameSlide.setStartOffset(500);
        AlphaAnimation nameAlpha = new AlphaAnimation(0f, 1f);
        nameAlpha.setDuration(800);
        nameAlpha.setStartOffset(500);

        tvAppName.startAnimation(nameSlide);
        tvAppName.startAnimation(nameAlpha);

        TranslateAnimation welcomeSlide = new TranslateAnimation(200, 0, 0, 0);
        welcomeSlide.setDuration(800);
        welcomeSlide.setStartOffset(700);
        AlphaAnimation welcomeAlpha = new AlphaAnimation(0f, 1f);
        welcomeAlpha.setDuration(800);
        welcomeAlpha.setStartOffset(700);

        tvWelcome.startAnimation(welcomeSlide);
        tvWelcome.startAnimation(welcomeAlpha);

        AlphaAnimation taglineAlpha = new AlphaAnimation(0f, 1f);
        taglineAlpha.setDuration(800);
        taglineAlpha.setStartOffset(900);

        tvTagline.startAnimation(taglineAlpha);

        TranslateAnimation btnSlide = new TranslateAnimation(0, 0, 100, 0);
        btnSlide.setDuration(800);
        btnSlide.setStartOffset(1100);
        AlphaAnimation btnAlpha = new AlphaAnimation(0f, 1f);
        btnAlpha.setDuration(800);
        btnAlpha.setStartOffset(1100);

        btnLogin.startAnimation(btnSlide);
        btnLogin.startAnimation(btnAlpha);
        btnRegister.startAnimation(btnSlide);
        btnRegister.startAnimation(btnAlpha);
    }
}
