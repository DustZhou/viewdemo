package com.hudunzht.cropimageview;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * author: ZHT
 * date: 2022/4/13
 * desc:
 */
public class OpenAlbumActivity{
    private static final int ALBUM_REQUEST_CODE = 1;


    public static void openAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }
}
