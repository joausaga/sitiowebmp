package org.aaron.image_folder;

import javax.imageio.ImageIO;
import javax.swing.filechooser.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Java has no Windows-style thumbnail view that supports multi-select.  So 
 * we roll our own here, extending the lame Java-based file chooser dialog.
 * @author aaron
 *
 */
public class thumbnail_view extends FileView {
    icon_container my_icon_container = new icon_container();
    ImageIcon image_icon;
    ImageIcon folder_icon;
    thumbnail_view() {
        folder_icon = image_folder.get_image_from_path("org/aaron/image_folder/folder.gif");
        image_icon = image_folder.get_image_from_path("org/aaron/image_folder/image.gif");
    }
    public Icon getIcon(File f) {
        Icon icon = null;
        
        String name = f.getName();
        if (name.toLowerCase().contains(".jpg") || 
            name.toLowerCase().contains(".gif") ||
            name.toLowerCase().contains(".png")) {
            icon = my_icon_container.get_stored_icon(f);
            if (icon == null) {
                my_icon_container.add_if_unique(f);
                if (image_icon != null)
                    return image_icon;
                return super.getIcon(f);
            }
            else return icon;
        } else {
            if (folder_icon != null)
                return folder_icon;
            icon = super.getIcon(f);
        }
        return icon;
    }
    
    public void change_dir_clear() {
        my_icon_container.clear();
    }
    public boolean makeNewImage() {
        return (my_icon_container.iconize_a_file(64, 64,true) != null);
    }
    
}
