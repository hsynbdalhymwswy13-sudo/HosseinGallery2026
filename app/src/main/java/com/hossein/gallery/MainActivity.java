package com.hossein.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("گالری حسین ❤️");
        title.setTextSize(28);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);

        Button galleryButton = new Button(this);
        galleryButton.setText("🖼️ گالری");

        Button albumButton = new Button(this);
        albumButton.setText("📁 آلبوم‌ها");

        Button selectButton = new Button(this);
        selectButton.setText("➕ انتخاب عکس");

        Button deleteButton = new Button(this);
        deleteButton.setText("🗑️ حذف عکس");

        Button backButton = new Button(this);
        backButton.setText("↩️ برگشت");

        imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);

        layout.addView(title);
        layout.addView(galleryButton);
        layout.addView(albumButton);
        layout.addView(selectButton);
        layout.addView(deleteButton);
        layout.addView(backButton);
        layout.addView(imageView);

        setContentView(layout);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 100);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            imageView.setImageURI(data.getData());
        }
    }
}
