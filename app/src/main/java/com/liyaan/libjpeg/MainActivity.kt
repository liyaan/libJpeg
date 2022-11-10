package com.liyaan.libjpeg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


class MainActivity : AppCompatActivity() {
    private val inputString:String by lazy {
        val input = File(Environment.getExternalStorageDirectory(), "jett.jpeg")
        input.absolutePath
    }
    private val inputBitmap:Bitmap by lazy {

        BitmapFactory.decodeFile(inputString)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        findViewById<TextView>(R.id.sample_text).text = stringFromJNI()
        findViewById<AppCompatImageView>(R.id.image)
            .setImageBitmap(inputBitmap)
        findViewById<AppCompatImageView>(R.id.image).setOnClickListener {
            Log.i("aaaaaaa","aaaaaaaaaaaaaaaaaaaaa")
            nativeCompress(inputBitmap, 50, inputString)
            Log.i("aaaaaaa","bbbbbbbbbbbbbbbbbbbbb")
            Toast.makeText(this, "执行完成", Toast.LENGTH_SHORT).show();
        }
    }
    external fun stringFromJNI(): String
    external fun nativeCompress(bitmap: Bitmap, q: Int, path: String)
    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                writeFile()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + this.packageName)
                startActivityForResult(intent, 1)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                writeFile()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    1
                )
            }
        } else {
            writeFile()
        }
    }

    /**
     * 模拟文件写入
     */
    private fun writeFile() {
        Log.i("aaaaa","写入文件成功")
    }
}