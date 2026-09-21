package com.hossein.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

public class PhotoAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Uri> photos;

    public PhotoAdapter(Context context, ArrayList<Uri> photos) {
        this.context = context;
        this.photos = photos;
    }

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
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView image;

        if (convertView == null) {
            image = new ImageView(context);
        } else {
            image = (ImageView) convertView;
        }

        int width = parent.getWidth() > 0
                ? parent.getWidth() / 3
                : 350;

        image.setLayoutParams(
                new ViewGroup.LayoutParams(width, width)
        );

        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        try {
            InputStream input =
                    context.getContentResolver()
                            .openInputStream(photos.get(position));

            Bitmap bitmap = BitmapFactory.decodeStream(input);

            if (input != null) {
                input.close();
            }

            image.setImageBitmap(bitmap);

        } catch (Exception e) {
            image.setImageResource(
                    android.R.drawable.ic_menu_report_image
            );
        }

        return image;
    }
}
