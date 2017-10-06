package com.example.ludovic.zikub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    /** Called when the user taps the Sign up button */
    public void signup(View view) throws IOException, JSONException {

        final Intent intent = new Intent(this, HomeActivity.class);

        TextView username = (TextView) findViewById(R.id.username);
        TextView mail = (TextView) findViewById(R.id.email);
        TextView pwd = (TextView) findViewById(R.id.password);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.10.10/")
                .build();

        ZikubService service = retrofit.create(ZikubService.class);
        Call<ResponseBody> result = service.signUp(
                mail.getText().toString(),
                username.getText().toString(),
                pwd.getText().toString()
        );

        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // tasks available
                    startActivity(intent);
                } else {
                    // error response, no access to resource?
                    Log.v("fail", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });
    }
}
