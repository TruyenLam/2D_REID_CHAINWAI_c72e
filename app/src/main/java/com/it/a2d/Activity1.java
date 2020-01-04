package com.it.a2d;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Administrator on 2018/6/11.
 */

public class Activity1 extends AppCompatActivity {

    boolean camflag=false;
    boolean memflag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        if (Build.VERSION.SDK_INT > 21) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(Activity1.this,
                        new String[]{android.Manifest.permission.CAMERA}, 1);

            } else {
                //说明已经获取到摄像头权限了 想干嘛干嘛
                camflag=true;
            }
            //读写内存权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 请求权限
                ActivityCompat
                        .requestPermissions(
                                this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                1);
            }

            int checkCallPhonePermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,}, 1);
                return;
            } else {
                // 已申请权限直接跳转到下一个界面
                memflag=true;

            }
            if(camflag && memflag)
            {
                Intent intent = new Intent(Activity1.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(this,"PERMISSION Fail", Toast.LENGTH_SHORT).show();

            }

        } else {
            //这个说明系统版本在6.0之下，不需要动态获取权限。
            Intent intent = new Intent(Activity1.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }


    @SuppressLint("NewApi")
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 1) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(permissions[0].equals("android.permission.WRITE_EXTERNAL_STORAGE"))
                {
                    memflag=true;
                }
                else if(permissions[0].equals("android.permission.CAMERA"))
                {
                    camflag=true;
                }

                // 申请成功，可以拍照

            } else {
                // Toast.makeText(this,
                // "CAMERA PERMISSION DENIED",Toast.LENGTH_SHORT).show();
            }
            if(camflag && memflag){
                Intent intent = new Intent(Activity1.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
