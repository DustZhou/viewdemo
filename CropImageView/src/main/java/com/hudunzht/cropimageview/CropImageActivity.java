package com.hudunzht.cropimageview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author: ZHT
 * date: 2022/4/13
 * desc:
 */
public class CropImageActivity extends AppCompatActivity {
    private static final int ALBUM_REQUEST_CODE = 1;
    private CropImageView cropImageView;
    private Button btnCrop;
    private Button btnCancel;
    private String CROP_IMAGE_PATH = "";
    protected Bitmap tupian = null;
    private ImageView img_crop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        openAlbum(this);
        Log.e("zht3","uri" +CROP_IMAGE_PATH);
        init();
    }

    private void init() {
        img_crop=findViewById(R.id.img_crop);
        cropImageView = (CropImageView) findViewById(R.id.crop_image);
        btnCrop = (Button) findViewById(R.id.btn_crop);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
//        CROP_IMAGE_PATH = getIntent().getStringExtra("image/*");
        CROP_IMAGE_PATH=getExternalCacheDir()+File.separator+"SDA";


        //裁剪保存
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取bitmap
                Bitmap cropImage = cropImageView.getCroImage();
                img_crop.setImageBitmap(cropImage);
                //文件存储路径
                File file = new File(CROP_IMAGE_PATH);
                //判断文件夹路径是否存在
                if (!file.exists()) {
                    file.mkdirs();
                }
                String dstPath=file.getAbsolutePath()+ File.separator;
                File imgFile=new File(dstPath,"image.png");
                try {
                    //保存操作
                    FileOutputStream saveImgOut = new FileOutputStream(imgFile);
                    cropImage.compress(Bitmap.CompressFormat.PNG, 100, saveImgOut);
                    saveImgOut.flush();
                    saveImgOut.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ex.getMessage();
                    Log.e("mrchen", ex.getMessage());
                }
            }
        });
    }
    public static void openAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        //imageview设置图片
        try {
            cropImageView.setImageBitmap(BitmapFactory.decodeStream(getContentResolver().openInputStream(uri)));
            Bitmap tupian  = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            cropImageView.getBitmapOri(tupian);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("相册获取");
    }
}