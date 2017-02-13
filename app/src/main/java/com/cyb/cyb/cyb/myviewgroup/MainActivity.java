package com.cyb.cyb.cyb.myviewgroup;

import android.app.Activity;
import android.os.Bundle;

import com.cyb.cyb.cyb.view.MyVerticalLinearLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticallinearlayout);

        MyVerticalLinearLayout vl = (MyVerticalLinearLayout) findViewById(R.id.vl);
        /*vl.setOnPageChangeListener(new MyVerticalLinearLayout.OnPageChangeListener() {
            @Override
            public void onPageChange(int currentPage) {
                Toast.makeText(MainActivity.this, ""+currentPage, Toast.LENGTH_SHORT).show();
            }
        });*/

    }
}
