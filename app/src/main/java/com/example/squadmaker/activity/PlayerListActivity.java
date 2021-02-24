package com.example.squadmaker.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squadmaker.PreferenceManager;
import com.example.squadmaker.R;
import com.example.squadmaker.adapter.RvPlayerListAdapter;
import com.example.squadmaker.data.RvPlayerListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayerListActivity extends AppCompatActivity {

    String TAG = "PlayerListActivity";

    Button btnPlayerListAdd;
    ImageView ivPlayerListDelete;
    RecyclerView rvPlayerList;
    RvPlayerListAdapter rvPlayerListAdapter;
    ArrayList<RvPlayerListItem> rvPlayerDataList = new ArrayList<>();

    JSONArray jArrayPlayerData = new JSONArray();
    JSONObject jObjectTemp = new JSONObject();

    //쉐어드프리퍼런스를 사용하기 위해 만든 클래스 - 뒤에 객체화 (new ~~~) 안해서 오류난적있었음
    //하나의 클래스에 쉐어드프리퍼런스에 사용되는 메소드들을 모아놨음
    PreferenceManager PM = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        btnPlayerListAdd = findViewById(R.id.btn_player_list_add);
        ivPlayerListDelete = findViewById(R.id.iv_player_list_delete);
        rvPlayerList = findViewById(R.id.rv_player_list);

        rvPlayerListAdapter = new RvPlayerListAdapter(this, rvPlayerDataList);
        rvPlayerList.setAdapter(rvPlayerListAdapter);

        // 리사이클러뷰에 LinearLayoutManager 지정 (따로 설정 안하면 자동으로 vertical)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPlayerList.setLayoutManager(linearLayoutManager);

        //아이템 데코레이션 - 아이템들을 구분하기 위해서 사용하는것. 선으로 나타낼수도있고 따로 클래스를 만들어서 여백을 줄수있게 할수도 있음
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvPlayerList.getContext(), linearLayoutManager.getOrientation());
        rvPlayerList.addItemDecoration(dividerItemDecoration);


        //쉐어드프리퍼런스에 입력한 선수이름, 선수등번호 하나씩 불러오기
