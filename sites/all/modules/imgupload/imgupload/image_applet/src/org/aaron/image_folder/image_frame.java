package org.aaron.image_folder;

import javax.swing.*;

/**
 * A Java-app version of the applet, only used for testing.
 * 
 * @author aaron
 *
 */
public class image_frame  implements new_file_notifier {
    JFrame my_frame;
    
    
    image_frame() {
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        my_frame = new JFrame("Picture Downloader");
        my_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        my_frame.setContentPane((new image_folder(this)).get_panel());
        my_frame.pack();
        my_frame.setVisible(true);
        my_frame.setSize(640,480);
    }
    
    public static void createAndShowGUI() {
        image_frame frame = new image_frame();
    }
     
    public int get_bandwidth() {
    	return 0;
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    @Override
    public void notify_new_file(String filename,String alias) {
        // do nothing....
        System.out.println("write " + filename + " aka " + alias);
    }

    @Override
    public boolean is_uploading() {
        // TODO Auto-generated method stub
        return false;
    }
    
}
