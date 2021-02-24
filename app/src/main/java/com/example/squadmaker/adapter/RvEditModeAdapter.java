package com.example.squadmaker.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.squadmaker.data.RvEditModeItem;
import com.example.squadmaker.data.RvPlayerListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RvEditModeAdapter extends RecyclerView.Adapter<RvEditModeAdapter.CustomViewHolder> {

    String TAG = "RvEditModeAdapter";

    public ArrayList<RvEditModeItem> rvPlayerDataList;
    private Context context;

    //어댑터 외부에서 아이템클릭리스너를 만들어주기 위해서 만든 인터페이스
    //인터페이스는 해당 메소드의 이름만 가지고있기때문에 필요한 부분에서 메소드의 내용을 꼭 입력해야함
    public interface OnItemClickListener  {
        void onItemClick(View v, int position) throws JSONException;
    }

    //뷰홀더를 만드는 부분에서 이 아이템 클릭 리스너를 포함새켜줘야함
    private OnItemClickListener listener = null;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    //생성자를 통해서 데이터를 저장할 arraylist를 지정함
    public RvEditModeAdapter(Context context, ArrayList<RvEditModeItem> list) {
        this.context = context;
        rvPlayerDataList = list;
    }

    @NonNull
    @Override
    //뷰홀더에 사용될 xml파일을 지정해주는 메소드
    public RvEditModeAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //xml파일 자체를 인플레이트 해준다
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_edit_mode_popup, parent, false);

        //아래쪽에 만들어둔 뷰홀더 클래스를 가져와서 적용시켜준다
        return new CustomViewHolder(view);
    }

    @Override
    //해당위치(position)의 뷰홀더에 내용을 연결시켜주는 메소드
    public void onBindViewHolder(@NonNull RvEditModeAdapter.CustomViewHolder holder, int position) {
        holder.tvPlayerName.setText(rvPlayerDataList.get(position).getPlayerName());
        holder.tvPlayerBackNumber.setText(rvPlayerDataList.get(position).getPlayerBackNumber());
    }

    @Override
    //데이터를 관리하는 리스트의 총 크기(갯수)를 나타내는 메소드
    public int getItemCount() {
        return (null != rvPlayerDataList ? rvPlayerDataList.size() : 0);
    }

    //뷰홀더 클래스를 만듬 - 실제 뷰홀더에서 일어나는 작업들을 여기에서 만드는것
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        //뷰홀더에 사용될것들을 만들어주고
        TextView tvPlayerName, tvPlayerBackNumber;

        CustomViewHolder(View itemView) {
            super(itemView);

            //id를 통해 매칭시켜준다
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvPlayerBackNumber = itemView.findViewById(R.id.tv_player_back_number);

            //리사이클러뷰의 아이템에 클릭리스너를 붙여서 해당 아이템의 정보를 FormationActivity로 다시 보내주려고 함
            //이부분이 EditModeActiviy에서 아이템클릭 리스너로 사용되는부분
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                    {
                        // 리스너 객체의 메서드 호출.
                        if (listener != null)
                        {
                            try {
                                listener.onItemClick(v, position);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }
}
