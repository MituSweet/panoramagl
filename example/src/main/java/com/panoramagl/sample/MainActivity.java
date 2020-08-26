package com.panoramagl.sample;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private PLManager plManager;
    private int currentIndex = -1;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plManager = new PLManager(this);
        plManager.setContentView((ViewGroup) findViewById(R.id.content_view));
        plManager.onCreate();

        plManager.setAccelerometerEnabled(false);
        plManager.setInertiaEnabled(false);
        plManager.setZoomEnabled(false);
        changePanorama();
    }

    @Override
    protected void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    protected void onPause() {
        plManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return plManager.onTouchEvent(event);
    }

    private void changePanorama() {
        final PLSphericalPanorama panorama = new PLSphericalPanorama();
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this)
                .asBitmap()
                .load("https://img.cdn.zhaoshang800.com/image/2020/07/14/f83a53b9-4f3b-40ea-b4a3-6844c43ec5d0.jpg")
                .apply(options).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                panorama.setImage(new PLImage(mBitmap, false));
                float pitch = 5f;
                final float[] yaw = {0f};
                float zoomFactor = 0.8f;

                if (currentIndex != -1) {
                    PLICamera camera = plManager.getPanorama().getCamera();
                    pitch = camera.getPitch();
                    yaw[0] = camera.getYaw();
                    zoomFactor = camera.getZoomFactor();
                }

                panorama.getCamera().lookAtAndZoomFactor(pitch, yaw[0], zoomFactor, false);
                plManager.setPanorama(panorama);
                plManager.setAutoPlay();
            }
        });
    }
}
