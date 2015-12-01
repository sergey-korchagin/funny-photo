/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseAnalytics;
import com.parse.starter.fragments.PicturesMainFragment;
import com.parse.starter.fragments.SplashScreenFragment;
import com.parse.starter.managers.ActionbarManager;
import com.parse.starter.utils.Constants;
import com.parse.starter.utils.Utils;


public class MainActivity extends AppCompatActivity {
  private ShareActionProvider mShareActionProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      ParseAnalytics.trackAppOpenedInBackground(getIntent());
      ActionbarManager.getInstance().init(this, getSupportActionBar());

      Constants.ACTIONBAR_HEIGHT = getSupportActionBar().getHeight();


      SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
      Utils.replaceFragment(getFragmentManager(), android.R.id.content, splashScreenFragment, false);
  }

    public void hideActionbar() {

        getSupportActionBar().hide();
    }

    public void showActionbar() {
        getSupportActionBar().show();
    }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate menu resource file.
    //getMenuInflater().inflate(R.menu.menu_main, menu);
      getMenuInflater().inflate(R.menu.share_menu, menu);

    // Locate MenuItem with ShareActionProvider
    MenuItem item = menu.findItem(R.id.menu_item_share);

    // Fetch and store ShareActionProvider
    mShareActionProvider =  (ShareActionProvider) MenuItemCompat.getActionProvider(item);
//      Intent shareIntent = new Intent(Intent.ACTION_SEND);
//      shareIntent.setType("text/plain");
//      shareIntent.putExtra(Intent.EXTRA_TEXT, "test");
//      setShareIntent(shareIntent);

    // Return true to display menu
    return true;
  }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
        }
        return super.onOptionsItemSelected(item);
    }

  // Call to update the share intent
  public void setShareIntent(Intent shareIntent) {
    if (mShareActionProvider != null) {
      mShareActionProvider.setShareIntent(shareIntent);
    }
  }

}