//        String sharedName = PM.getString(PlayerListActivity.this,"first");
//        String sharedNumber = PM.getString(PlayerListActivity.this,"second");
//
//        //만들어놨던 배열에 입력받은 것들 분리해서 담기
//        playerDataArray = PM.getString(PlayerListActivity.this, "split").split("/");
//
//        //배열에 있는것들 하나씩 출력하기
//        for(int i = 0 ;i < playerDataArray.length; i++) {
//            System.out.println(playerDataArray[i]);
//        }
//
//        Log.i(TAG, "----onStart : " + playerDataArray[0]);
//        Log.i(TAG, "----onStart : " + playerDataArray[1]);
//        아이템에 사용될 객체로 만들어준다
//        RvPlayerListItem data = new RvPlayerListItem(playerDataArray[0], "등번호 : " + playerDataArray[1]);
//        mArrayList.add(0, data); //리스트의 첫번째에 삽입됨
//        rvPlayerData.add(data); //리스트의 마지막에 삽입됨

        //PM.clear(PlayerListActivity.this);

        //쉐어드에서 JSON으로 저장된 내용을 불러오는 부분
        //일단 String으로 받아와서 리사이클러뷰에 보여주려고 하고있음
        String tempStr = PM.getString(PlayerListActivity.this, "playerData");
        Log.d(TAG, "!!@@ JSON으로 저장한데이터 불러오기 : " + tempStr);

        try {
            //이부분에서만 잠시 사용할 jsonArray 생성 - 그후 위에서 받아온 String값을 넣어줌
            JSONArray tempArray = new JSONArray(tempStr);

            //여기서 i값이 리사이클러뷰의 data arrayList와 jsonArray의 index에 해당함
            //그래서 0부터 순차적으로 1씩 올려가면서 반복적으로 사용가능
            for(int i = 0; i < tempArray.length(); i++)
            {
                //이부분에서만 잠시 사용할 jsonObject 생성
                JSONObject tempObject = tempArray.getJSONObject(i);

                //여기서 jsonArray에 다시한번 넣어주는 이유는 다시 선수를 추가할때 이미 만들어져 있던 선수들의 데이터를 가져와야 하기 때문에 미리 만들어둔 jsonArray에 값을 넣어줬음
                //단순히 리사이클러뷰에 띄워주기만 하는거면 이 부분이 필요가 없음
                jArrayPlayerData.put(tempObject);
                Log.d(TAG, "!!@@ jArrayPlayerData 에다가 다시 JSONobject넣어주기 : " + jArrayPlayerData);

                //jsonObject에서 name값이 "이름", "등번호" 인 값을 추출
                String tempPlayerName = tempObject.getString("이름");
                String tempPlayerBacknumber = tempObject.getString("등번호");

                //추출한 string 값을 리사이클러뷰의 아이템에 넣어주고
                RvPlayerListItem data = new RvPlayerListItem(tempPlayerName, "등번호 : " + tempPlayerBacknumber);
                //어댑터의 리스트에 넣어주고
                rvPlayerDataList.add(data);
                //어탭터를 갱신해서 리사이클러뷰에 보여줌
                rvPlayerListAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //플레이어 추가버튼 클릭리스너
        btnPlayerListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //다이얼로그 빌더
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerListActivity.this);
                //빌더에 사용되는 뷰를 붙여줌
                View view = LayoutInflater.from(PlayerListActivity.this).inflate(R.layout.dialog_rv_player_list, null, false);
                builder.setView(view);

                //안에있는 뷰들을 각각 매칭시킴
                final Button buttonSubmit = view.findViewById(R.id.btn_submit);
                final EditText editPlayerName = view.findViewById(R.id.edit_player_name);
                final EditText editPlayerBackNumber = view.findViewById(R.id.edit_player_back_number);

                //버튼의 텍스트가 원래 수정이였음, 이부분에서만 등록으로 바꿔서 사용
                buttonSubmit.setText("등록");

                //위에서 설정해놓은대로 다이얼로그를 만듬
                final AlertDialog dialog = builder.create();
                //Log.i(TAG, "----buttonSubmit.setOnClick, playerName : AlertDialog dialog = builder.create()");


                //다이얼로그에 있는 등록 버튼을 클릭했을때
                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        //PM.clear(PlayerListActivity.this);
                        //사용자가 입력한 내용을 가져와서
                        String playerName = editPlayerName.getText().toString();
                        String playerBackNumber = editPlayerBackNumber.getText().toString();
                        //Log.i(TAG, "----buttonSubmit.setOnClick, playerName : " + playerName);
                        //Log.i(TAG, "----buttonSubmit.setOnClick, playerBackNumber : " + playerBackNumber);

                        try {
                            //아무런 값이 들어있지 않은 jsonObject에 값을 넣어줌
                            //name에 "이름"과 "등번호"만 사용할것이기 때문에 계속 재사용 가능
                            jObjectTemp.put("이름", playerName);
                            jObjectTemp.put("등번호", playerBackNumber);
                            Log.d(TAG, "!!@@ JSONObject jObjectTemp : " + jObjectTemp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //json데이터를 받아오는 부분에서 만들어줬던 jsonArray
                        //여기에 기존선수들의 데이터가 이미 있고, 새로 추가하는 선수들의 데이터를 추가시켜줌
                        jArrayPlayerData.put(jObjectTemp);
                        Log.d(TAG, "!!@@ jArrayPlayerData : " + jArrayPlayerData);

                        //jsonArray를 string으로 변환해서 쉐어드에 저장
                        String result = jArrayPlayerData.toString();
                        Log.d(TAG, "!!@@ String result : " + result);

                        PM.setString(PlayerListActivity.this,"playerData", result);




                        // 아이템에 추가할 데이터를 만들고
                        RvPlayerListItem data = new RvPlayerListItem(playerName, "등번호 : " + playerBackNumber);
                        //Log.i(TAG, "----buttonSubmit.setOnClick, data : " + data.getPlayerName());
                        //Log.i(TAG, "----buttonSubmit.setOnClick, data : " + data.getPlayerBackNumber());

                        //데이터를 리스트에 추가
                        //mArrayList.add(0, data); //첫번째 줄에 삽입됨
                        rvPlayerDataList.add(data); //마지막 줄에 삽입됨
                        //Log.i(TAG, "----buttonSubmit.setOnClick, mList : " + rvPlayerData.get(0).getPlayerName());
                        //Log.i(TAG, "----buttonSubmit.setOnClick, mList : " + rvPlayerData.get(0).getPlayerBackNumber());

                        //저장버튼 누르면 쉐어드프리퍼런스에 저장 (단순히 하나하나 저장하기)
                        //PM.setString(PlayerListActivity.this, "first", playerName);
                        //PM.setString(PlayerListActivity.this, "second", playerBackNumber);
                        //Log.i(TAG, "----buttonSubmit.setOnClick, PM.setString : " + PM.getString(PlayerListActivity.this, "first"));
                        //Log.i(TAG, "----buttonSubmit.setOnClick, PM.setString : " + PM.getString(PlayerListActivity.this, "second"));

                        //하나의 키에 두개의 값 저장하기
                        //playerData = playerName + "/" + playerBackNumber;
                        //PM.setString(PlayerListActivity.this, "split", playerData);
                        //Log.i(TAG, "----buttonSubmit.setOnClick, PM.setString : " + PM.getString(PlayerListActivity.this, "split"));

                        //리스트의 상단에 입력하기
                        //mAdapter.notifyItemInserted(0);
                        //Log.i(TAG, "----buttonSubmit.setOnClick, mAdapter : " + mList.get(0));

                        //리스트 갱신하기
                        rvPlayerListAdapter.notifyDataSetChanged();
                        //Log.i(TAG, "----buttonSubmit.setOnClick, mAdapter : " + rvPlayerListAdapter.rvPlayerDataList.get(0).getPlayerName());
                        //Log.i(TAG, "----buttonSubmit.setOnClick, mAdapter : " + rvPlayerListAdapter.rvPlayerDataList.get(0).getPlayerBackNumber());
                        dialog.dismiss();
                    }
                });

                dialog.show();

            }
        });

        //액티비티의 우측 상단에있는 휴지통버튼
        //"playerData"라는 키의 value들을 전부 삭제하기 위한 버튼
        ivPlayerListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerListActivity.this);
                builder.setTitle("선수 목록 초기화");
                builder.setMessage("정말 목록을 초기화 하시겠습니까 ?");
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // User cancelled the dialog
                    }
                });
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Your Button Click Action Code

                        //데이터를 관리하는 리스트의 모든 데이터를 지워준 후에 어댑터를 갱신시김
                        //리사이클러뷰를 관리하는부분
                        rvPlayerDataList.clear();
                        rvPlayerListAdapter.notifyDataSetChanged();

                        //여기가 쉐어드에 저장된 playerData의 value들을 전부 지워주는 부분
                        PM.removeKey(PlayerListActivity.this, "playerData");
                        Log.d(TAG, "!!@@ 선수목록 초기화 : " + PM.getString(PlayerListActivity.this,"playerData"));
                    }
                });
                builder.show();
            }
        });
    }
}
