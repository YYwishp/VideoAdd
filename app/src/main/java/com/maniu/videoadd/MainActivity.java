package com.maniu.videoadd;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    public void add(View view) {
        final String videoPath= new File(Environment.getExternalStorageDirectory(), "input.mp4").getAbsolutePath();
        final String videoPath1 = new File(Environment.getExternalStorageDirectory(), "input2.mp4").getAbsolutePath();
        final String outPath = new File(Environment.getExternalStorageDirectory(), "outPath.mp4").getAbsolutePath();
        new Thread() {
            @Override
            public void run() {

                try {
                    copyAssets("input.mp4", videoPath);
                    copyAssets("input2.mp4", videoPath1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    VideoProcess.appendVideo( videoPath1,videoPath, outPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MainActivity.this, "合并完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();


    }
    public   boolean checkPermission(
           ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
           requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

        }
        return false;
    }


    private void copyAssets(String assetsName, String path) throws IOException {
        AssetFileDescriptor assetFileDescriptor = getAssets().openFd(assetsName);
        FileChannel from = new FileInputStream(assetFileDescriptor.getFileDescriptor()).getChannel();
        FileChannel to = new FileOutputStream(path).getChannel();
        from.transferTo(assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength(), to);
    }
}