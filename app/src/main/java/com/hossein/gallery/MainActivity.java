package com.hossein.gallery;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    private GridView grid;
    private final ArrayList<Uri> photos = new ArrayList<>();
    private SharedPreferences prefs;

    private int columns = 3;
    private String currentAlbum = "";
    private String currentSearch = "";
    private int sortMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("gallery", MODE_PRIVATE);

        if (hasPermission()) {
            buildHome();
        } else {
            requestPermission();
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            return checkSelfPermission(
                    Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED;
        }

        return checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    100
            );
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] results
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                results
        );

        if (requestCode == 100 && hasPermission()) {
            buildHome();
        }
    }

    private void buildHome() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(15, 15, 15, 5);

        TextView title = new TextView(this);
        title.setText("گالری حسین");
        title.setTextSize(25);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);

        header.addView(
                title,
                new LinearLayout.LayoutParams(0, 65, 1)
        );

        TextView search = makeButton("🔍");
        TextView settings = makeButton("⚙");

        header.addView(search, new LinearLayout.LayoutParams(60, 60));
        header.addView(settings, new LinearLayout.LayoutParams(60, 60));

        root.addView(header);

        EditText searchBox = new EditText(this);
        searchBox.setHint("جست‌وجوی عکس...");
        searchBox.setSingleLine(true);
        searchBox.setVisibility(View.GONE);

        root.addView(
                searchBox,
                new LinearLayout.LayoutParams(-1, 60)
        );

        LinearLayout menu = new LinearLayout(this);
        menu.setGravity(Gravity.CENTER);

        TextView gallery = makeMenu("🖼️ گالری");
        TextView albums = makeMenu("📁 آلبوم‌ها");
        TextView favorites = makeMenu("❤️ علاقه‌مندی");
        TextView trash = makeMenu("🗑️ سطل");

        menu.addView(gallery);
        menu.addView(albums);
        menu.addView(favorites);
        menu.addView(trash);

        root.addView(menu);

        LinearLayout tools = new LinearLayout(this);
        tools.setGravity(Gravity.CENTER_VERTICAL);

        TextView count = new TextView(this);
        count.setText("عکس‌ها");
        count.setTextSize(15);
        count.setGravity(Gravity.CENTER_VERTICAL);

        tools.addView(
                count,
                new LinearLayout.LayoutParams(0, 55, 1)
        );

        TextView sort = makeButton("↕ مرتب‌سازی");
        TextView gridButton = makeButton("▦");

        tools.addView(
                sort,
                new LinearLayout.LayoutParams(130, 55)
        );

        tools.addView(
                gridButton,
                new LinearLayout.LayoutParams(60, 55)
        );

        root.addView(tools);

        grid = new GridView(this);
        grid.setNumColumns(columns);
        grid.setHorizontalSpacing(3);
        grid.setVerticalSpacing(3);
        grid.setPadding(3, 3, 3, 3);

        root.addView(
                grid,
                new LinearLayout.LayoutParams(-1, 0, 1)
        );

        setContentView(root);

        loadPhotos();

        search.setOnClickListener(v -> {
            if (searchBox.getVisibility() == View.GONE) {
                searchBox.setVisibility(View.VISIBLE);
                searchBox.requestFocus();
            } else {
                searchBox.setVisibility(View.GONE);
                searchBox.setText("");
                currentSearch = "";
                loadPhotos();
            }
        });

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            currentSearch = searchBox.getText().toString().trim();
            loadPhotos();
            return false;
        });

        gallery.setOnClickListener(v -> {
            currentAlbum = "";
            loadPhotos();
        });

        favorites.setOnClickListener(v -> {
            currentAlbum = "__FAVORITES__";
            loadPhotos();
        });

        trash.setOnClickListener(v -> {
            currentAlbum = "__TRASH__";
            loadPhotos();
        });

        albums.setOnClickListener(v -> showAlbums());

        sort.setOnClickListener(v -> showSortMenu(sort));

        gridButton.setOnClickListener(v -> {
            columns = columns == 3 ? 4 : 3;
            grid.setNumColumns(columns);
            loadPhotos();
        });

        settings.setOnClickListener(v -> showSettings());

        grid.setOnItemClickListener(
                (parent, view, position, id) ->
                        openPhoto(position)
        );

        grid.setOnItemLongClickListener(
                (parent, view, position, id) -> {
                    showPhotoMenu(position);
                    return true;
                }
        );
    }

    private TextView makeButton(String text) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(16);
        v.setTextColor(Color.BLACK);
        v.setGravity(Gravity.CENTER);
        return v;
    }

    private TextView makeMenu(String text) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(13);
        v.setTextColor(Color.BLACK);
        v.setGravity(Gravity.CENTER);
        v.setPadding(5, 5, 5, 5);
        v.setLayoutParams(
                new LinearLayout.LayoutParams(0, 70, 1)
        );
        return v;
    }

    private void loadPhotos() {

        photos.clear();

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
        };

        String order;

        if (sortMode == 1) {
            order = MediaStore.Images.Media.DISPLAY_NAME + " ASC";
        } else if (sortMode == 2) {
            order = MediaStore.Images.Media.SIZE + " DESC";
        } else {
            order = MediaStore.Images.Media.DATE_ADDED + " DESC";
        }

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                order
        );

        if (cursor == null) {
            grid.setAdapter(new GalleryAdapter());
            return;
        }

        int idColumn = cursor.getColumnIndex(
                MediaStore.Images.Media._ID
        );

        int nameColumn = cursor.getColumnIndex(
                MediaStore.Images.Media.DISPLAY_NAME
        );

        int albumColumn = cursor.getColumnIndex(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        );

        Set<String> favorites = new HashSet<>(
                prefs.getStringSet(
                        "favorites",
                        new HashSet<>()
                )
        );

        Set<String> trash = new HashSet<>(
                prefs.getStringSet(
                        "trash",
                        new HashSet<>()
                )
        );

        while (cursor.moveToNext()) {

            long id = cursor.getLong(idColumn);

            Uri uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
            );

            String name = cursor.getString(nameColumn);
            String album = cursor.getString(albumColumn);

            if (album == null) {
                album = "سایر";
            }

            String value = uri.toString();

            if (currentAlbum.equals("__TRASH__")) {

                if (!trash.contains(value)) {
                    continue;
                }

            } else {

                if (trash.contains(value)) {
                    continue;
                }

                if (currentAlbum.equals("__FAVORITES__")
                        && !favorites.contains(value)) {
                    continue;
                }

                if (!currentAlbum.isEmpty()
                        && !currentAlbum.equals("__FAVORITES__")
                        && !currentAlbum.equals(album)) {
                    continue;
                }
            }

            if (!currentSearch.isEmpty()
                    && (name == null
                    || !name.toLowerCase()
                    .contains(currentSearch.toLowerCase()))) {
                continue;
            }

            photos.add(uri);
        }

        cursor.close();

        grid.setAdapter(new GalleryAdapter());
    }

    private void showAlbums() {

        ArrayList<String> list = new ArrayList<>();

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                },
                null,
                null,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " ASC"
        );

        if (cursor != null) {

            while (cursor.moveToNext()) {

                String name = cursor.getString(0);

                if (name == null) {
                    name = "سایر";
                }

                if (!list.contains(name)) {
                    list.add(name);
                }
            }

            cursor.close();
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(25, 25, 25, 25);

        TextView title = new TextView(this);
        title.setText("📁 آلبوم‌ها");
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        for (String album : list) {

            TextView item = new TextView(this);
            item.setText("📁  " + album);
            item.setTextSize(19);
            item.setPadding(20, 25, 20, 25);

            layout.addView(item);

            String selected = album;

            item.setOnClickListener(v -> {
                currentAlbum = selected;
                buildHome();
            });
        }

        TextView back = new TextView(this);
        back.setText("← برگشت");
        back.setTextSize(20);
        back.setGravity(Gravity.CENTER);
        back.setPadding(20, 30, 20, 30);

        layout.addView(back);

        setContentView(layout);

        back.setOnClickListener(v -> buildHome());
    }

    private void showSortMenu(View anchor) {

        PopupMenu menu = new PopupMenu(this, anchor);

        menu.getMenu().add("جدیدترین");
        menu.getMenu().add("نام");
        menu.getMenu().add("حجم");

        menu.setOnMenuItemClickListener(item -> {

            String text = item.getTitle().toString();

            if (text.equals("نام")) {
                sortMode = 1;
            } else if (text.equals("حجم")) {
                sortMode = 2;
            } else {
                sortMode = 0;
            }

            loadPhotos();
            return true;
        });

        menu.show();
    }

    private void showPhotoMenu(int position) {

        Uri uri = photos.get(position);

        PopupMenu menu = new PopupMenu(this, grid);

        menu.getMenu().add("❤️ علاقه‌مندی");
        menu.getMenu().add("📤 اشتراک‌گذاری");
        menu.getMenu().add("🗑️ انتقال به سطل");
        menu.getMenu().add("ℹ️ اطلاعات");

        menu.setOnMenuItemClickListener(item -> {

            String text = item.getTitle().toString();

            if (text.contains("علاقه")) {
                toggleFavorite(uri);
            } else if (text.contains("اشتراک")) {
                sharePhoto(uri);
            } else if (text.contains("سطل")) {
                moveToTrash(uri);
            } else {
                showDetails(uri);
            }

            return true;
        });

        menu.show();
    }

    private void toggleFavorite(Uri uri) {

        Set<String> set = new HashSet<>(
                prefs.getStringSet(
                        "favorites",
                        new HashSet<>()
                )
        );

        String value = uri.toString();

        if (set.contains(value)) {
            set.remove(value);
        } else {
            set.add(value);
        }

        prefs.edit()
                .putStringSet("favorites", set)
                .apply();

        loadPhotos();
    }

    private void moveToTrash(Uri uri) {

        Set<String> set = new HashSet<>(
                prefs.getStringSet(
                        "trash",
                        new HashSet<>()
                )
        );

        set.add(uri.toString());

        prefs.edit()
                .putStringSet("trash", set)
                .apply();

        Toast.makeText(
                this,
                "عکس به سطل منتقل شد",
                Toast.LENGTH_SHORT
        ).show();

        loadPhotos();
    }

    private void sharePhoto(Uri uri) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        startActivity(
                Intent.createChooser(
                        intent,
                        "اشتراک‌گذاری عکس"
                )
        );
    }

    private void showDetails(Uri uri) {

        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.MIME_TYPE,
                        MediaStore.Images.Media.WIDTH,
                        MediaStore.Images.Media.HEIGHT
                },
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {

            String name = cursor.getString(0);
            long size = cursor.getLong(1);
            String mime = cursor.getString(2);
            int width = cursor.getInt(3);
            int height = cursor.getInt(4);

            String text =
                    "📷 " + name +
                    "\n\nحجم: " + (size / 1024) + " KB" +
                    "\nابعاد: " + width + " × " + height +
                    "\nفرمت: " + mime;

            cursor.close();

            Toast.makeText(
                    this,
                    text,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void showSettings() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);

        TextView title = new TextView(this);
        title.setText("⚙ تنظیمات");
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        TextView dark = new TextView(this);
        dark.setText("🌙 حالت شب");
        dark.setTextSize(20);
        dark.setPadding(20, 35, 20, 35);
        layout.addView(dark);

        TextView lock = new TextView(this);
        lock.setText("🔒 قفل گالری");
        lock.setTextSize(20);
        lock.setPadding(20, 35, 20, 35);
        layout.addView(lock);

        TextView back = new TextView(this);
        back.setText("← برگشت");
        back.setTextSize(20);
        back.setGravity(Gravity.CENTER);
        back.setPadding(20, 35, 20, 35);
        layout.addView(back);

        setContentView(layout);

        dark.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "حالت شب در حال تکمیل است",
                        Toast.LENGTH_SHORT
                ).show()
        );

        lock.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "قفل گالری در حال تکمیل است",
                        Toast.LENGTH_SHORT
                ).show()
        );

        back.setOnClickListener(v -> buildHome());
    }

    private void openPhoto(int position) {

        Intent intent =
                new Intent(this, PhotoActivity.class);

        intent.putExtra(
                "photo_uri",
                photos.get(position).toString()
        );

        startActivity(intent);
    }

    class GalleryAdapter extends android.widget.BaseAdapter {

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
                ViewGroup parent
        ) {

            ImageView image;

            if (convertView == null) {
                image = new ImageView(MainActivity.this);
            } else {
                image = (ImageView) convertView;
            }

            int width =
                    parent.getWidth() > 0
                            ? parent.getWidth() / columns
                            : 300;

            image.setLayoutParams(
                    new GridView.LayoutParams(
                            width,
                            width
                    )
            );

            image.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );

            image.setImageURI(
                    photos.get(position)
            );

            return image;
        }
    }
}
