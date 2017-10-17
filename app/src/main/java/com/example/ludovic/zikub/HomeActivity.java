package com.example.ludovic.zikub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button imageButton1 = (Button) findViewById(R.id.imageButton1);
        Button imageButton2 = (Button) findViewById(R.id.imageButton2);
        Button imageButton3 = (Button) findViewById(R.id.imageButton3);
        Button imageButton4 = (Button) findViewById(R.id.imageButton4);
        Button imageButton5 = (Button) findViewById(R.id.imageButton5);

        int width = (getResources().getDisplayMetrics().widthPixels) / 2;
        int height = (int) ((getResources().getDisplayMetrics().heightPixels) /  3.1);

        FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) imageButton1.getLayoutParams();
        params1.width = width;
        params1.height = height;

        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) imageButton2.getLayoutParams();
        params2.width = width;
        params2.height = height;

        FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) imageButton3.getLayoutParams();
        params3.width = width;
        params3.height = height;

        FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) imageButton4.getLayoutParams();
        params4.width = width;
        params4.height = height;

        FrameLayout.LayoutParams params5 = (FrameLayout.LayoutParams) imageButton5.getLayoutParams();
        params5.width = width;
        params5.height = height;

        imageButton1.setLayoutParams(params1);
        imageButton2.setLayoutParams(params2);
        imageButton3.setLayoutParams(params3);
        imageButton4.setLayoutParams(params4);
        imageButton5.setLayoutParams(params5);

    }

    @Override
    public void onBackPressed() {
    }
}
