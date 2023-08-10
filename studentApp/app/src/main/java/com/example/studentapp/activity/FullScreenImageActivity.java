package com.example.studentapp.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.studentapp.R;

import android.widget.ImageView;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        String imageUrl = getIntent().getStringExtra("image_url");

        ImageView imageView = findViewById(R.id.imageViewFullScreen);
        Glide.with(this)
                .load(imageUrl)
                .into(imageView);
    }
}
