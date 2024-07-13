package com.megvii.idcardlib;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ResultActivity extends Activity {
    private ImageView mResultFaceImage;
    private ImageView mResultIdcardImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mResultFaceImage = (ImageView) findViewById(R.id.result_face_image);
        mResultIdcardImage = (ImageView) findViewById(R.id.result_idcard_image);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        byte[] portraitimg = bundle.getByteArray("portraitimg_bitmap");
        byte[] iDCardImg = bundle.getByteArray("idcardimg_bitmap");

        if (portraitimg != null) {
            Bitmap bmpPortraitImg = BitmapFactory.decodeByteArray(portraitimg, 0, portraitimg.length);
            mResultFaceImage.setImageBitmap(bmpPortraitImg);
        }

        if (iDCardImg != null) {
            Bitmap bmpIDCardImg = BitmapFactory.decodeByteArray(iDCardImg, 0, iDCardImg.length);
            mResultIdcardImage.setImageBitmap(bmpIDCardImg);
        }
    }


}