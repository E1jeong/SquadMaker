package com.example.squadmaker.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.squadmaker.AlarmReceiver;
import com.example.squadmaker.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ScoreBoardActivity extends AppCompatActivity {

    String TAG = "ScoreBoardActivity";

    //타이머에 적용하는 스레드에서 run()메소드 - while 루프에서 사용되는 변수
    boolean isRunning = true;
    //타이머 스레드에서 메시지 객체로 넘겨주는 인자
    //이 인자를 통해서 핸들러에서 UI에 나타내는 분과 초를 계산함
    int i = 0;
    //액티비티에 경기시작 이라는 버튼이 있는데 해당버튼을 누를때마다 기능을 바꿔주기 위해서 사용하는 변수
    int btnCount = 0;
    //시간설정이라는 버튼을 누르면 다이얼로그에 내가 원하는 시간을 분단위로 입력하는데 사용됨. 아래에 int min 변수와 비교해서 스레드를 컨트롤함.
    int minute;
    //핸들러에서 UI에 분과 초를 나타낼때 사용하는 변수
    int min, sec;

    //공공데이터포털에서 미세먼지에 대한 정보를 불러올때 api에 사용할 url
    //영등포구의 최신 미세먼지 정보만 불러오게 요청해논 상태
    //그 값을 json으로 받아와서 필요한 정보만 UI에 보여줌
    //해당 기능은 loadParticulateMatter()메소드 안에 스레드로 작동
    String urlStr = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=영등포구&dataTerm=daily&pageNo=1&numOfRows=1&ServiceKey=TYFq6bU2bObmWx9h6YsfWvFE7uRl7tl1idfEhPRBn%2FuRcb5G8AwvScZngqGoQ8ALu%2Fq9AKjEKpP%2B%2FqXdFqLRig%3D%3D&ver=1.0&_returnType=json";

    TextView tvClock,tvMatchStart,tvTimeSettings,tvPM10,tvPM25,tvMeasureTime,tvHomeScore, tvAwayScore;

    ImageView ivHomeScoreUp, ivHomeScoreDown, ivAwayScoreUp, ivAwayScoreDown;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private Thread timeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        tvClock = findViewById(R.id.tv_clock);
        tvMatchStart = findViewById(R.id.tv_match_start);
        tvTimeSettings = findViewById(R.id.tv_time_settings);
        tvPM10 = findViewById(R.id.tv_pm10);
        tvPM25 = findViewById(R.id.tv_pm25);
        tvMeasureTime = findViewById(R.id.tv_measure_time);

        tvHomeScore = findViewById(R.id.tv_home_score);
        tvAwayScore = findViewById(R.id.tv_away_score);

        ivHomeScoreUp = findViewById(R.id.iv_home_score_up);
        ivHomeScoreDown = findViewById(R.id.iv_home_score_down);
        ivAwayScoreUp = findViewById(R.id.iv_away_score_up);
        ivAwayScoreDown = findViewById(R.id.iv_away_score_down);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        viewCreate();

        loadParticulateMatter();
    }

    private void viewCreate() {

        //tvMatchStart 해당 텍스트뷰에 클릭리스너를 설정, 누르면 playtime에서 시간이 흐르게 함
        //해당 텍스트를 누를때마다 btnCount라는 변수를 통해 텍스트를 바꿔주고, 스레드도 일시정지 시켜줌
        tvMatchStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //서비스를 통해 알람을 울리게 했음
                alarmStart();

                timeThread = new Thread(new TimeThread());
                timeThread.start();

                Thread.State state = timeThread.getState();
                Log.d(TAG, "!!@@ 스레드 상태확인 경기시작 버튼 누르자마자 : " + state);

                //btnCount == 0 일때, 스레드 계속 진행
                if(btnCount == 0)
                {
                    isRunning = true;
                    tvMatchStart.setText("일시정지");
                    btnCount ++;

                    Log.d(TAG, "!!@@ 스레드 상태 : " + state);
                }

                //btnCount == 1일때, 스레드 멈춤
                else
                {
                    isRunning = false;
                    tvMatchStart.setText("다시시작");
                    btnCount --;

                    Log.d(TAG, "!!@@ 스레드 상태 : " + state);
                }
            }
        });

        //tvTimeSettings 텍스트뷰(시간설정 버튼)를 클릭시 다이얼로그를 띄워줌
        //다이얼로그에서 원하는 시간을 분단위로 입력할수 있음
        tvTimeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_time_settings, null, false);
                builder.setView(view);

                final EditText etTime = view.findViewById(R.id.et_time);
                final Button btnOK = view.findViewById(R.id.btn_ok);

                final AlertDialog dialog = builder.create();

                btnOK.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        //다이얼로그에 입력한 값을 정수형태로 받아서 저장
                        //이 값으로 스레드 컨트롤
                        minute = Integer.parseInt(etTime.getText().toString());

                        dialog.dismiss();

                        //Log.d(TAG, "!!@@ 스레드 상태체크 다이얼로그에서 시간지정할때 : " + state);
                    }
                });

                dialog.show();
            }
        });

        //Home팀의 점수를 1점씩 올리게 해주는 버튼
        ivHomeScoreUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tempInt = Integer.parseInt(tvHomeScore.getText().toString());

                tempInt = tempInt + 1;

                String tempStr = Integer.toString(tempInt);

                tvHomeScore.setText(tempStr);

            }
        });

        //Home팀의 점수를 1점씩 내리는 버튼
        ivHomeScoreDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tempInt = Integer.parseInt(tvHomeScore.getText().toString());

                //스포츠경기가 0점 이하로 내려가진 않기때문에 0에서 멈추게 해줬음
                if(tempInt <= 0)
                {
                    tvHomeScore.setText("0");
                }
                else
                {
                    tempInt = tempInt - 1;

                    String tempStr = Integer.toString(tempInt);

                    tvHomeScore.setText(tempStr);
                }
            }
        });

        //Away팀의 점수를 1씩 올리는 버튼
        ivAwayScoreUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tempInt = Integer.parseInt(tvAwayScore.getText().toString());

                tempInt = tempInt + 1;

                String tempStr = Integer.toString(tempInt);

                tvAwayScore.setText(tempStr);

            }
        });

        //Away팀의 점수를 1씩 내리는 버튼
        ivAwayScoreDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int tempInt = Integer.parseInt(tvAwayScore.getText().toString());

                if(tempInt <= 0)
                {
                    tvAwayScore.setText("0");
                }
                else
                {
                    tempInt = tempInt - 1;

                    String tempStr = Integer.toString(tempInt);

                    tvAwayScore.setText(tempStr);
                }

            }
        });



    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            //타이머에 진행되는 시간을 실시간의 비율로 나타내주는 수식 (초와 분을 계산)
            sec = msg.arg1 % 60;
            min = msg.arg1 / 60;

            //msg.arg1 변수를 100으로 나눠주는거는 mSec의 값을 띄울때 사용
            //int mSec = msg.arg1 % 100;
            //int hour = (msg.arg1 / 100) / 360;
            //String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);

            //시간설정에서 입력한 시간과 타이머에서 나타나는 시간이 같을때 타이머 시간과 버튼의 텍스트를 다시 세팅
            //그리고 종료됨을 알려주는 다이얼로그를 생성했음. 그 이유는 시간과 텍스트만 초기화 해주면 타이머가 종료된지 알아차리기가 쉽지않음
            //minute != 0 이라는 조건을 걸어주지 않으면 해당 액티비티에 들어가자마자 다이얼로그가 한번 뜨게됨. 그부분을 제외하기 위한 조건
            if(minute == sec && minute != 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ScoreBoardActivity.this);
                builder.setMessage("경기시간이 종료되었습니다");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //다이얼로그의 확인버튼을 누르면 스레드와 서비스를 모두 멈추고 핸들러를 메시지의 i 인자를 초기화 시킴
                        i = 0;
                        btnCount = 0;
                        tvClock.setText("00:00");
                        tvMatchStart.setText("경기시작");

                        //interrupt와 alarmsStop의 위치를 바꾸면 둘다 제대로 멈추지 않았었는데 위치를 바꿔주니 잘 작동함
                        alarmStop();

                        timeThread.interrupt();
                        Thread.State state = timeThread.getState();
                        Log.d(TAG, "!!@@ 스레드 상태확인 핸들러에서 다이얼로그 OK버튼 누를때 : " + state);
                    }
                });
                builder.show();
            }

            //계산한 초와 분을 UI에 나타내주는 부분
            String result = String.format("%02d:%02d", min, sec);
            tvClock.setText(result);
        }
    };


    //1초마다 핸들러에 인자를 1씩 더해서 넘겨주는 스레드
    //그 인자를 통해서 타이머에 입력되는 분, 초가 생성됨
    public class TimeThread implements Runnable {

        public void run() {

            while(isRunning) {

                //interrupt시키기 위해 사용했음
                try
                {
                    //스레드 종료 조건 - 내가 설정한 시간과 타이머의 시간이 같을때 스레드 종료
                    if(minute == sec)
                    {
                        break;
                    }

                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    Thread.State state = timeThread.getState();
                    Log.d(TAG, "!!@@ 스레드 상태확인 스레드의 run() : " + state);

                    //원래 mSec까지 나타내려면 10으로 설정하고 위에 핸들러 UI부분을 바꿔줘야함
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //공공데이터포털에서 미세먼지의 데이터 가져오는 메소드
    //이부분은 되는 코드를 찾아서 가져왔음
    // json을 원하는 부분만 나타내게 해줬음
    void loadParticulateMatter() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                final StringBuffer sb = new StringBuffer();

                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();// 접속

                    if (conn != null)
                    {
                        conn.setConnectTimeout(2000);
                        conn.setUseCaches(false);

                        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                        {   //데이터 읽기
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                            while(true)
                            {
                                String line = br.readLine();
                                Log.d(TAG, "!!@@ 데이터 추출 과정중 String line : " + line);
                                if (line == null) break;
                                sb.append(line);
                            }
                            br.close(); // 스트림 해제
                        }
                        conn.disconnect(); // 연결 끊기
                    }

                    //받아온 json데이터에서 미세먼지와 초미세먼지의 등급을 추출하는 부분
                    Log.d(TAG, sb.toString());
                    JSONObject temp = new JSONObject(sb.toString());
                    Log.d(TAG, "!!@@ jobject 확인 : " + temp);

                    String tempStr = temp.getString("list");
                    Log.d(TAG,"!!@@ tempStr 확인 : " + tempStr);

                    JSONArray tempJArray = new JSONArray(tempStr);
                    Log.d(TAG, "!!@@ jArray 확인 : " + tempJArray);

                    JSONObject tempSecond = tempJArray.optJSONObject(0);
                    Log.d(TAG, "!!@@ JSONobject 추출 : " + tempSecond);

                    final String[] pm10Grade = {tempSecond.getString("pm10Grade")};
                    final String[] pm25Grade = {tempSecond.getString("pm25Grade")};
                    final String measureTime = tempSecond.getString("dataTime");
                    Log.d(TAG, "!!@@ 미세먼지 등급확인 첫번째 : " + pm10Grade[0]);
                    Log.d(TAG, "!!@@ 미세먼지 등급확인 두번째 : " + pm25Grade[0]);

                    //미세먼지와 초미세먼지의 등급이 1,2,3,4로 표현되어있고 작을수록 좋은 등급이라서 아래처럼 등급을 한글로 보이게 나눠줬음
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //pm10Grade.equals("1")이라고 코드를 치니까 안드로이드 스튜디오에서 아래처럼 배열로 사용하라고 띄워줘서 그렇게 사용함
                            if(pm10Grade[0].equals("1"))
                            {
                                pm10Grade[0] = "좋음";
                            }
                            else if(pm10Grade[0].equals("2"))
                            {
                                pm10Grade[0] = "보통";
                            }
                            else if(pm10Grade[0].equals("3"))
                            {
                                pm10Grade[0] = "나쁨";
                            }
                            else if(pm10Grade[0].equals("4"))
                            {
                                pm10Grade[0] = "매우나쁨";
                            }

                            if(pm25Grade[0].equals("1"))
                            {
                                pm25Grade[0] = "좋음";
                            }
                            else if(pm25Grade[0].equals("2"))
                            {
                                pm25Grade[0] = "보통";
                            }
                            else if(pm25Grade[0].equals("3"))
                            {
                                pm25Grade[0] = "나쁨";
                            }
                            else if(pm25Grade[0].equals("4"))
                            {
                                pm25Grade[0] = "매우나쁨";
                            }

                            tvPM10.setText("미세먼지의 농도 : " + pm10Grade[0]);
                            tvPM25.setText("초미세먼지의 농도 : " + pm25Grade[0]);
                            tvMeasureTime.setText("측정 시간 : " + measureTime);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start(); // 쓰레드 시작
    }


    //지정된 시간에 알람을 울리는 메소드
    //스레드를 시작하는 경기시작 버튼을 누르면 사용됨
    public void alarmStart() {

        // Receiver 설정
        Intent intent = new Intent(this, AlarmReceiver.class);
        // state 값이 on 이면 알람시작, off 이면 중지
        intent.putExtra("state", "on");

        this.pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        /*
         * Intent 플래그
         *    FLAG_ONE_SHOT : 한번만 사용하고 다음에 이 PendingIntent가 불려지면 Fail을 함
         *    FLAG_NO_CREATE : PendingIntent를 생성하지 않음. PendingIntent가 실행중인것을 체크를 함
         *    FLAG_CANCEL_CURRENT : 실행중인 PendingIntent가 있다면 기존 인텐트를 취소하고 새로만듬
         *    FLAG_UPDATE_CURRENT : 실행중인 PendingIntent가 있다면  Extra Data만 교체함
         */

        // 알람 매니저설정
        //오차가 ±3초 정도 있는것 같음
        //System.currentTimeMillis() + (minute*1000) 이 부분이 지정된 시간(내가 입력한 시간)
        //지금은 시연을 위해 sec 단위로 나타낸것이고, 원래대로 분단위로 나타내려면 (minute*60000)로 해야함 - 1분은 60초이기때문에
        this.alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (minute*1000), pendingIntent);
        /*
         * AlarmType
         *    RTC_WAKEUP : 대기모드에서도 알람이 작동함을 의미함
         *    RTC : 대기모드에선 알람을 작동안함
         */
    }

    //알람을 중지하는 메소드
    //이 메소드를 사용해주지 않으면 서비스가 종료되지 않는것 같음 - 어플을 run 한다음에 메모리에서 해당 어플을 종료하면 run버튼이 다시 재생버튼 처럼 돌아와야 하는데 계속 돌아오지않았음
    //스레드가 종료될때 뜨는 다이얼로그의 확인버튼을 누르면 실행됨
    private void alarmStop() {
        if (this.pendingIntent == null) {
            return;
        }

        // 알람 취소
        this.alarmManager.cancel(this.pendingIntent);

        // 알람 중지 Broadcast
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("state","off");

        sendBroadcast(intent);

        this.pendingIntent = null;
    }
}
