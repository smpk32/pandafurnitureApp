package com.panda.user.pandafurnitureapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.panda.user.pandafurnitureapp.item.FoodInfoItem;
import com.panda.user.pandafurnitureapp.item.GeoItem;
import com.panda.user.pandafurnitureapp.lib.GoLib;
import com.panda.user.pandafurnitureapp.lib.MyLog;

public class PandaFurnitureRegisterActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    public static FoodInfoItem currentItem = null;

    Context context;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pandafurniture_register);

        context = this;

        int memberSeq = ((MyApp)getApplication()).getMemberSeq();

        //BestFoodRegisterLocationFragment로 넘길 기본적인 정보를 저장한다.
        FoodInfoItem infoItem = new FoodInfoItem();
        infoItem.memberSeq = memberSeq;
//        infoItem.latitude = GeoItem.getKnownLocation().latitude;
//        infoItem.longitude = GeoItem.getKnownLocation().longitude;

        MyLog.d(TAG, "infoItem " + infoItem.toString());

        setToolbar();

        //BestFoodRegisterLocationFragment를 화면에 보여준다.
//        GoLib.getInstance().goFragment(getSupportFragmentManager(),
//                R.id.content_main, BestFoodRegisterLocationFragment.newInstance(infoItem));
    }


    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.bestfood_register);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.action_close:
                finish();
                break;
        }

        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


}
