package com.example.squadmaker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squadmaker.PreferenceManager;
import com.example.squadmaker.R;
import com.example.squadmaker.adapter.RvEditModeAdapter;
import com.example.squadmaker.adapter.RvPlayerListAdapter;
import com.example.squadmaker.data.RvEditModeItem;
import com.example.squadmaker.data.RvPlayerListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// 이 액티비티는 formationActivity에서 연필모양의 버튼(편집모드)를 눌렀을때, 선수들의 이미지(빨간점)를 누르면 나타나는 팝업창 처럼 보이는 역할을 함
// playerListActivity에서 만든 선수들의 정보를 그대로 가져와서 리사이클러뷰로 보여줌
// 리사이클러뷰의 해당 아이템을 클릭할때 입력된 정보(등번호, 이름)을 경기장 이미지에 보여줌
public class EditModeActivity extends AppCompatActivity {

    // formationActivity에서 선수이미지를 클릭했을때 startActivityForResult에 대응하는 결과코드
    // 135라는 숫자에 의미는 없고, 해당 코드를 하나의 값으로 지정해주기 위해서 만들었음
    public static final int playerDataResultCode = 135;

    String TAG = "EditModeActivity";

    RecyclerView rvEditMode;
    RvEditModeAdapter rvEditModeAdapter;
    ArrayList<RvEditModeItem> rvEditModeDataList = new ArrayList<>();

    PreferenceManager PM = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mode_popup);

        rvEditMode = findViewById(R.id.rv_edit_mode);
        rvEditModeAdapter = new RvEditModeAdapter(EditModeActivity.this, rvEditModeDataList);
        rvEditMode.setAdapter(rvEditModeAdapter);

        // 리사이클러뷰에 LinearLayoutManager 지정 (따로 설정 안하면 자동으로 vertical)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditModeActivity.this);
        rvEditMode.setLayoutManager(linearLayoutManager);

        //아이템 데코레이션 - 아이템들을 구분하기 위해서 사용하는것. 선으로 나타낼수도있고 따로 클래스를 만들어서 여백을 줄수있게 할수도 있음
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(EditModeActivity.this, linearLayoutManager.getOrientation());
        rvEditMode.addItemDecoration(dividerItemDecoration);

        //이 액티비티에서 리사이클러뷰의 아이템클릭리스너를 만들었음
        //RvEditModeAdapter에서 인터페이스로 만든 메소드의 내용을 추가해줬음
        //추가한내용은 해당 아이템을 클릭하면 formationActivity로 해당 아이템의 데이터(등번호, 이름)을 전달함
        rvEditModeAdapter.setOnItemClickListener(new RvEditModeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) throws JSONException {

                //playListActivity에서 생성된 선수 정보를 이 액티비티로 불러옴
                String tempStr = PM.getString(EditModeActivity.this, "playerData");
                Log.d(TAG, "!!@@ JSON으로 저장한데이터 불러오기 : " + tempStr);

                //이부분에서만 잠시 사용할 jsonArray 생성 - 그후 위에서 받아온 String값을 넣어줌
                JSONArray tempArray = new JSONArray(tempStr);

                //이부분에서만 잠시 사용할 jsonObject 생성한 후에 해당위치(position)의 object를 가져옴
                JSONObject tempObject = tempArray.getJSONObject(position);

                //tempObject에서 "이름", "등번호" 인 값을 가져옴
                String tempPlayerName = tempObject.getString("이름");
                String tempPlayerBacknumber = tempObject.getString("등번호");

                //가져온 데이터를 인텐트를 통해서 formationActivity로 보내준 후 finish
                Intent intent = new Intent();
                intent.putExtra("이름", tempPlayerName);
                intent.putExtra("등번호", tempPlayerBacknumber);
                setResult(playerDataResultCode, intent);

                finish();
            }
        }) ;


        //이부분은 playerActivity의 정보를 리사이클러뷰에 보여주는 부분
        String tempStr = PM.getString(EditModeActivity.this, "playerData");
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

                //tempObject에서 "이름", "등번호" 인 값을 추출
                String tempPlayerName = tempObject.getString("이름");
                String tempPlayerBacknumber = tempObject.getString("등번호");

                //추출한 string 값을 리사이클러뷰의 아이템에 넣어주고
                RvEditModeItem data = new RvEditModeItem(tempPlayerName, tempPlayerBacknumber + ". ");
                //어댑터의 리스트에 넣어주고
                rvEditModeDataList.add(data);
                //어탭터를 갱신해서 리사이클러뷰에 보여줌
                rvEditModeAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
