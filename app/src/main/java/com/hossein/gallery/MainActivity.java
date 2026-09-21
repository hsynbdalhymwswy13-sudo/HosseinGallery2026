package com.hossein.gallery;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends Activity {

    GridView gridView;
    ArrayList<Uri> photos = new ArrayList<>();
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                10
            );
        } else {
            requestPermissions(
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                10
            );
        }

        createGallery();
    }

    void createGallery() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("گالری حسین");
        title.setTextSize(26);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 30, 10, 25);

        gridView = new GridView(this);
        gridView.setNumColumns(3);
        gridView.setVerticalSpacing(4);
        gridView.setHorizontalSpacing(4);
        gridView.setPadding(4, 4, 4, 4);

        root.addView(title);
        root.addView(gridView,
                new LinearLayout.LayoutParams(
                        -1,
                        0,
                        1
                ));

        setContentView(root);

        loadPhotos();
    }

    void loadPhotos() {

        photos.clear();

        String[] projection = {
                MediaStore.Images.Media._ID
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {

            int idColumn = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media._ID
            );

            while (cursor.moveToNext()) {

                long id = cursor.getLong(idColumn);

                Uri photoUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        String.valueOf(id)
                );

                photos.add(photoUri);
            }

            cursor.close();
        }

        adapter = new ImageAdapter();
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        showPhoto(position);
                    }
                }
        );
    }

    void showPhoto(int position) {

        ImageView image = new ImageView(this);
        image.setImageURI(photos.get(position));
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setBackgroundColor(Color.BLACK);

        setContentView(image);

        image.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createGallery();
                    }
                }
        );
    }

    class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(
                int position,
                View convertView,
                android.view.ViewGroup parent) {

            ImageView image;

            if (convertView == null) {
                image = new ImageView(MainActivity.this);
            } else {
                image = (ImageView) convertView;
            }

            int size = parent.getWidth() / 3;

            image.setLayoutParams(
                    new AbsListView.LayoutParams(size, size)
            );

            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.setImageURI(photos.get(position));

            return image;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == 10) {
            loadPhotos();
        }
    }
}
