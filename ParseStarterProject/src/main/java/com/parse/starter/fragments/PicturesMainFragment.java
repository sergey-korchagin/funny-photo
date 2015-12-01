package com.parse.starter.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class PicturesMainFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPager mPager;
    private PhotoPagerAdapter mAdapter;
    List<ParseObject> categories;
    List<ParseObject> updatedCategories;
    int skip = 0;
int querySize;
    boolean first;
    Uri previous = null;
    boolean firstTime = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pictures_main_fragment, container, false);


        mPager = (ViewPager) root.findViewById(R.id.photos_image_pager);

        mPager.addOnPageChangeListener(this);
        getQuerySize();
        getCategories();



        return root;
    }

    public void getQuerySize(){

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
        if(categories.get(position).get("mPicture")!=null){
            ParseFile applicantResume = (ParseFile) categories.get(position).get("mPicture");
            applicantResume.getUrl();
//            applicantResume.getDataInBackground(new GetDataCallback() {
//                public void done(byte[] data, ParseException e) {
//                    if (e == null) {
//                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                        Intent share = new Intent(Intent.ACTION_SEND);
//                        share.setType("image/jpeg");
//
//                        ContentValues values = new ContentValues();
//                        values.put(MediaStore.Images.Media.TITLE, "title");
//                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                        Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                values);
//
//                        OutputStream outstream;
//                        try {
//                            outstream = getActivity().getContentResolver().openOutputStream(uri);
//                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
//                            outstream.close();
//                        } catch (Exception ex) {
//                            System.err.println(e.toString());
//                        }
//
//                        share.putExtra(Intent.EXTRA_STREAM, uri);
//                        ((MainActivity) getActivity()).setShareIntent(share);
//
//
//                    } else {
//                        e.printStackTrace();
//                    }
//                }
//            });

//            Intent share = new Intent(Intent.ACTION_SEND);
//                   share.setType("text/plain");
//            share.putExtra(Intent.EXTRA_STREAM, "test");
//                       ((MainActivity) getActivity()).setShareIntent(share);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, applicantResume.getUrl());
            ((MainActivity) getActivity()).setShareIntent(shareIntent);

        }

        if (position%5==1 && skip<querySize){
                skip = skip+5;


//            if(skip == querySize){
//                skip = 0;
//            }
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity!=null){
            ((MainActivity) activity).showActionbar();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
