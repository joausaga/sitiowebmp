package org.aaron.image_folder;

import java.io.File;

import javax.swing.*;
import java.awt.event.*;

/**
 * Implements the file chooser dialog, overriding the display class.
 * 
 * @author aaron
 *
 */
public class file_chooser extends JFileChooser {
    static File[] getFiles() {
        file_chooser jfc = new file_chooser();
        jfc.setMultiSelectionEnabled(true);
        jfc.showOpenDialog(null);
        jfc.stop_running();
        return jfc.getSelectedFiles();        
    }
    
    thumbnail_view my_view = new thumbnail_view();
    boolean started = false;
    Timer my_timer;
    
    public void stop_running() {
        if (my_timer != null) {
            my_timer.stop();
        }
    }
    public file_chooser() {
        javax.swing.filechooser.FileFilter ff = new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName();
                if (name.toLowerCase().contains(".jpg") || 
                    name.toLowerCase().contains(".gif") ||
                    name.toLowerCase().contains(".png")) {
                    return true;
                }
                return false;
            }
            public String getDescription() {
                return "Image Files";
            }
        };
                
        addChoosableFileFilter(ff);
        
        setFileView(my_view);
        
        my_timer = new Timer(30,new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update_pics();
            }
        });
        
        my_timer.start();
        started = true;
    }
    
    public void update_pics() {
        java.awt.Graphics g = getGraphics();
        if (my_view.makeNewImage() == false) {
            my_timer.setDelay(250);
        } else {
            my_timer.setDelay(30);
        }
        if (g!= null) paint(g);        
    }
    public void setCurrentDirectory(File dir) {
        if (started) {
            my_view.change_dir_clear();
            my_timer.start();
        }
        super.setCurrentDirectory(dir);
    }
}
