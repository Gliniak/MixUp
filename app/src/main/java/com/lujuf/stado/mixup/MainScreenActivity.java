package com.lujuf.stado.mixup;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Gliniak on 06.01.2018.
 */

public class MainScreenActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mainscreen);


    }
}
