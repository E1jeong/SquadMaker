package com.example.squadmaker.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

public class RvFormationImageItem {

    private Bitmap formationImage;

    public Bitmap getFormationImage() {
        return formationImage;
    }

    public RvFormationImageItem (Bitmap formationImage) {
        //this.formationImage = Uri.parse("file:///" + Environment.getExternalStorageDirectory().getAbsolutePath() + formationImage);
        this.formationImage = formationImage;
    }
}
