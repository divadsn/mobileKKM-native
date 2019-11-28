package de.codebucket.mkkm.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import de.codebucket.mkkm.R;

import static android.util.Patterns.EMAIL_ADDRESS;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";
    private static final int REGISTRATION_RESULT_CODE = 99;

    ScrollView mScrollView;
    TextInputEditText mEmailInput, mPasswordInput;
    MaterialButton mRegisterButton, mLoginButton;
    TextView mForgotPassword;
    View mLoadingLayout;

    private boolean isLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mScrollView = findViewById(R.id.login_scroll_view);
        mEmailInput = findViewById(R.id.login_email_text);
        mPasswordInput = findViewById(R.id.login_password_text);

        mForgotPassword = findViewById(R.id.login_forgot_password);
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        mRegisterButton = findViewById(R.id.login_create_account);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(LoginActivity.this, RegistrationActivity.class), REGISTRATION_RESULT_CODE);
            }
        });

        mLoginButton = findViewById(R.id.login_submit);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        mLoadingLayout = findViewById(R.id.login_loading_layout);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isLoading) {
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void performLogin() {
        if (isLoading) {
            return;
        }

        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        boolean validForm = true;

        if (TextUtils.isEmpty(email) || !EMAIL_ADDRESS.matcher(email).matches()) {
            showInputError(mEmailInput, getString(R.string.error_email_required));
            validForm = false;
        } else {
            showInputError(mEmailInput, null);
        }

        if (TextUtils.isEmpty(password) || password.length() <= 6) {
            showInputError(mPasswordInput, getString(R.string.error_password_required));
            validForm = false;
        } else {
            showInputError(mPasswordInput, null);
        }

        if (!validForm) {
            return;
        }

        showLoadingView(true);
    }

    private void showLoadingView(final boolean visible) {
        mRegisterButton.animate().alpha(visible ? 0.5f : 1.0f).setDuration(50);
        mLoginButton.animate().alpha(visible ? 0.5f : 1.0f).setDuration(50);

        if (visible) {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        mScrollView.animate().alpha(visible ? 0.2f : 1.0f).setDuration(300);
        mLoadingLayout.animate().alpha(visible ? 1.0f : 0.0f).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (!visible) {
                    mLoadingLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showInputError(View inputView, String error) {
        TextInputLayout parent = (TextInputLayout) inputView.getParent().getParent();
        parent.setError(error);

        if (error == null) {
            parent.setErrorEnabled(false);
        }
    }
}
