package com.example.ludovic.zikub;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        final Intent intent = new Intent(this, HomeActivity.class);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        TextView username = (TextView) findViewById(R.id.username);
        TextView pwd = (TextView) findViewById(R.id.password);

        if(!username.getText().toString().matches("") && !pwd.getText().toString().matches("")) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://54.37.68.6/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ZikubService service = retrofit.create(ZikubService.class);
            Call<Result> result = service.login(
                    username.getText().toString(),
                    pwd.getText().toString()
            );

            result.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    String result = new Gson().toJson(response.body().getSuccess());
                    if (response.isSuccessful()) {
                        // tasks available
                        if(result.equals("true")) {
                            int id_user = response.body().getIdUser();

                            SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("Storage.Users", Context.MODE_PRIVATE);

                            sharedPreferences
                                    .edit()
                                    .putInt("idUser", id_user)
                                    .apply();

                            startActivity(intent);
                        }
                        else {
                            TextView error = (TextView) findViewById(R.id.errorLogin);
                            error.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // error response, no access to resource?
                        Log.v("fail", response.toString());
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Log.d("Error", t.getMessage());
                }
            });
        }
    }

}

