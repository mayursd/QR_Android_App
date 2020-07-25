package com.example.mayur

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.PathUtils
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.IllegalArgumentException

private const val FILE_PICKER_ID = 12
private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity() , View.OnClickListener {

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.Gen -> {
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(
                    text1.text.toString(),
                    BarcodeFormat.QR_CODE,
                    1600,
                    1600

                )

                QR.setImageBitmap(bitmap)

            }
        }
    }
    lateinit var intentIntegrator : IntentIntegrator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intentIntegrator = IntentIntegrator(this)
        Gen.setOnClickListener(this)
        fbutton.setOnClickListener{
            startActivity(Intent(this,Scan::class.java))
        }
    }

}
