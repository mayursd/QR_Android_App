package com.example.mayur

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.otaliastudios.cameraview.frame.Frame
import kotlinx.android.synthetic.main.scan.*

class Scan : AppCompatActivity() {

    internal var isDetected = false
    lateinit var options: FirebaseVisionBarcodeDetectorOptions
    lateinit var detector: FirebaseVisionBarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan)

        Dexter.withActivity(this@Scan)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    setupCamera()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    TODO("Not yet implemented")
                }

            }).check()


    }

    private fun setupCamera() {
        options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
            .build()
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        //btn_again.isEnabled = isDetected
        btn_again.setOnClickListener {
            isDetected = !isDetected
            btn_again.isEnabled = isDetected
        }

        cameraview.setLifecycleOwner(this)
        cameraview.addFrameProcessor { frame -> processImage(getVisionImageFromFrame(frame)) }
    }

    private fun processImage(image : FirebaseVisionImage) {
        if (!isDetected)
            detector.detectInImage(image)
                .addOnFailureListener{ e -> Toast.makeText(this@Scan,""+e.message, Toast.LENGTH_SHORT).show()}
                .addOnSuccessListener { firebaseVisionBarcodes ->
                    processResult(firebaseVisionBarcodes)
                }
    }

    private fun processResult(firebaseVisionBarcodes: List<FirebaseVisionBarcode>) {
        if (firebaseVisionBarcodes.size >0)
        {
            isDetected = true
            btn_again.isEnabled = isDetected
            for (item in firebaseVisionBarcodes)
            {
                val value_type = item.valueType
                when(value_type)
                {
                    FirebaseVisionBarcode.TYPE_TEXT -> {
                        createDialog(item.rawValue)
                    }
                    FirebaseVisionBarcode.TYPE_CONTACT_INFO -> {
                        val info = StringBuilder("Name: ")
                            .append(item.contactInfo!!.name!!.formattedName)
                            .append("\n")
                            .append("Address: ")
                            .append(item.contactInfo!!.addresses[0].addressLines[0])
                            .append("\n")
                            .append("Emai: ")
                            .append(item.contactInfo!!.emails[0].address)
                            .toString()
                        createDialog(info)
                    }
                    FirebaseVisionBarcode.TYPE_URL -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.rawValue))
                        startActivity(intent)
                    }

                }
            }
        }
    }

    private fun createDialog(text: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(text)
            .setPositiveButton("Ok",{dialogInterface , i -> dialogInterface.dismiss()})
        val dialog = builder.create()
        dialog.show()

    }

    private fun getVisionImageFromFrame(frame: Frame): FirebaseVisionImage {
        val data = frame.data
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setHeight(frame.size.height)
            .setWidth(frame.size.width)
            .build()
        return FirebaseVisionImage.fromByteArray(data,metadata)

    }

}