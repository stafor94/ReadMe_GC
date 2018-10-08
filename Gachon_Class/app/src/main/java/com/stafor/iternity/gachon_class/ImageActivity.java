package com.stafor.iternity.gachon_class;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = (ImageView) findViewById(R.id.imageView);

        // 인텐트를 전달받아서 이미지를 설정
        Intent myIntent = getIntent();
        imageView.setImageResource(myIntent.getIntExtra("img", 0));

        // 이미지뷰 핀치줌, 확대, 축소
        PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
    }
}
