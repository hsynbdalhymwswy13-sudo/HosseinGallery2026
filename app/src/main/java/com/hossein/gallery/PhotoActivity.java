package com.hossein.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotoActivity extends Activity {

    private ImageView imageView;
    private ScaleGestureDetector scaleDetector;
    private float scale = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getParcelableExtra("photo_uri");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);

        TextView top = new TextView(this);
        top.setText("‹  گالری حسین");
        top.setTextColor(Color.WHITE);
        top.setTextSize(20);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(20, 20, 20, 20);

        imageView = new ImageView(this);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (uri != null) {
            imageView.setImageURI(uri);
        }

        root.addView(top, new LinearLayout.LayoutParams(
                -1, 70
        ));

        root.addView(imageView, new LinearLayout.LayoutParams(
                -1, 0, 1
        ));

        setContentView(root);

        top.setOnClickListener(v -> finish());

        scaleDetector = new ScaleGestureDetector(
                this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                    @Override
                    public boolean onScale(
                            ScaleGestureDetector detector) {

                        scale *= detector.getScaleFactor();

                        if (scale < 1.0f) {
                            scale = 1.0f;
                        }

                        if (scale > 5.0f) {
                            scale = 5.0f;
                        }

                        imageView.setScaleX(scale);
                        imageView.setScaleY(scale);

                        return true;
                    }
                }
        );

        imageView.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            return true;
        });
    }
}
