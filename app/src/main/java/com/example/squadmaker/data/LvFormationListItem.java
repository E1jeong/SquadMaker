package com.example.squadmaker.data;

//FormationListActivity의 리스트뷰에 사용될 데이터
public class LvFormationListItem {

    //이 리스트뷰에서만 사용할 데이터 저장 공간
    private String formation;

    //생성자를 통해 데이터 저장
    public LvFormationListItem(String formation) {
        this.formation = formation;
    }

//    public void setFormation(String formation) {
//        this.formation = formation;
//    }

    //데이터에 저장된 값을 가져오는 메소드
    public String getFormation() {
        return formation;
    }

}
