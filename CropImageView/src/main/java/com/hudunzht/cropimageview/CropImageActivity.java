package com.hudunzht.cropimageview;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

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
    }

//    private static final int ALBUM_REQUEST_CODE = 1;
//
//    public static void openAlbum(Activity activity) {
//        Intent intent = new Intent(Intent.ACTION_PICK, null);
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
//    }

}
