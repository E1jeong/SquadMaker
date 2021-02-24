package com.example.squadmaker.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.squadmaker.PreferenceManager;
import com.example.squadmaker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    PreferenceManager PM = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewCreate();
    }

    //메인액티비티에서 생성된 뷰를 관리하는 메소드
    private void viewCreate() {

        //앱 이름의 오른쪽에 만들어질 이미지뷰 - 이미지뷰에 클릭리스너를 붙여서 settings액티비티로 이동
        ImageView ivAppSettings = findViewById(R.id.iv_app_settings);
        //메인메뉴의 첫번째 - 텍스트뷰로 만들어서 클릭리스너를 붙여서 formation액티비티로 이동
        TextView tvMenuFormation = findViewById(R.id.tv_menu_formation);
        //메인메뉴의 두번째 - 텍스트뷰에 클릭리스너를 붙여서 scoreboard액티비티로 이동함
        TextView tvMenuScoreBoard = findViewById(R.id.tv_menu_score_board);

        /*
         테스트로만든 화면
         */
        TextView tvTest = findViewById(R.id.tv_test);


        //Lottie 애니메이션뷰 - 바닥에서 공이 탕탕 튀는 애니메이션
        LottieAnimationView lottieBallJumping = findViewById(R.id.lottie_jumping_ball);
        lottieBallJumping.playAnimation();
        lottieBallJumping.setRepeatCount(LottieDrawable.INFINITE);

        //Lottie 애니메이션뷰 - 메인 애니메이션 (경기장에 공이 왔다갔다 하는 애니메이션)
        //애니메이션이 종료되면 없어지게 해줬음
//        final LottieAnimationView lottieMainAnimation = findViewById(R.id.lottie_main_animation);
//        lottieMainAnimation.playAnimation();
//        //lottieMainAnimation.setProgress();
//        lottieMainAnimation.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                lottieMainAnimation.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//
//        });

        //ivAppSettings 해당 이미지뷰를 통해 다음 액티비티(설정화면)로 화면전환
        ivAppSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);

            }
        });

        //tvMenuFormation 해당 텍스트뷰를 통해 다음 액티비티(포메이션 설정하는 액티비티)로 화면전환
        tvMenuFormation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, FormationActivity.class);
                startActivity(intent);

            }
        });

        //tvMenuScoreBoard 해당 텍스트뷰를 통해 다음 액티비티(타이머, 점수 설정하는 액티비티)로 화면전환
        tvMenuScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ScoreBoardActivity.class);
                startActivity(intent);

            }
        });

        //tvMenuScoreBoard 해당 텍스트뷰를 통해 다음 액티비티(타이머, 점수 설정하는 액티비티)로 화면전환
        tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);

            }
        });
    }
}
