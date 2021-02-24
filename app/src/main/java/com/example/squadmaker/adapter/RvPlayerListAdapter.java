package com.example.squadmaker.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squadmaker.PreferenceManager;
import com.example.squadmaker.R;
import com.example.squadmaker.data.RvPlayerListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RvPlayerListAdapter extends RecyclerView.Adapter<RvPlayerListAdapter.CustomViewHolder> {

    String TAG = "RvPlayerListAdapter";

    public ArrayList<RvPlayerListItem> rvPlayerDataList;
    private Context context;

    PreferenceManager PM = new PreferenceManager();

    //생성자를 통해서 데이터를 저장할 arraylist를 지정함
    public RvPlayerListAdapter(Context context, ArrayList<RvPlayerListItem> list) {
        this.context = context;
        rvPlayerDataList = list;
    }

    @NonNull
    @Override
    //뷰홀더에 사용될 xml파일을 지정해주는 메소드
    public RvPlayerListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //xml파일 자체를 인플레이트 해준다
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_player_list, parent, false);

        //아래쪽에 만들어둔 뷰홀더 클래스를 가져와서 적용시켜준다
        return new CustomViewHolder(view);
    }

    @Override
    //해당위치(position)의 뷰홀더에 내용을 연결시켜주는 메소드
    public void onBindViewHolder(@NonNull RvPlayerListAdapter.CustomViewHolder holder, int position) {
        holder.tvPlayerName.setText(rvPlayerDataList.get(position).getPlayerName());
        holder.tvPlayerBackNumber.setText(rvPlayerDataList.get(position).getPlayerBackNumber());
    }

    @Override
    //데이터를 관리하는 리스트의 총 크기(갯수)를 나타내는 메소드
    public int getItemCount() {
        return (null != rvPlayerDataList ? rvPlayerDataList.size() : 0);
    }

    //뷰홀더 클래스를 만듬 - 실제 뷰홀더에서 일어나는 작업들을 여기에서 만드는것
    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        //뷰홀더에 사용될것들을 만들어주고
        TextView tvPlayerName, tvPlayerBackNumber;

        CustomViewHolder(View itemView) {
            super(itemView);

            //id를 통해 매칭시켜준다
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvPlayerBackNumber = itemView.findViewById(R.id.tv_player_back_number);

            //OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정 - 아이템을 길게 클릭할때 편집, 삭제 기능을 추가하기 위해 사용함
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        //해당 아이템을 길게 클릭했을때 나타나는 기능
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            //컨텍스트 메뉴 2개를 생성
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");

            //아래에서 만든 onEditMenu를 붙여줌
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 컨텍스트 메뉴에서 항목 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  // 편집 항목을 선택시

                        //다이얼로그 생성
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        // 다이얼로그에 dialog_rv_player_list.xml일을 매칭
                        View view = LayoutInflater.from(context).inflate(R.layout.dialog_rv_player_list, null, false);
                        //매칭시킨 뷰를 다이얼로그에 붙여줌
                        builder.setView(view);
                        //다이얼로그 안에서 사용될 버튼과 텍스트입력란
                        final Button buttonSubmit = view.findViewById(R.id.btn_submit);
                        final EditText editPlayerName = view.findViewById(R.id.edit_player_name);
                        final EditText editPlayerBackNumber = view.findViewById(R.id.edit_player_back_number);
                        Log.i(TAG, "----case 1001 : 다이얼로그에 사용할 화면들 매칭시켜줌");

                        //해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                        editPlayerName.setText(rvPlayerDataList.get(getAdapterPosition()).getPlayerName());
                        editPlayerBackNumber.setText(rvPlayerDataList.get(getAdapterPosition()).getPlayerBackNumber());
                        Log.i(TAG, "----case 1001 편집 누르고 이름 불러오기 : " + rvPlayerDataList.get(0).getPlayerName());
                        Log.i(TAG, "----case 1001 편집 누르고 등번호 불러오기 : " + rvPlayerDataList.get(0).getPlayerBackNumber());

                        //해당부분 클릭시 텍스트를 전부 없애기
                        editPlayerName.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                editPlayerName.setText("");
                            }
                        });

                        editPlayerBackNumber.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                editPlayerBackNumber.setText("");
                            }
                        });

                        //다이얼로그 실제 생성
                        final AlertDialog dialog = builder.create();
                        Log.i(TAG, "----case 1001 : builder.create() 한부분");

                        //다이얼로그를 띄워서 선수 이름과 등번호에 대해 입력을 다 한 후에 수정 버튼을 클릭할때
                        buttonSubmit.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                //입력한것을 string에 잠시 저장
                                String playerName = editPlayerName.getText().toString();
                                String playerBackNumber = editPlayerBackNumber.getText().toString();

                                //임시로 사용할 string에 선수들 정보가 담긴 json데이터를 불러옴
                                String tempStr = PM.getString(context, "playerData");
                                Log.d(TAG, "!!@@ JSON데이터 String값 어댑터에서 불러오기 : " + tempStr);

                                try {
                                    //임시로 사용할 jsonArray에 불러온 string값을 넣어줌
                                    JSONArray tempArray = new JSONArray(tempStr);
                                    Log.d(TAG, "!!@@ JSON데이터 object에 넣기 : " + tempArray);

                                    //리사이클러뷰에서 클릭된 index를 getAdapterPosition()통해 가져와서 임시로 사용할 jsonObject에 추출
                                    //그 후에 수정하려고 입력한 데이터값들을 동일한 name(이름, 등번호)에 넣어줌
                                    JSONObject tempObject = tempArray.optJSONObject(getAdapterPosition());
                                    tempObject.put("이름", playerName);
                                    tempObject.put("등번호", playerBackNumber);

                                    //jsonArray에 해당위치에 다시 데이터들을 입력하고 string으로 변환한 후에 다시 쉐어드에 저장
                                    tempArray.put(getAdapterPosition(), tempObject);
                                    String result = tempArray.toString();
                                    PM.setString(context, "playerData", result);
                                    Log.d(TAG, "!!@@ JSON데이터 바꾼뒤에 다시 쉐어드에 저장 : " + PM.getString(context, "playerData"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                //data리스트의 해당위치 있는 데이터를 변경하고
                                RvPlayerListItem data = new RvPlayerListItem(playerName, "등번호 : " + playerBackNumber);
                                rvPlayerDataList.set(getAdapterPosition(), data);

                                //어댑터에서 RecyclerView에 반영
                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    // 삭제 항목 선택했을때
                    case 1002:

                        //이 부분을 바로 아래있는 리사이클러뷰의 데이터 삭제부분보다 밑에서 실행했었는데 삭제가 안됐었음
                        //그 이유가 이미 리사이클러뷰에서 삭제를 해버려서 index가 바뀌기 때문이였음 --> 그러므로 쉐어드에 저장된 데이터를 먼저 다루는게 맞는것 같음
                        //임시로 사용할 string에 선수들 정보가 담긴 json데이터를 불러옴
                        String tempStr = PM.getString(context, "playerData");
                        Log.d(TAG, "!!@@ JSON데이터 String값 어댑터에서 불러오기 : " + tempStr);
                        try {
                            //임시로 사용할 jsonArray에 불러온 string값을 넣어줌
                            JSONArray tempArray = new JSONArray(tempStr);
                            Log.d(TAG, "!!@@ JSON데이터 object에 넣기 : " + tempArray);

                            //리사이클러뷰에서 클릭된 index를 getAdapterPosition()통해 가져와서 해당 json데이터를 삭제
                            tempArray.remove(getAdapterPosition());
                            Log.d(TAG, "!!@@ JSON 데이터 제거하는 부분 remove() : " + tempArray);

                            //그 후 jsonArray를 다시 string에 넣어서 쉐어드에 저장
                            String result = tempArray.toString();
                            PM.setString(context, "playerData", result);
                            Log.d(TAG, "!!@@ JSON데이터 바꾼뒤에 다시 쉐어드에 저장 : " + PM.getString(context, "playerData"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //리스트에서 해당데이터 삭제
                        rvPlayerDataList.remove(getAdapterPosition());
                        //어댑터에서 해당데이터 삭제
                        notifyItemRemoved(getAdapterPosition());
                        // 어댑터 갱신
                        notifyItemRangeChanged(getAdapterPosition(), rvPlayerDataList.size());

                        break;

                }
                return true;
            }
        };
    }
}
