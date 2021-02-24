package com.example.squadmaker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.squadmaker.R;
import com.example.squadmaker.data.LvFormationListItem;

import java.util.ArrayList;

//FormationListActivity에서 사용한 리스트뷰에 적용한 어댑터
public class LvFormationListAdapter extends BaseAdapter {

    String TAG = "LvFormationListAdapter";

    private ArrayList<LvFormationListItem> lvItemList = new ArrayList<>();
    private Context context;
    private LayoutInflater layoutInflater;

    //어댑터 생성자에서 데이터의 리스트를 전달받음
    public LvFormationListAdapter (Context context, ArrayList<LvFormationListItem> lvItem) {
        this.context = context;
        lvItemList = lvItem;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    //Adapter에 사용되는 데이터의 개수를 리턴해줌
    public int getCount() {
        return lvItemList.size();
    }

    @Override
    //해당 위치(position)에 있는 데이터 리턴
    public Object getItem(int position) {
        return lvItemList.get(position);
    }

    @Override
    //해당 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //아이템을 나타낼 view에 iv_formation_list 레이아웃을 적용시킴
        View view = layoutInflater.inflate(R.layout.item_lv_formation_list, null);

        //위의 해당 레이아웃에서 직접 나타내줄 텍스트뷰를 적용시킴
        TextView tvFormationList = view.findViewById(R.id.tv_item_formation_list);

        //해당 텍스트뷰에 입력된 정보를 나타내게 해줌
        tvFormationList.setText(lvItemList.get(position).getFormation());

        return view;
    }

    //아이템 데이터 추가를 위한 함수 - 나는 string변수 하나만 입력받으면 되기때문에 이렇게 만들었음
    public void addItem(String formation) {

        //이부분에서 가장 위에 arrayList를 만들때 뒤에 객체화(new ArrayList<>())를 안시켜주면 데이터가 안넘어가는 현상이 발생했었음
        LvFormationListItem data = new LvFormationListItem(formation);

        lvItemList.add(data);
    }
}
