package com.hudunzht.cropimageview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: ZHT
 * date: 2022/4/20
 * desc:正方形裁剪 SquareCropImageActivity
 */
public class SquareCropImageActivity extends AppCompatActivity {
    private static final int ALBUM_REQUEST_CODE = 1;
    private SquareCropImageView squareCropImageView;
    private Button btnCrop;
    private Button btnCancel;
    private String CROP_IMAGE_PATH = "";
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_square_crop_image);
        openAlbum(this);
        init();
    }


    private void init() {
        //初始化控件
        squareCropImageView = (SquareCropImageView) findViewById(R.id.square_crop_Image);
        btnCrop = (Button) findViewById(R.id.btn_crop);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        //保存路径
        CROP_IMAGE_PATH = getExternalCacheDir() + File.separator + "SDA";
        //上下文
        context = getApplicationContext();

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取bitmap
//                Bitmap cropImage = cropImageView.getCroImage();
                //文件存储路径
                File file = new File(CROP_IMAGE_PATH);
                //判断文件夹路径是否存在
                if (!file.exists()) {
                    file.mkdirs();
                }
                String dstPath = file.getAbsolutePath() + File.separator;
                File imgFile = new File(dstPath, "image.png");
                try {
                    //保存操作
                    FileOutputStream saveImgOut = new FileOutputStream(imgFile);
//                    cropImage.compress(Bitmap.CompressFormat.PNG, 100, saveImgOut);
                    saveImgOut.flush();
                    saveImgOut.close();
                    Toast.makeText(context, "裁剪成功！", Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ex.getMessage();
                    Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //取消裁剪
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        try {
            squareCropImageView.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(uri)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 调用系统相册。在onActivityResult返回。
     *
     * @param activity
     */
    public static void openAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }
}
