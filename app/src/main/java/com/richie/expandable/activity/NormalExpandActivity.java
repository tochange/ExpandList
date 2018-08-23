package com.richie.expandable.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.richie.expandable.R;

public class NormalExpandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        BottomMenuEditFragment.addFragment(this, R.id.root);
    }
}
