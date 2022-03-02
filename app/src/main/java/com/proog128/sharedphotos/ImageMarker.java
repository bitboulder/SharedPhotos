package com.proog128.sharedphotos;

import com.proog128.sharedphotos.filesystem.IPath;

import android.graphics.Color;
import android.widget.ImageView;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class ImageMarker {
  private static Set<IPath> marks_ = new HashSet<>();
  private static boolean used_ = false;

  private static boolean isMarked(IPath path){ return marks_.contains(path); }

  public static void clear(){ marks_.clear(); used_=false; }

  public static boolean used(){ return used_; }

  public static void toggle(IPath path){
    used_=true;
    if(!marks_.remove(path)) marks_.add(path);
  }

  public static void download(ImageDownload id){
    Iterator<IPath> it=marks_.iterator();
    while(it.hasNext()) id.run(it.next());
    clear();
  }

  public static void dplMark(ImageView mark,IPath path){
    if(!used_){
      mark.setImageResource(android.R.color.transparent);
    }else if(isMarked(path)){
      mark.setImageResource(android.R.drawable.star_big_on);
    }else{
      mark.setImageResource(android.R.drawable.star_big_off);
    }
    mark.setBackgroundColor(Color.TRANSPARENT);
  }
}
