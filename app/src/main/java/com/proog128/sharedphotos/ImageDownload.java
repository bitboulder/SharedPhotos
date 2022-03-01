package com.proog128.sharedphotos;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import android.view.Gravity;

import com.proog128.sharedphotos.filesystem.IPath;

public class ImageDownload {
  // https://www.codeproject.com/articles/1112730/android-download-manager-tutorial-how-to-download
  private Context context_;

  public ImageDownload(Context context){
    context_=context;
    IntentFilter fi=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    context_.registerReceiver(receiver,fi);
  }

  private BroadcastReceiver receiver=new BroadcastReceiver(){
    @Override
    public void onReceive(Context context,Intent intent){ msg("Download complete"); }
  };

  public void msg(String txt){ Toast.makeText(context_,txt,Toast.LENGTH_LONG).show(); }

  public void run(IPath path){
    Uri uri=Uri.parse(path.getContentUrl());
    DownloadManager dm=(DownloadManager)context_.getSystemService(Context.DOWNLOAD_SERVICE);
    DownloadManager.Request req=new DownloadManager.Request(uri);
    req.setTitle(String.format("Image Download %s",path.toString()));
    req.setDescription(String.format("Image Download %s",path.toString()));
    req.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,String.format("SharedPhotos/%s.jpg",path.toString()));
    dm.enqueue(req);
    msg("Download started");
  }

}
