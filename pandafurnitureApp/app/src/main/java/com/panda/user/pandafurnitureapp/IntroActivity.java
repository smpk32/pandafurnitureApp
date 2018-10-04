package com.panda.user.pandafurnitureapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.panda.user.pandafurnitureapp.item.MemberInfoItem;
import com.panda.user.pandafurnitureapp.lib.EtcLib;
import com.panda.user.pandafurnitureapp.lib.MyLog;
import com.panda.user.pandafurnitureapp.lib.RemoteLib;
import com.panda.user.pandafurnitureapp.lib.StringLib;
import com.panda.user.pandafurnitureapp.remote.RemoteService;
import com.panda.user.pandafurnitureapp.remote.ServiceGenerator;
import com.panda.user.pandafurnitureapp.lib.GeoLib;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        context = this;


        if (!RemoteLib.getInstance().isConnected(context)) {
            showNoService();
            return;
        }
    }

    /**
     * 일정 시간(1.2초) 이후에 startTask() 메소드를 호출해서
     * 서버에서 사용자 정보를 조회한다.
     */
    @Override
    protected void onStart() {
        super.onStart();

        Log.e("TEST","onStart process");

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTask();
            }
        }, 1200);
    }

    /**
     * 현재 인터넷에 접속할 수 없기 때문에 서비스를 사용할 수 없다는 메시지와
     * 화면 종료 버튼을 보여준다.
     */
    private void showNoService() {
        TextView messageText = (TextView) findViewById(R.id.networktext);
        messageText.setVisibility(View.VISIBLE);

        Button closeButton = (Button) findViewById(R.id.close);
        closeButton.setOnClickListener(this);

//        closeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        closeButton.setVisibility(View.VISIBLE);
    }

    /**
     * 현재 폰의 전화번호와 동일한 사용자 정보를 조회할 수 있도록
     * selectMemberInfo() 메소드를 호출한다.
     * 그리고 setLastKnownLocation() 메소드를 호출해서 현재 위치 정보를 설정한다.
     */
    public void startTask() {
        String phone = EtcLib.getInstance().getPhoneNumber(this);

        Log.e("TEST",phone);

        selectMemberInfo(phone);
        GeoLib.getInstance().setLastKnownLocation(this);
    }

    /**
     * 리트로핏을 활용해서 서버로부터 사용자 정보를 조회한다.
     * 사용자 정보를 조회했다면 setMemberInfoItem() 메소드를 호출하고
     * 그렇지 않다면 goProfileActivity() 메소드를 호출한다.
     *
     * @param phone 폰의 전화번호
     */
    public void selectMemberInfo(String phone) {

        Log.e("TEST", "selectMemberInfo");

        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<MemberInfoItem> call = remoteService.selectMemberInfo(phone);
        call.enqueue(new Callback<MemberInfoItem>() {
            @Override
            public void onResponse(Call<MemberInfoItem> call, Response<MemberInfoItem> response) {
                MemberInfoItem item = response.body();

                if (response.isSuccessful() && !StringLib.getInstance().isBlank(item.name)) {
                    MyLog.d(TAG, "success " + response.body().toString());
                    setMemberInfoItem(item);
                } else {
                    MyLog.d(TAG, "not success");
                    goProfileActivity(item);
                }
            }

            @Override
            public void onFailure(Call<MemberInfoItem> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity1");
                MyLog.d(TAG, "에러여기"+ t.toString());
            }
        });
    }

    /**
     * 전달받은 MemberInfoItem을 Application 객체에 저장한다.
     * 그리고 startMain() 메소드를 호출한다.
     *
     * @param item 사용자 정보
     */
    private void setMemberInfoItem(MemberInfoItem item) {
        ((MyApp) getApplicationContext()).setMemberInfoItem(item);

        startMain();
    }

    /**
     * MainActivity를 실행하고 현재 액티비티를 종료한다.
     */
    public void startMain() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        Log.d(" startmain", "startmain");

        finish();
    }

    /**
     * 사용자 정보를 조회하지 못했다면 insertMemberPhone() 메소드를 통해
     * 전화번호를 서버에 저장하고 MainActivity를 실행한 후 ProfileActivity를 실행한다.
     * 그리고 현재 액티비티를 종료한다.
     *
     * @param item 사용자 정보
     */
    private void goProfileActivity(MemberInfoItem item) {
        if (item == null || item.seq <= 0) {
            insertMemberPhone();
        }

        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        startActivity(intent);
        Log.d(" start", "start");
        Intent intent2 = new Intent(this, ProfileActivity.class);
        startActivity(intent2);

        finish();
    }

    /**
     * 폰의 전화번호를 서버에 저장한다.
     */
    private void insertMemberPhone() {
        String phone = EtcLib.getInstance().getPhoneNumber(context);
        RemoteService remoteService =
                ServiceGenerator.createService(RemoteService.class);

        Call<String> call = remoteService.insertMemberPhone(phone);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    MyLog.d(TAG, "success insert id " + response.body().toString());
                } else {
                    int statusCode = response.code();

                    ResponseBody errorBody = response.errorBody();

                    MyLog.d(TAG, "fail " + statusCode + errorBody.toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity2");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close:
                // 닫기버튼
                finish();
                break;

        }
    }
}
