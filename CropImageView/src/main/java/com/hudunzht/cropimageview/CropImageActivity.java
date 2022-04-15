package com.hudunzht.cropimageview;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * author: ZHT
 * date: 2022/4/13
 * desc:
 */
public class CropImageActivity extends AppCompatActivity {

//    public CropImageActivity() {
//        super(R.layout.activity_crop_image);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        init();
    }

    private void init(){
        ConstraintLayout layout = findViewById(R.id.activity_crop_image);
        CropImageView view = new CropImageView(this,null,0);
        view.invalidate();
        layout.addView(view);
    }

//    private static final int ALBUM_REQUEST_CODE = 1;
//
//    public static void openAlbum(Activity activity) {
//        Intent intent = new Intent(Intent.ACTION_PICK, null);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
//    }

}
