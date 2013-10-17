package org.aaron.image_folder;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.awt.image.*;
import javax.imageio.*;

import javax.swing.Icon;

/**
 * Container class for containing a lot of scaled thumbnail images
 * 
 * @author aaron
 *
 */
public class icon_container {
    PriorityBlockingQueue<File> vec = new PriorityBlockingQueue<File>();
    ConcurrentHashMap<String,image_file_info> images = new ConcurrentHashMap<String,image_file_info>();
    
    public image_file_info getImageInfo(String name) {
        return images.get(name);
    }
    

    public void replace_image(String name,image_file_info info) {
        image_sizer is = new image_sizer(new File(name),128,128,info.rotate,false);
        info.image = is.getIcon();
        images.remove(name);
        images.put(name,info);
    }
    private boolean is_file_in_vec(File f) {
        if (f == null)
            return false;
        String name = f.getAbsolutePath();
        Iterator<File> it = vec.iterator();
        while (it.hasNext()) {
            if (name.contentEquals(it.next().getAbsolutePath()))
                return true;
        }
        return false;
    }
    
    public int getFilesLeft() {
        return vec.size();
    }
    public int getIconCount() {
        return images.size();
    }
    public void add_if_unique(File f) {
        if ((is_file_in_vec(f) == false) &&
                (f != null)) 
        { 
            vec.add(f);
        }
    }
    
    public image_file_info iconize_a_file(int x,int y,boolean quick) {
        if (vec.isEmpty() == false) {
            image_file_info rv = new image_file_info();
            File f = vec.remove();
            image_sizer is = new image_sizer(f,x,y,0,true);
            // image_sizer scaled = new image_sizer(f,rv.width,rv.height);
            // BufferedImage scaled_img = scaled.getImage();
            // rv.scaled_image = scaled_img;
            /*try {
                ImageIO.write(rv.scaled_image, "jpg", tmp);
                tmp.deleteOnExit();
                rv.scaled_file = tmp;
            } catch (IOException ioe) {System.out.println("scaling exp: " + ioe.toString());} */
            rv.image = is.getIcon();
            images.put(f.getAbsolutePath(), rv);
            
            rv.image = is.getIcon();
            rv.original_height = is.original_height;
            rv.original_width = is.original_width;
            rv.width = gui_names.default_width;
            if (rv.width > rv.original_width)
                rv.width = rv.original_width;
            double dh = rv.original_height;
            dh *= (rv.width*1.0)/(rv.original_width*1.0);
            rv.height = (int)dh;
            if (rv.height > rv.original_height)
                rv.height = rv.original_height;
            rv.file = f;
            return rv;
        }
        return null;
    }
    
    public Icon get_stored_icon(File f) {
        image_file_info img = images.get(f.getAbsolutePath());
        if (img == null) {
            return null;
        }        
        return img.image;
    }
    
    public void clear() {
        vec.clear();
        images.clear();
    }
}
