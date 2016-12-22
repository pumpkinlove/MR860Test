package com.miaxis.mr860test.domain;

import android.graphics.Bitmap;

/**
 * Created by xu.nan on 2016/12/22.
 */

public class IdCardPhotoEvent {
    private Bitmap bitmap;

    public IdCardPhotoEvent() {
    }

    public IdCardPhotoEvent(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
