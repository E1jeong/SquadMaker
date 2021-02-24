package com.example.squadmaker.adapter;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.squadmaker.PreferenceManager;
import com.example.squadmaker.R;
import com.example.squadmaker.data.RvFormationImageItem;
import com.example.squadmaker.data.RvPlayerListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RvFormationImageAdapter extends RecyclerView.Adapter<RvFormationImageAdapter.ViewHolder> {

    private String TAG = "RvFormationImageAdapter";
    public ArrayList<RvFormationImageItem> rvImageDataList;
    private Context context;

    PreferenceManager PM = new PreferenceManager();


    //FormationActivity에서 리사이클러뷰에 아이템클릭리스너를 작동시키기 위해 만들어준 인터페이스
    //인터페이스는 해당 메소드의 이름만 가지고있기때문에 필요한 부분에서 메소드의 내용을 꼭 입력해야함
    public interface OnItemClickListener  {
        void onItemClick(View v, int position) throws JSONException;
    }

    //뷰홀더를 만드는 부분에서 이 아이템 클릭 리스너를 포함새켜줘야함
    private RvFormationImageAdapter.OnItemClickListener listener = null;
    public void setOnItemClickListener(RvFormationImageAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }



    public RvFormationImageAdapter (Context context, ArrayList<RvFormationImageItem> list) {
        this.context = context;
        rvImageDataList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_formation_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivFormationImage.setImageBitmap(rvImageDataList.get(position).getFormationImage());
    }

    @Override
    public int getItemCount() {
        return rvImageDataList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        ImageView ivFormationImage;

        ViewHolder(View itemView) {
            super(itemView);

            ivFormationImage = itemView.findViewById(R.id.iv_formation_image);

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

            //OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정 - 아이템을 길게 클릭할때 편집, 삭제 기능을 추가하기 위해 사용함
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        //해당 아이템을 길게 클릭했을때 나타나는 기능
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            //컨텍스트 메뉴 생성
            MenuItem delete = menu.add(Menu.NONE, 1001, 1, "제거하기");

            //아래에서 만든 onEditMenu를 붙여줌
            delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 컨텍스트 메뉴에서 항목 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  // 제거하기 항목을 선택시

                        //이 부분을 바로 아래있는 리사이클러뷰의 데이터 삭제부분보다 밑에서 실행했었는데 삭제가 안됐었음
                        //그 이유가 이미 리사이클러뷰에서 삭제를 해버려서 index가 바뀌기 때문이였음 --> 그러므로 쉐어드에 저장된 데이터를 먼저 다루는게 맞는것 같음
                        //임시로 사용할 string에 선수들 정보가 담긴 json데이터를 불러옴
                        String tempStr = PM.getString(context, "formationData");
                        try {
                            //임시로 사용할 jsonArray에 불러온 string값을 넣어줌
                            JSONArray tempArray = new JSONArray(tempStr);

                            //리사이클러뷰에서 클릭된 index를 getAdapterPosition()통해 가져와서 해당 json데이터를 삭제
                            tempArray.remove(getAdapterPosition());

                            //그 후 jsonArray를 다시 string에 넣어서 쉐어드에 저장
                            String result = tempArray.toString();
                            PM.setString(context, "formationData", result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //리스트에서 해당데이터 삭제
                        rvImageDataList.remove(getAdapterPosition());
                        //어댑터에서 해당데이터 삭제
                        notifyItemRemoved(getAdapterPosition());
                        // 어댑터 갱신
                        notifyItemRangeChanged(getAdapterPosition(), rvImageDataList.size());

                        break;

                }
                return true;
            }
        };
    }
}
