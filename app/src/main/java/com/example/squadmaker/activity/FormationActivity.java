package com.example.squadmaker.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squadmaker.PreferenceManager;
import com.example.squadmaker.R;
import com.example.squadmaker.adapter.RvEditModeAdapter;
import com.example.squadmaker.adapter.RvFormationImageAdapter;
import com.example.squadmaker.adapter.RvPlayerListAdapter;
import com.example.squadmaker.data.RvFormationImageItem;
import com.example.squadmaker.data.RvPlayerListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.squadmaker.activity.EditModeActivity.playerDataResultCode;

public class FormationActivity extends AppCompatActivity {

    String TAG = "FormationActivity";

    //FormationListActivity에서 포메이션정보를 받아올때 사용하는 리퀘스트코드, 값을 지정해놓기 위해 사용
    static final int formationRequestCode = 210;

    //EditModeActivity에서 선수들의 이름과 등번호를 받아올때 사용할 리퀘스트 코드
    //연필모양의 버튼을 누르면 선수들의 이미지가 클릭이 가능해지는데 그때 사용되는 리퀘스트코드
    static final int playerRequestCode1 = 1;
    static final int playerRequestCode2 = 2;
    static final int playerRequestCode3 = 3;
    static final int playerRequestCode4 = 4;
    static final int playerRequestCode5 = 5;
    static final int playerRequestCode6 = 6;
    static final int playerRequestCode7 = 7;
    static final int playerRequestCode8 = 8;
    static final int playerRequestCode9 = 9;
    static final int playerRequestCode10 = 10;

    //editMode 버튼(연필모양버튼)을 클릭할때마다 이미지와 토스트메세지를 바뀌게 해주기 위해 사용하는 변수
    int editModeControl = 0;

    //startActivityForResult를 통해 FormationListActivity의 리스트뷰에서 아이템 클릭시 받아올 string 값을 저장하기 위해 만들었음
    //이 변수를 통해서 선수들의 좌표를 나타내는 메소드(setFormation())를 컨트롤함
    String formationData;

    //경기장 이미지 위에 선수들을 나타낼 빨간색 원 이미지 10개
    //선수이름과 등번호를 나타낼수있는 텍스트뷰 10개 : PlayerListActivity에서 만들어진 정보를 편집모드를 통해 정보받아 나타낼 텍스트(입력받지 않으면 사용되지 않음)
    //편집모드란 왼쪽 상단에 연필모양의 버튼이 있는데 그 버튼을 한번 누르면 선수들의 이미지를 클릭 가능하게 하고, 한번 더 누르면 클릭을 못하게 함
    //선수들의 이미지를 클릭하면 PlayerListActivity에 있는 리사이클러뷰를 팝업창(EditModeActivity)으로 보여주게 하고, 팝업창에서 해당 아이템을 클릭할때 그 등번호와 이름을 불러와서 포메이션에 직접 나타내줄때 사용
    //왼쪽 아래가 1번. 우측,상단 으로 갈수록 번호 증가 - 이 부분은 실행시켜보면 알수있음
    ImageView ivPlayerCircle1, ivPlayerCircle2, ivPlayerCircle3, ivPlayerCircle4, ivPlayerCircle5, ivPlayerCircle6, ivPlayerCircle7, ivPlayerCircle8, ivPlayerCircle9, ivPlayerCircle10;
    TextView tvPlayerCircleName1, tvPlayerCircleName2, tvPlayerCircleName3, tvPlayerCircleName4, tvPlayerCircleName5, tvPlayerCircleName6, tvPlayerCircleName7, tvPlayerCircleName8, tvPlayerCircleName9, tvPlayerCircleName10;
    TextView tvPlayerCircleBackNumber1, tvPlayerCircleBackNumber2, tvPlayerCircleBackNumber3, tvPlayerCircleBackNumber4, tvPlayerCircleBackNumber5, tvPlayerCircleBackNumber6, tvPlayerCircleBackNumber7, tvPlayerCircleBackNumber8, tvPlayerCircleBackNumber9, tvPlayerCircleBackNumber10;
    //RelativeLayout rlPlayer1, rlPlayer2, rlPlayer3, rlPlayer4, rlPlayer5, rlPlayer6, rlPlayer7, rlPlayer8, rlPlayer9, rlPlayer10;

    //경기장 이미지 부분을 나타낸 레이아웃(경기장 위에 선수들 10명을 한번에 캡쳐해서 저장하기위해)
    ConstraintLayout clGround;

    //좌표를 저장할때 사용하는 변수 - 선수이미지가 움직일때 가장 처음의 좌표를 알아야 해서 사용하는 변수
    //절대좌표는 앱의 좌측상단이 (0,0)이고, 상대좌표는 해당 뷰의 좌측 상단이 (0,0)
    float prevX, prevY;
    //선수 이미지(빨간원)들이 저장될때 그 좌표를 저장할 변수
    float recentX1, recentY1, recentX2, recentY2, recentX3, recentY3, recentX4, recentY4, recentX5, recentY5, recentX6, recentY6, recentX7, recentY7, recentX8, recentY8, recentX9, recentY9, recentX10, recentY10;

    RecyclerView rvFormationImage;
    RvFormationImageAdapter rvFormationImageAdapter;
    ArrayList<RvFormationImageItem> rvFormationImageList = new ArrayList<>();

    // 이 부분을 미리 만들어놓는 이유는
    // 액티비티 하단의 포메이션 이미지를 나타내는 리사이클러뷰에 저장된 데이터를 미리 나타내준 다음
    // 새로운 데이터를 추가할때 이미 만들어진 내용을 담기 위해 만들어놨음
    // 그래서 onCreate()에서 저장된 데이터를 불러오는 부분에서 먼저 사용, 그래서 미리 저장된 데이터를 가지고있는 상태를 만들어줬음
    JSONArray jArrayFormationData = new JSONArray();

