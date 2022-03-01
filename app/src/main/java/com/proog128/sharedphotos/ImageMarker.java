package com.proog128.sharedphotos;

import com.proog128.sharedphotos.filesystem.IPath;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class ImageMarker {
    private static Set<IPath> marks_ = new HashSet<>();

    public static boolean isMarked(IPath path){ return marks_.contains(path); }

    public static void clear(){ marks_.clear(); }
  
    public static void toggle(IPath path){
      if(!marks_.remove(path)) marks_.add(path);
    }

    public static void download(ImageDownload id){
      Iterator<IPath> it=marks_.iterator();
      while(it.hasNext()) id.run(it.next());
      marks_.clear();
    }
}
