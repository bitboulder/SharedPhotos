package com.proog128.sharedphotos;

import android.app.UiModeManager;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.proog128.sharedphotos.filesystem.IPath;

import com.github.chrisbanes.photoview.PhotoViewAttacher;

import static android.content.Context.UI_MODE_SERVICE;

import android.widget.Toast;

public class PictureFragment extends Fragment implements LoaderManager.LoaderCallbacks<ImageLoader.Image> {
    private IPath path_;
    private ImageView image_;
    private ProgressBar progress_;
    private PhotoViewAttacher attacher_;
    private TextView caption_;
    private ImageView mark_;
    private int orientation_ = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

    public void setUrl(IPath path) {
        path_ = path;
    }

    private boolean isTV;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        updateOrientation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        UiModeManager uiModeManager = (UiModeManager) getActivity().getSystemService(UI_MODE_SERVICE);
        isTV = uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_picture, container, false);

        caption_ = (TextView) rootView.findViewById(R.id.caption);

        image_ = (ImageView) rootView.findViewById(R.id.image);
        progress_ = (ProgressBar) rootView.findViewById(R.id.progress);
        attacher_ = new PhotoViewAttacher(image_);
        mark_ = (ImageView)rootView.findViewById(R.id.mark);

        getLoaderManager().initLoader(0, null, this);

        updMark();
        attacher_.setOnLongClickListener(new View.OnLongClickListener(){
          @Override
          public boolean onLongClick(View view){
            ImageMarker.toggle(path_);
            updMark();
            return true;
          }
        });

        mark_.setOnClickListener(new View.OnClickListener(){
          @Override
          public void onClick(View view){
            if(ImageMarker.used()){
              ImageMarker.toggle(path_);
              updMark();
            }
          }
        });

        return rootView;
    }

    private void updMark(){ ImageMarker.dplMark(mark_,path_); }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(0);

        super.onDestroyView();
    }

    @Override
    public Loader<ImageLoader.Image> onCreateLoader(int id, Bundle args) {
        progress_.setVisibility(View.VISIBLE);
        return new ImageLoaderTask(getActivity(), path_);
    }

    @Override
    public void onLoadFinished(Loader<ImageLoader.Image> loader, ImageLoader.Image img) {
        progress_.setVisibility(View.GONE);

        Bitmap bmp = img.getBitmap();
        if(bmp.getWidth() > bmp.getHeight()) {
            orientation_ = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            orientation_ = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        image_.setImageBitmap(bmp);
        caption_.setText(subtitlesEnabled() ? img.getCaption() : "");
        attacher_.update();

        updateOrientation();
    }

    @Override
    public void onLoaderReset(Loader<ImageLoader.Image> loader) {
        progress_.setVisibility(View.GONE);
        image_.setImageBitmap(null);
        caption_.setText("");
        attacher_.update();
    }

    private void updateOrientation() {
        if(autoRotateEnabled()) {
            if (getUserVisibleHint() && orientation_ != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                getActivity().setRequestedOrientation(orientation_);
            }
        } else {
            if (getUserVisibleHint()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
    }

    private boolean autoRotateEnabled() {
        if(getActivity() != null && getActivity().getApplicationContext() != null) {
            if (isTV) {
                return false;
            } else {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                return prefs.getBoolean(SettingsActivity.KEY_PREF_AUTO_ROTATE, true);
            }
        }
        return true;
    }

    private boolean subtitlesEnabled() {
        if(getActivity() != null && getActivity().getApplicationContext() != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            return prefs.getBoolean(SettingsActivity.KEY_PREF_SUBTITLE, true);
        }
        return true;
    }
}
