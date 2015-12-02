package com.parse.starter.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.MainActivity;
import com.parse.starter.R;
import com.parse.starter.adapters.PhotoPagerAdapter;
import com.parse.starter.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by User on 30/11/2015.
 */
public class PicturesMainFragment extends Fragment implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    List<ParseObject> updatedCategories;
    int skip = 0;
    int querySize;
    public int mPosition;
    ImageView btnShare;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;
     int width = 0;
    int height = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View root = inflater.inflate(R.layout.pictures_main_fragment, container, false);

        btnShare = (ImageView) root.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);
        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);


        mPager.addOnPageChangeListener(this);
        checkIfStorageAvailable();
        getQuerySize();
        getCategories();

       final LinearLayout layout = (LinearLayout)root.findViewById(R.id.headerLayout);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
               width  = layout.getMeasuredWidth();
                height = layout.getMeasuredHeight();

            }
        });
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(height-20,height-20);
        btnShare.setLayoutParams(parms);
        return root;
    }

    public void getQuerySize() {

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    querySize = ((List) o).size();
                }
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {


        mPosition = position;

        if (position % 5 == 1 && skip < querySize) {
            skip = skip + 5;

            ParseQuery query = new ParseQuery("picture");
            query.addDescendingOrder("createdAt");
            query.setSkip(skip);
            query.setLimit(5);
            query.findInBackground(new FindCallback() {
                @Override
                public void done(List objects, ParseException e) {
                }

                @Override
                public void done(Object o, Throwable throwable) {
                    if (o instanceof List) {
                        updatedCategories = (List<ParseObject>) o;
                        mAdapter.getMorePhotos(updatedCategories, querySize);
                        mAdapter.notifyDataSetChanged();

                    }
                }
            });

        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void getCategories() {

        ParseQuery query = new ParseQuery("picture");
        query.addDescendingOrder("createdAt");
        query.setLimit(5);
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o instanceof List) {
                    categories = (List<ParseObject>) o;
                    mAdapter = new PhotoPagerAdapter(categories, getActivity());

                    mPager.setAdapter(mAdapter);

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if (btnShare.getId() == v.getId()) {
                if(mExternalStorageAvailable && mExternalStorageWriteable){
                    sendShareIntentPhoto(mPosition);
                }else{
                    sendShareIntentLink(mPosition);
            }
        }
    }

    public void sendShareIntentLink(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true).setMessage("No enough storage! But you still can share link to picture!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (categories.get(position).get("mPicture") != null) {
                            ParseFile applicantResume = (ParseFile) categories.get(position).get("mPicture");
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, applicantResume.getUrl());
                            startActivity(Intent.createChooser(shareIntent, "Share link"));
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        Window window = alert.getWindow();
        window.setGravity(Gravity.TOP);
        alert.show();


    }

    public void sendShareIntentPhoto(int position) {
        if (categories.get(position).get("mPicture") != null) {
            ParseFile applicantResume = (ParseFile) categories.get(position).get("mPicture");
            applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "title");
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                    Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);

                    OutputStream outstream;
                    try {
                        outstream = getActivity().getContentResolver().openOutputStream(uri);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                        outstream.close();
                    } catch (Exception ex) {
                        System.err.println(e.toString());
                    }

                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share picture"));


                } else {
                    e.printStackTrace();
                }
            }
        }

            );

            }
        }


    public void checkIfStorageAvailable(){
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }


}
