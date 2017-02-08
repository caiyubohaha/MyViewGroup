package com.cyb.cyb.cyb.myviewgroup;

import android.app.Activity;
import android.os.Bundle;

import com.cyb.cyb.cyb.view.VerticalLinearLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verticallinearlayout);

        VerticalLinearLayout vl = (VerticalLinearLayout) findViewById(R.id.vl);

    }
}