    PreferenceManager PM = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formation);

        //이 액티비티에서 사용되는 뷰들을 관리하는 메소드, 아래에 만들었음
        viewCreate();

        //저장한 포메이션 정보를 불러오는 부분
        String tempStr = PM.getString(FormationActivity.this, "formationData");
        try {
            JSONArray tempArray = new JSONArray(tempStr);

            for(int i = 0; i < tempArray.length(); i++)
            {
                JSONObject tempObject = tempArray.getJSONObject(i);

                //이 부분이 위에서 객체화 할때 써논 내용
                //저장된 데이터를 불러와서 가지고 있어야 새로운 데이터를 추가할때 이 부분도 같이 추가됨
                jArrayFormationData.put(tempObject);

                //string으로 저장된 bitmap을 다시 전환 시킨후 리사이클러뷰에 나타내줌
                Bitmap bitmap = stringToBitmap(tempObject.getString("포메이션이미지"));
                RvFormationImageItem data  = new RvFormationImageItem(bitmap);
                rvFormationImageList.add(data);
                rvFormationImageAdapter.notifyDataSetChanged();
            }
       } catch (JSONException e) { e.printStackTrace(); }

        //해당 리사이클러뷰의 아이템 클릭 리스너
        //리사이클러뷰에 아이템을 저장할때 각 선수들의 좌표값도 저장하는데 해당 아이템을 클릭할때
        //그 저장된 포메이션 그대로 좌표를 불러오기 위해서 사용
        rvFormationImageAdapter.setOnItemClickListener(new RvFormationImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) throws JSONException {

                //formationData라는 key에 사진과 좌표값이 모두 저장되어있음
                //그 부분을 하나씩 추출하는 과정
                String tempStr = PM.getString(FormationActivity.this, "formationData");

                JSONArray tempArray = new JSONArray(tempStr);

                JSONObject tempObject = tempArray.getJSONObject(position);

                tvPlayerCircleName1.setText(tempObject.getString("1번선수 이름"));
                tvPlayerCircleBackNumber1.setText(tempObject.getString("1번선수 등번호"));
                ivPlayerCircle1.setX(tempObject.getInt("1번선수 x좌표"));
                ivPlayerCircle1.setY(tempObject.getInt("1번선수 y좌표"));

                tvPlayerCircleName2.setText(tempObject.getString("2번선수 이름"));
                tvPlayerCircleBackNumber2.setText(tempObject.getString("2번선수 등번호"));
                ivPlayerCircle2.setX(tempObject.getInt("2번선수 x좌표"));
                ivPlayerCircle2.setY(tempObject.getInt("2번선수 y좌표"));

                tvPlayerCircleName3.setText(tempObject.getString("3번선수 이름"));
                tvPlayerCircleBackNumber3.setText(tempObject.getString("3번선수 등번호"));
                ivPlayerCircle3.setX(tempObject.getInt("3번선수 x좌표"));
                ivPlayerCircle3.setY(tempObject.getInt("3번선수 y좌표"));

                tvPlayerCircleName4.setText(tempObject.getString("4번선수 이름"));
                tvPlayerCircleBackNumber4.setText(tempObject.getString("4번선수 등번호"));
                ivPlayerCircle4.setX(tempObject.getInt("4번선수 x좌표"));
                ivPlayerCircle4.setY(tempObject.getInt("4번선수 y좌표"));

                tvPlayerCircleName5.setText(tempObject.getString("5번선수 이름"));
                tvPlayerCircleBackNumber5.setText(tempObject.getString("5번선수 등번호"));
                ivPlayerCircle5.setX(tempObject.getInt("5번선수 x좌표"));
                ivPlayerCircle5.setY(tempObject.getInt("5번선수 y좌표"));

                tvPlayerCircleName6.setText(tempObject.getString("6번선수 이름"));
                tvPlayerCircleBackNumber6.setText(tempObject.getString("6번선수 등번호"));
                ivPlayerCircle6.setX(tempObject.getInt("6번선수 x좌표"));
                ivPlayerCircle6.setY(tempObject.getInt("6번선수 y좌표"));

                tvPlayerCircleName7.setText(tempObject.getString("7번선수 이름"));
                tvPlayerCircleBackNumber7.setText(tempObject.getString("7번선수 등번호"));
                ivPlayerCircle7.setX(tempObject.getInt("7번선수 x좌표"));
                ivPlayerCircle7.setY(tempObject.getInt("7번선수 y좌표"));

                tvPlayerCircleName8.setText(tempObject.getString("8번선수 이름"));
                tvPlayerCircleBackNumber8.setText(tempObject.getString("8번선수 등번호"));
                ivPlayerCircle8.setX(tempObject.getInt("8번선수 x좌표"));
                ivPlayerCircle8.setY(tempObject.getInt("8번선수 y좌표"));

                tvPlayerCircleName9.setText(tempObject.getString("9번선수 이름"));
                tvPlayerCircleBackNumber9.setText(tempObject.getString("9번선수 등번호"));
                ivPlayerCircle9.setX(tempObject.getInt("9번선수 x좌표"));
                ivPlayerCircle9.setY(tempObject.getInt("9번선수 y좌표"));

                tvPlayerCircleName10.setText(tempObject.getString("10번선수 이름"));
                tvPlayerCircleBackNumber10.setText(tempObject.getString("10번선수 등번호"));
                ivPlayerCircle10.setX(tempObject.getInt("10번선수 x좌표"));
                ivPlayerCircle10.setY(tempObject.getInt("10번선수 y좌표"));
            }
        }) ;
    }

    //해당 액티비티에서 사용될 뷰를 관리해주는 메소드
    private void viewCreate() {

        //해당 액티비티를 열자마자 refresh 버튼을 누를수 있기때문에 그 부분에 대한 값을 추가함
        //이 변수는 FormationListActivity에서 포메이션의 정보를 받아와서 저장하는 역할을 함
        formationData = "init";

        //해당 액티비티 상단 메뉴바에서 첫번째 이미지 - 편집모드 : 버튼을 한번 누르면 선수들의 이미지를 클릭 가능하게 하고, 한번 더 누르면 클릭을 못하게 함
        final ImageView ivEditMode = findViewById(R.id.iv_edit_mode);
        //해당 액티비티 상단 메뉴바에서 두번째 이미지 - 포메이션에서 플레이어들의 이름과 등번호를 나타내주는 액티비티로 넘어가게 해줌
        ImageView ivPlayerList = findViewById(R.id.iv_player_list);
        //해당 액티비티 상단 메뉴바에서 세번째 이미지 - 포메이션들의 목록을 쭉 나열시켜논 액티비티로 넘어가게 해줌
        ImageView ivFormationList = findViewById(R.id.iv_formation_list);
        //해당 액티비티 상단 메뉴바에서 네번째 이미지 - 선수들 이미지의 위치를 초기화 해줌
        ImageView ivRefresh = findViewById(R.id.iv_refresh);
        //경기장 이미지에서 나타난 포메이션을 저장하게 해주는 버튼
        Button btnSave = findViewById(R.id.btn_formation_save);

        //경기장 이미지 위에 나타난 빨간 점 이미지(선수들을 나타냄)
        //for문으로 나타내려고 1234 사용했는데 실패함
        ivPlayerCircle1 = findViewById(R.id.iv_circle_1);
        ivPlayerCircle2 = findViewById(R.id.iv_circle_2);
        ivPlayerCircle3 = findViewById(R.id.iv_circle_3);
        ivPlayerCircle4 = findViewById(R.id.iv_circle_4);
        ivPlayerCircle5 = findViewById(R.id.iv_circle_5);
        ivPlayerCircle6 = findViewById(R.id.iv_circle_6);
        ivPlayerCircle7 = findViewById(R.id.iv_circle_7);
        ivPlayerCircle8 = findViewById(R.id.iv_circle_8);
        ivPlayerCircle9 = findViewById(R.id.iv_circle_9);
        ivPlayerCircle10 = findViewById(R.id.iv_circle_10);

        tvPlayerCircleName1 = findViewById(R.id.tv_circle_1_name);
        tvPlayerCircleName2 = findViewById(R.id.tv_circle_2_name);
        tvPlayerCircleName3 = findViewById(R.id.tv_circle_3_name);
        tvPlayerCircleName4 = findViewById(R.id.tv_circle_4_name);
        tvPlayerCircleName5 = findViewById(R.id.tv_circle_5_name);
        tvPlayerCircleName6 = findViewById(R.id.tv_circle_6_name);
        tvPlayerCircleName7 = findViewById(R.id.tv_circle_7_name);
        tvPlayerCircleName8 = findViewById(R.id.tv_circle_8_name);
        tvPlayerCircleName9 = findViewById(R.id.tv_circle_9_name);
        tvPlayerCircleName10 = findViewById(R.id.tv_circle_10_name);

        tvPlayerCircleBackNumber1 = findViewById(R.id.tv_circle_1_back_number);
        tvPlayerCircleBackNumber2 = findViewById(R.id.tv_circle_2_back_number);
        tvPlayerCircleBackNumber3 = findViewById(R.id.tv_circle_3_back_number);
        tvPlayerCircleBackNumber4 = findViewById(R.id.tv_circle_4_back_number);
        tvPlayerCircleBackNumber5 = findViewById(R.id.tv_circle_5_back_number);
        tvPlayerCircleBackNumber6 = findViewById(R.id.tv_circle_6_back_number);
        tvPlayerCircleBackNumber7 = findViewById(R.id.tv_circle_7_back_number);
        tvPlayerCircleBackNumber8 = findViewById(R.id.tv_circle_8_back_number);
        tvPlayerCircleBackNumber9 = findViewById(R.id.tv_circle_9_back_number);
        tvPlayerCircleBackNumber10 = findViewById(R.id.tv_circle_10_back_number);


        //경기장 이미지 (위의 빨간점 이미지 10개를 포함한 레이아웃)
        clGround = findViewById(R.id.cl_ground);

        //선수들의 이미지를 드래그 앤 드롭 해주는 메소드, 여기서 각각의 좌표값도 알수있음
        playerImageControl();

        rvFormationImage = findViewById(R.id.rv_saved_image_file);
        rvFormationImageAdapter = new RvFormationImageAdapter(this, rvFormationImageList);
        rvFormationImage.setAdapter(rvFormationImageAdapter);

        //리니어 레이아웃을 가로방향으로 생성 (경기장 위에 포메이션을 만든 이미지뷰를 액티비티의 가장 하단에 가로방향으로 보여주려고 함)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFormationImage.setLayoutManager(linearLayoutManager);


        //ivEditMode - 연필모양의 버튼
        //한번 누르면 이미지가 바뀌고 편집모드 on 상태로 바뀜 --> 그러면 선수들의 이미지가 클릭이 가능해짐
        //다시 한번더 누르면 원래 이미지로 돌아오고 선수들의 이미지는 클릭이 불가능해짐
        //한번 누를때마다 토스트메시지로 편집모드 on, 편집모드 off라는 문구를 보여주게 했음
        ivEditMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //편집모드 on 상태
                //선수들 이미지 각각에 클릭 리스너를 붙여줘서 편집모드에 들어왔을때만 클릭을 가능하게 했음
                if(editModeControl == 0)
                {
                    //한번 클릭했을때 이미지를 바꿔주는 부분
                    ivEditMode.setImageResource(R.drawable.image_edit_mode);
                    //이 변수를 통해 if문의 조건을 나타냈음
                    //아래에 else부분에서는 --를 해주고있음
                    editModeControl++;

                    //선수들의 이미지를 클릭했을때 팝업창(EditModeActivity)을 띄워주게 했음
                    //거기서 정보(등번호, 이름)를 받아와야 하기 때문에 startActivityForResult를 사용했고
                    //팝업창에 있는 정보를 모든 선수들이 사용할수 있기 때문에 requestCode를 전부 다르게 사용했음
                    ivPlayerCircle1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode1);

                        }
                    });

                    ivPlayerCircle2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode2);

                        }
                    });

                    ivPlayerCircle3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode3);

                        }
                    });

                    ivPlayerCircle4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode4);

                        }
                    });

                    ivPlayerCircle5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode5);

                        }
                    });

                    ivPlayerCircle6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode6);

                        }
                    });

                    ivPlayerCircle7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode7);

                        }
                    });

                    ivPlayerCircle8.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode8);

                        }
                    });

                    ivPlayerCircle9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode9);

                        }
                    });

                    ivPlayerCircle10.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(FormationActivity.this, EditModeActivity.class);
                            startActivityForResult(intent, playerRequestCode10);

                        }
                    });

                    Toast.makeText(FormationActivity.this, "편집모드 on", Toast.LENGTH_SHORT).show();
                }

                //편집모드 off
                else
                {
                    ivEditMode.setImageResource(R.drawable.image_edit);
                    editModeControl--;
                    Toast.makeText(FormationActivity.this, "편집모드 off", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //ivPlayerList 해당 이미지뷰를 클릭하면 PlayerListActivity로 화면전환
        ivPlayerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 포메이션에서 선수명단액티비티로 이동
                Intent intent = new Intent(FormationActivity.this, PlayerListActivity.class);
                startActivity(intent);

            }
        });

        //ivFormationList 해당 이미지뷰를 클릭하면 FormationListActivity 화면전환
        ivFormationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //이동한 액티비티에서 결과값을 받아와야 하기때문에 startActivityForResult를 사용함
                Intent intent = new Intent(FormationActivity.this, FormationListActivity.class);
                startActivityForResult(intent, formationRequestCode);

            }
        });

        //ivRefresh 해당 이미지뷰를 클릭하면 setFormation()를 사용
        //setForamtion() - 선수들의 이미지 좌표를 설정해주는 메소드
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFormation();
            }
        });

        //저장하기 버튼을 누르면 경기장 이미지 부분만 캡쳐해서 저장
        //선수들 이미지의 좌표를 받아와서 저장한 후에 이미지와 함께 쉐어드로 저장
        //progressDialog를 띄워주는 asyncTask - 이미지가 저장되는 시간을 눈으로 보여주기 위해서 사용
        //경기장 이미지를 bitmap으로 변환해서 갤러리에 저장
        //그 경기장 이미지를 string으로 변환해서 쉐어드로 저장 - 재접속 했을때 리사이클러뷰에 보여주기 위해서
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 저장하기 버튼을 누르면 선수이미지 10개의 x좌표, y좌표를 저장하는 부분
                // 이 메소드를 onCreate() 부분에서 사용하면 전부 (0,0) 값을 리턴함
                // 그 이유가 뷰를 완전히 그리기도 전에 좌표값을 얻으려고 해서 그렇다고 함 (출처 : 블로그)
                // 모든 y좌표에 -360을 해줬는데 그 이유는 getLocationOnScreen() 메소드는 내 핸드폰스크린의 좌측상단이 (0,0)을 기점으로 잡기때문에 그만큼의 간격을 미리 설정해줘야 하기 때문
                int[] location1 = new int[2];
                ivPlayerCircle1.getLocationOnScreen(location1);
                recentX1 = location1[0];
                recentY1 = location1[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX1 + ", " + recentY1);

                int[] location2 = new int[2];
                ivPlayerCircle2.getLocationInWindow(location2);
                recentX2 = location2[0];
                recentY2 = location2[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX2 + ", " + recentY2);

                int[] location3 = new int[2];
                ivPlayerCircle3.getLocationInWindow(location3);
                recentX3 = location3[0];
                recentY3 = location3[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX3 + ", " + recentY3);

                int[] location4 = new int[2];
                ivPlayerCircle4.getLocationInWindow(location4);
                recentX4 = location4[0];
                recentY4 = location4[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX4 + ", " + recentY4);

                int[] location5 = new int[2];
                ivPlayerCircle5.getLocationInWindow(location5);
                recentX5 = location5[0];
                recentY5 = location5[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX5 + ", " + recentY5);

                int[] location6 = new int[2];
                ivPlayerCircle6.getLocationInWindow(location6);
                recentX6 = location6[0];
                recentY6 = location6[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX6 + ", " + recentY6);

                int[] location7 = new int[2];
                ivPlayerCircle7.getLocationInWindow(location7);
                recentX7 = location7[0];
                recentY7 = location7[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX7 + ", " + recentY7);

                int[] location8 = new int[2];
                ivPlayerCircle8.getLocationInWindow(location8);
                recentX8 = location8[0];
                recentY8 = location8[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX8 + ", " + recentY8);

                int[] location9 = new int[2];
                ivPlayerCircle9.getLocationInWindow(location9);
                recentX9 = location9[0];
                recentY9 = location9[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX9 + ", " + recentY9);

                int[] location10 = new int[2];
                ivPlayerCircle10.getLocationInWindow(location10);
                recentX10 = location10[0];
                recentY10 = location10[1] - 360;
                Log.d(TAG, "!!@@ 좌표확인 : " + recentX10 + ", " + recentY10);


                //맨 아래부분에 만든 AsyncTask
                //이부분은 이미지를 저장하는동안 progressDialog를 띄워주게 했음.  이미지를 저장하는 시간이 흘러간다는 표현을 해주고싶었음
                LoadingTask loadingTask = new LoadingTask();
                loadingTask.execute();

                //왜 폴더가 생성이 안되는지 이유를 찾아야함 --> 퍼미션때문이였음. 자바코드로 런타임 퍼미션을 설정해주던가, 핸드폰에서 권한설정을 직접 해주면 해결됨
                //찾아봤는데 getExternalStorageDirectory()메소드가 사용중지 될 예정이라고 함
                //getAgetAbsolutePath() 메소드를 제거하고 실행시켜도 똑같은 외부저장소 위치에 폴더가 생성됨
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FormationImage");
                if(!dir.exists())
                {
                    //해당 위치에 FormationImage 폴더를 생성
                    dir.mkdirs();
                }

                //시간을 나타내주는 포맷을 가진 클래스
                SimpleDateFormat day = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date();

                //실제로 저장되는 이미지의 이름만 따로 관리하고 싶어서 만들었음
                String formationImage = "/Capture" + day.format(date) + ".jpeg";

                //해당 뷰의 이미지를 drawing cache에 저장
                //캐시메모리 - 속도 빠른 임시저장소 (cpu에 달려있음), 여기서 drawing cache는 사진을 임시저장 하게 해주는 역할을 하는것같음
                //clGround - 경기장 이미지 위에 10개의 빨간점(선수들)과 텍스트을 나타낸 레이아웃
                clGround.buildDrawingCache();

                //처음에는 캡쳐한 clGround를 uri로 저장한 후에 다시 uri로 불러와서 리사이클러뷰에 나타내려고했는데 멘토와 하브루타 후 비트맵으로 저장 하게 됐음
                //비트맵으로 저장한 이유는 uri로 저장한 이미지를 다시 uri로 불러올때 저장된 사진이 지워지면 오류가 발생할수 있다고 들었기때문에 비트맵으로 저장하게됐음
                //drawing cache에 저장된 이미지를 bitmap으로 변환
                Bitmap captureView = clGround.getDrawingCache();

                //outputStream - 내 프로그램(어플)에서 데이터를 내보낼때 사용, inputStream은 반대로 데이터를 내 프로그램으로 가져올때 사용
                FileOutputStream fos = null;
                try{
                    //해당 위치에 저장할 outputStream 만들기
                    fos = new FileOutputStream(dir + formationImage);

                    //비트맵 이미지를 fos에 저장
                    //비트맵 이미지를 저장할때 용량이 너무 커서 quality를 100에서 50으로 낮춰줌, 그랬더니 1.3MB였던 용량이 0.2MB정도로 감소했음
                    captureView.compress(Bitmap.CompressFormat.JPEG, 50, fos);

                    //비트맵 이미지의 크기를 조절하는 부분, 크기 조절할때 세로길이를 기준으로 함
                    //내가 원하는 이미지의 세로길이를 viewHeight = 70로 나타냄, 70으로 한 이유는 여러가지 값을 넣고 해보다가 알맞게 들어가는 값이여서 70으로 정함
                    int viewHeight = 70;
                    float width = captureView.getWidth();
                    float height = captureView.getHeight();

                    //원본이미지의 세로길이와 비교해서 아래와 같은 비율로 가로세로 길이를 줄여줌
                    if(height > viewHeight)
                    {
                        float percentage = height / 100;
                        float scale = viewHeight / percentage;
                        width = width * scale / 100;
                        height = height * scale / 100;
                    }

                    //이미지의 용량을 줄이기위해 사용한것들, 근데 크게 상관은 없어보였음
                    //inSampleSize는 2,4,8,.. 이런식으로 2의제곱 형태로 나타내야된다고 하고, 이미지의 용량은 반대로 1/2, 1/4, 1/8,... 식으로 줄어든다고 함
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 8;
                    Bitmap tempImage = BitmapFactory.decodeFile(dir + formationImage, options);

                    // 리사이클러뷰에 저장되는 최종 이미지
                    Bitmap resizedCaptureView = Bitmap.createScaledBitmap(tempImage, (int)width, (int)height, true);

                    //최종 이미지를 string으로 변환하는 작업
                    //이 코드 아래부분에 메소드로 만들어져있음
                    String bitmapToString = bitmapToString(resizedCaptureView);

                    //jsonobject에 이미지와 선수들의 좌표, 이름, 등번호를 저장하는 과정
                    JSONObject tempObject = new JSONObject();
                    tempObject.put("포메이션이미지", bitmapToString);

                    tempObject.put("1번선수 이름", tvPlayerCircleName1.getText());
                    tempObject.put("1번선수 등번호", tvPlayerCircleBackNumber1.getText());
                    tempObject.put("1번선수 x좌표", recentX1);
                    tempObject.put("1번선수 y좌표", recentY1);

                    tempObject.put("2번선수 이름", tvPlayerCircleName2.getText());
                    tempObject.put("2번선수 등번호", tvPlayerCircleBackNumber2.getText());
                    tempObject.put("2번선수 x좌표", recentX2);
                    tempObject.put("2번선수 y좌표", recentY2);

                    tempObject.put("3번선수 이름", tvPlayerCircleName3.getText());
                    tempObject.put("3번선수 등번호", tvPlayerCircleBackNumber3.getText());
                    tempObject.put("3번선수 x좌표", recentX3);
                    tempObject.put("3번선수 y좌표", recentY3);

                    tempObject.put("4번선수 이름", tvPlayerCircleName4.getText());
                    tempObject.put("4번선수 등번호", tvPlayerCircleBackNumber4.getText());
                    tempObject.put("4번선수 x좌표", recentX4);
                    tempObject.put("4번선수 y좌표", recentY4);

                    tempObject.put("5번선수 이름", tvPlayerCircleName5.getText());
                    tempObject.put("5번선수 등번호", tvPlayerCircleBackNumber5.getText());
                    tempObject.put("5번선수 x좌표", recentX5);
                    tempObject.put("5번선수 y좌표", recentY5);

                    tempObject.put("6번선수 이름", tvPlayerCircleName6.getText());
                    tempObject.put("6번선수 등번호", tvPlayerCircleBackNumber6.getText());
                    tempObject.put("6번선수 x좌표", recentX6);
                    tempObject.put("6번선수 y좌표", recentY6);

                    tempObject.put("7번선수 이름", tvPlayerCircleName7.getText());
                    tempObject.put("7번선수 등번호", tvPlayerCircleBackNumber7.getText());
                    tempObject.put("7번선수 x좌표", recentX7);
                    tempObject.put("7번선수 y좌표", recentY7);

                    tempObject.put("8번선수 이름", tvPlayerCircleName8.getText());
                    tempObject.put("8번선수 등번호", tvPlayerCircleBackNumber8.getText());
                    tempObject.put("8번선수 x좌표", recentX8);
                    tempObject.put("8번선수 y좌표", recentY8);

                    tempObject.put("9번선수 이름", tvPlayerCircleName9.getText());
                    tempObject.put("9번선수 등번호", tvPlayerCircleBackNumber9.getText());
                    tempObject.put("9번선수 x좌표", recentX9);
                    tempObject.put("9번선수 y좌표", recentY9);

                    tempObject.put("10번선수 이름", tvPlayerCircleName10.getText());
                    tempObject.put("10번선수 등번호", tvPlayerCircleBackNumber10.getText());
                    tempObject.put("10번선수 x좌표", recentX10);
                    tempObject.put("10번선수 y좌표", recentY10);

                    //위에서 미리 데이터 정보를 가지고있는 jsonArray에 추가
                    jArrayFormationData.put(tempObject);

                    //jsonArray를 String으로 변환
                    String result = jArrayFormationData.toString();

                    //변환된 String을 쉐어드로 저장
                    PM.setString(FormationActivity.this,"formationData", result);


                    //위에서 저장시킨 이미지를 bitMap으로 리사이클러뷰에 보여주는 부분
                    RvFormationImageItem data  = new RvFormationImageItem(resizedCaptureView);
                    rvFormationImageList.add(data);
                    rvFormationImageAdapter.notifyDataSetChanged();

                    //캡쳐한 이미지를 갤러리어플에서 확인할수 있게 해줌
                    //아래 내용은 특정한 파일만 스캔해주는 역할을 함
                    //uri.parse("file://"+Environment.getExternalStorageDirectory()+"/폴더")를 해주면 특정 폴더를 스캔해준다고함
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + dir + "/Capture" + day.format(date) + ".JPEG")));

                    //fos.flush - 버퍼에 잔류하는 모든 바이트를 출력
                    fos.close();

                    //drawingcache에 있는 내용을 파괴
                    clGround.destroyDrawingCache();

                } catch (IOException | JSONException e) { e.printStackTrace(); }


            }
        });
    }

    //startForActivityResult를 사용한 부분이 두곳이 있음
    //FormationListActivity에서 formation의 정보를 받아오는 부분이 있고
    //EditModeActivity에서 선수들의 이름과 등번호를 받아오는 부분이 있음
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //FormationListActivity에서 formation의 정보를 받아오는 부분
        //리퀘스트 코드는 formationRequestCode로 위에서 만들었음
        if(requestCode == formationRequestCode)
        {
            //결과코드는 해당 포지션마다 다르게 지정함. 아래 보면 알겠지만 541포메이션이라면 541을 받아옴
            if(resultCode == 541)
            {
                assert data != null;
                //key와 value를 동일하게 설정해뒀음. 그래야 헷깔리지 않을것 같음
                formationData = data.getStringExtra("541");

                //데이터 넘어오는것 확인함
                //Log.d(TAG, "!!@@ onActivityResult() - 541 눌렀을때 데이터 넘어오나 확인하기 : " + formationData);

                setFormation();
            }
            if(resultCode == 532)
            {
                assert data != null;
                formationData = data.getStringExtra("532");
                setFormation();
            }
            if(resultCode == 523)
            {
                formationData = data.getStringExtra("523");
                setFormation();
            }
            if(resultCode == 451)
            {
                formationData = data.getStringExtra("451");
                setFormation();
            }
            if(resultCode == 442)
            {
                formationData = data.getStringExtra("442");
                setFormation();
            }
            if(resultCode == 433)
            {
                formationData = data.getStringExtra("433");
                setFormation();
            }
            if(resultCode == 424)
            {
                formationData = data.getStringExtra("424");
                setFormation();
            }
            if(resultCode == 352)
            {
                formationData = data.getStringExtra("352");
                setFormation();
            }
            if(resultCode == 343)
            {
                formationData = data.getStringExtra("343");
                setFormation();
            }
        }


        //EditModeActivity에서 선수들의 이름과 등번호를 받아오는 부분
        //각 선수들마다 requestCode를 다르게 해줬음
        //그 이유는 EditModeActivity에 있는 데이터를 모든 선수가 다 받을수 있기때문
        if(requestCode == playerRequestCode1)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName1.setText(playerName);
                tvPlayerCircleBackNumber1.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode2)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName2.setText(playerName);
                tvPlayerCircleBackNumber2.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode3)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName3.setText(playerName);
                tvPlayerCircleBackNumber3.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode4)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName4.setText(playerName);
                tvPlayerCircleBackNumber4.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode5)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName5.setText(playerName);
                tvPlayerCircleBackNumber5.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode6)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName6.setText(playerName);
                tvPlayerCircleBackNumber6.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode7)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName7.setText(playerName);
                tvPlayerCircleBackNumber7.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode8)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName8.setText(playerName);
                tvPlayerCircleBackNumber8.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode9)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName9.setText(playerName);
                tvPlayerCircleBackNumber9.setText(playerBackNumber);
            }
        }
        if(requestCode == playerRequestCode10)
        {
            if(resultCode == playerDataResultCode)
            {
                String playerName = data.getStringExtra("이름");
                String playerBackNumber = data.getStringExtra("등번호");

                tvPlayerCircleName10.setText(playerName);
                tvPlayerCircleBackNumber10.setText(playerBackNumber);
            }
        }
    }



    //플레이어 이미지(빨간원 이미지 총 10개)를 터치를 통해 움직일수 있게하고, 해당 좌표를 얻는 메소드
    //이미지에서 손을땔때(ACTION_UP) 최종 좌표를 저장함, 그 저장한 좌표를 btnSave를 누를때 쉐어드로 저장함
    //그래서 리사이클러뷰의 아이템클릭리스너에서 이 좌표를 사용해서 다시 해당 포메이션의 좌표를 그대로 보여주게 함
    //터치리스너 마지막에 return하는부분에 editModeControl을 통해 제어해주는 부분이있는데 return false를 해주면 클릭이 가능
    @SuppressLint("ClickableViewAccessibility")
    private void playerImageControl() {

        ivPlayerCircle1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){

                    //사용자가 터치를 시작했을때(손가락을 누를때) ACTION_DOWN
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;

                    //사용자가 터치해서 움직일때
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;

                    // 이건 뭐지
                    case MotionEvent.ACTION_CANCEL:

                    //사용자가 터치를 끝낼때(손가락을 뗄때)
                    case MotionEvent.ACTION_UP:
                        recentX1 = v.getX();
                        recentY1 = v.getY();
                        Log.d(TAG, "!!@@ 1번째이미지 최종 x,y값 : " + recentX1 + ", " + recentY1);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }

            }
        });

        ivPlayerCircle2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX2 = v.getX();
                        recentY2 = v.getY();
                        Log.d(TAG, "!!@@ 2번째이미지 최종 x,y값 : " + recentX2 + ", " + recentY2);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX3 = v.getX();
                        recentY3 = v.getY();
                        Log.d(TAG, "!!@@ 3번째이미지 최종 x,y값 : " + recentX3 + ", " + recentY3);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX4 = v.getX();
                        recentY4 = v.getY();
                        Log.d(TAG, "!!@@ 4번째이미지 최종 x,y값 : " + recentX4 + ", " + recentY4);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX5 = v.getX();
                        recentY5 = v.getY();
                        Log.d(TAG, "!!@@ 5번째이미지 최종 x,y값 : " + recentX5 + ", " + recentY5);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX6 = v.getX();
                        recentY6 = v.getY();
                        Log.d(TAG, "!!@@ 6번째이미지 최종 x,y값 : " + recentX6 + ", " + recentY6);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX7 = v.getX();
                        recentY7 = v.getY();
                        Log.d(TAG, "!!@@ 7번째이미지 최종 x,y값 : " + recentX7 + ", " + recentY7);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX8 = v.getX();
                        recentY8 = v.getY();
                        Log.d(TAG, "!!@@ 8번째이미지 최종 x,y값 : " + recentX8 + ", " + recentY8);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle9.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX9 = v.getX();
                        recentY9 = v.getY();
                        Log.d(TAG, "!!@@ 9번째이미지 최종 x,y값 : " + recentX9 + ", " + recentY9);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });

        ivPlayerCircle10.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prevX = event.getX();
                        prevY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        Log.v(TAG, "dx : " + dx + " dy :: " + dy);
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        recentX10 = v.getX();
                        recentY10 = v.getY();
                        Log.d(TAG, "!!@@ 10번째이미지 최종 x,y값 : " + recentX10 + ", " + recentY10);
                        break;
                }

                if(editModeControl == 0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }

    //이미지를 저장할때 progressDialog를 보여주는 asyncTask
    private class LoadingTask extends AsyncTask<Void, Void, Void> {

        //android.view.WindowManager$BadTokenException: Unable to add window -- token null is not valid; is your activity running?
        //처음에 토큰 에러가 발생했었음 - ProgressDialog의 context를 getApplicationContext() -->  FormationActivity.this로 바꿧더니 해결함
        ProgressDialog asyncDialog = new ProgressDialog(FormationActivity.this);

        @Override
        //onPreExecute() - async의 사전준비를 해주는 메소드, 초기화를 설정해주는 부분이라고 생각하면 될것같다
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("포메이션을 저장중입니다");

            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        //doInBackground() - 실제로 서브스레드가 작업을 하는 메소드
        protected Void doInBackground(Void... voids) {
            try {

                //ProgressDialog 진행 시간을 정해주는곳
                for(int i = 0; i < 5; i++)
                {
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        //onPostExecute() - 서브스레드가 작업을 마친 후에 메인스레드에서 하는 작업
        protected void onPostExecute(Void aVoid) {
            asyncDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }


    //비트맵이미지를 string으로 변환하는 메소드
    public String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    //String을 비트맵 이미지로 변환하는 메소드
    public Bitmap stringToBitmap(String encodedString) {

        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


    //FormationListActivity에서 포메이션의 정보를 받아왔을때 이미지들의 좌표를 정해주는 메소드
    //onActivityResult(), refresh버튼에 사용됨
    private void setFormation() {

        tvPlayerCircleName1.setText("");
        tvPlayerCircleName2.setText("");
        tvPlayerCircleName3.setText("");
        tvPlayerCircleName4.setText("");
        tvPlayerCircleName5.setText("");
        tvPlayerCircleName6.setText("");
        tvPlayerCircleName7.setText("");
        tvPlayerCircleName8.setText("");
        tvPlayerCircleName9.setText("");
        tvPlayerCircleName10.setText("");

        tvPlayerCircleBackNumber1.setText("");
        tvPlayerCircleBackNumber2.setText("");
        tvPlayerCircleBackNumber3.setText("");
        tvPlayerCircleBackNumber4.setText("");
        tvPlayerCircleBackNumber5.setText("");
        tvPlayerCircleBackNumber6.setText("");
        tvPlayerCircleBackNumber7.setText("");
        tvPlayerCircleBackNumber8.setText("");
        tvPlayerCircleBackNumber9.setText("");
        tvPlayerCircleBackNumber10.setText("");

        if(formationData.equals("init"))
        {
            ivPlayerCircle1.setX(120); ivPlayerCircle1.setY(870);
            ivPlayerCircle2.setX(360); ivPlayerCircle2.setY(870);
            ivPlayerCircle3.setX(600); ivPlayerCircle3.setY(870);
            ivPlayerCircle4.setX(840); ivPlayerCircle4.setY(870);
            ivPlayerCircle5.setX(120); ivPlayerCircle5.setY(540);
            ivPlayerCircle6.setX(360); ivPlayerCircle6.setY(540);
            ivPlayerCircle7.setX(600); ivPlayerCircle7.setY(540);
            ivPlayerCircle8.setX(840); ivPlayerCircle8.setY(540);
            ivPlayerCircle9.setX(280); ivPlayerCircle9.setY(210);
            ivPlayerCircle10.setX(680); ivPlayerCircle10.setY(210);
        }
        if(formationData.equals("541"))
        {
            //총 가로길이 1000, 세로길이 1500
            ivPlayerCircle1.setX(150); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(320); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(490); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(660); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(830); ivPlayerCircle5.setY(900);
            ivPlayerCircle6.setX(180); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(380); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(580); ivPlayerCircle8.setY(550);
            ivPlayerCircle9.setX(780); ivPlayerCircle9.setY(550);
            ivPlayerCircle10.setX(480); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("532"))
        {
            ivPlayerCircle1.setX(150); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(320); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(490); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(660); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(830); ivPlayerCircle5.setY(900);
            ivPlayerCircle6.setX(230); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(480); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(730); ivPlayerCircle8.setY(550);
            ivPlayerCircle9.setX(380); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(580); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("523"))
        {
            ivPlayerCircle1.setX(150); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(320); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(490); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(660); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(830); ivPlayerCircle5.setY(900);
            ivPlayerCircle6.setX(380); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(580); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(230); ivPlayerCircle8.setY(250);
            ivPlayerCircle9.setX(480); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(730); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("451"))
        {
            ivPlayerCircle1.setX(180); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(380); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(580); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(780); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(150); ivPlayerCircle5.setY(550);
            ivPlayerCircle6.setX(320); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(490); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(660); ivPlayerCircle8.setY(550);
            ivPlayerCircle9.setX(830); ivPlayerCircle9.setY(550);
            ivPlayerCircle10.setX(480); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("442"))
        {
            ivPlayerCircle1.setX(180); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(380); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(580); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(780); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(180); ivPlayerCircle5.setY(550);
            ivPlayerCircle6.setX(380); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(580); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(780); ivPlayerCircle8.setY(550);
            ivPlayerCircle9.setX(380); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(580); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("433"))
        {
            ivPlayerCircle1.setX(180); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(380); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(580); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(780); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(230); ivPlayerCircle5.setY(550);
            ivPlayerCircle6.setX(480); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(730); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(230); ivPlayerCircle8.setY(250);
            ivPlayerCircle9.setX(480); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(730); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("424"))
        {
            ivPlayerCircle1.setX(180); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(380); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(580); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(780); ivPlayerCircle4.setY(900);
            ivPlayerCircle5.setX(380); ivPlayerCircle5.setY(550);
            ivPlayerCircle6.setX(580); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(180); ivPlayerCircle7.setY(250);
            ivPlayerCircle8.setX(380); ivPlayerCircle8.setY(250);
            ivPlayerCircle9.setX(580); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(780); ivPlayerCircle10.setY(250);

        }
        if(formationData.equals("352"))
        {
            ivPlayerCircle1.setX(230); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(480); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(730); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(150); ivPlayerCircle4.setY(550);
            ivPlayerCircle5.setX(320); ivPlayerCircle5.setY(550);
            ivPlayerCircle6.setX(490); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(660); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(830); ivPlayerCircle8.setY(550);
            ivPlayerCircle9.setX(380); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(580); ivPlayerCircle10.setY(250);
        }
        if(formationData.equals("343"))
        {
            ivPlayerCircle1.setX(230); ivPlayerCircle1.setY(900);
            ivPlayerCircle2.setX(480); ivPlayerCircle2.setY(900);
            ivPlayerCircle3.setX(730); ivPlayerCircle3.setY(900);
            ivPlayerCircle4.setX(180); ivPlayerCircle4.setY(550);
            ivPlayerCircle5.setX(380); ivPlayerCircle5.setY(550);
            ivPlayerCircle6.setX(580); ivPlayerCircle6.setY(550);
            ivPlayerCircle7.setX(780); ivPlayerCircle7.setY(550);
            ivPlayerCircle8.setX(230); ivPlayerCircle8.setY(250);
            ivPlayerCircle9.setX(480); ivPlayerCircle9.setY(250);
            ivPlayerCircle10.setX(730); ivPlayerCircle10.setY(250);
        }
    }

}
