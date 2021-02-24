package com.example.squadmaker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.squadmaker.R;
import com.example.squadmaker.adapter.LvFormationListAdapter;
import com.example.squadmaker.data.LvFormationListItem;

import java.util.ArrayList;

public class FormationListActivity extends AppCompatActivity {

    String TAG = "FormationListActivity";

    ArrayList<LvFormationListItem> formationDataList = new ArrayList<>();
    LvFormationListAdapter lvFormationListAdapter;
    ListView lvFormation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formation_list);

        //현재 액티비티는 개발자가 미리 포메이션 정보를 입력해놓는 액티비티
        //그래서 텍스트 정보만 입력하는 리스트뷰를 사용
        lvFormation = findViewById(R.id.lv_formation_list);
        lvFormationListAdapter = new LvFormationListAdapter(this, formationDataList);
        lvFormation.setAdapter(lvFormationListAdapter);

        //해당 어댑터에 addItem이란 메소드를 만들어놨음 - 어댑터의 리스트에 내가 입력한 string을 추가해주는 메소드
        //이 부분이 포메이션 정보를 미리 만들어 놓는 부분
        lvFormationListAdapter.addItem("5-4-1");
        lvFormationListAdapter.addItem("5-3-2");
        lvFormationListAdapter.addItem("5-2-3");
        lvFormationListAdapter.addItem("4-5-1");
        lvFormationListAdapter.addItem("4-4-2");
        lvFormationListAdapter.addItem("4-3-3");
        lvFormationListAdapter.addItem("4-2-4");
        lvFormationListAdapter.addItem("3-5-2");
        lvFormationListAdapter.addItem("3-4-3");

        //리스트뷰의 각 아이템마다 가지고 있는 formation을 클릭시에 해당 값을 이전 액티비티(formationActivity)에 전달해주기 위해만듬
        lvFormation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //위에 addItem을 통해 입력된 순서대로 진행됨
                //헷갈림을 방지하기 위해 key, value, resultCode를 통일함
                if(position == 0)
                {
                    Intent intent = new Intent();
                    intent.putExtra("541", "541");
                    setResult(541, intent);
                    finish();
                }
                else if(position == 1)
                {
                    Intent intent = new Intent();
                    intent.putExtra("532", "532");
                    setResult(532, intent);
                    finish();
                }
                else if(position == 2)
                {
                    Intent intent = new Intent();
                    intent.putExtra("523", "523");
                    setResult(523, intent);
                    finish();
                }
                else if(position == 3)
                {
                    Intent intent = new Intent();
                    intent.putExtra("451", "451");
                    setResult(451, intent);
                    finish();
                }
                else if(position == 4)
                {
                    Intent intent = new Intent();
                    intent.putExtra("442", "442");
                    setResult(442, intent);
                    finish();
                }
                else if(position == 5)
                {
                    Intent intent = new Intent();
                    intent.putExtra("433", "433");
                    setResult(433, intent);
                    finish();
                }
                else if(position == 6)
                {
                    Intent intent = new Intent();
                    intent.putExtra("424", "424");
                    setResult(424, intent);
                    finish();
                }
                else if(position == 7)
                {
                    Intent intent = new Intent();
                    intent.putExtra("352", "352");
                    setResult(352, intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.putExtra("343", "343");
                    setResult(343, intent);
                    finish();
                }
            }
        });
    }
}
