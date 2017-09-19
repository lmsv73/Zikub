package com.example.ludovic.zikub;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

    }

    @Override
    public void onClick(View v) {
        setContentView(R.layout.activity_login);
    }


}

