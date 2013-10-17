package org.aaron.image_folder;

import java.awt.Component;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Another way to do a table of GUIs.  but I don't
 * use it in this application.
 * 
 * @author aaron
 *
 */
public class image_list //  extends JLabel
 implements ListCellRenderer {

    HashMap<Integer,view_panel> my_panels = new HashMap<Integer, view_panel>();
    
    // JLabel my_label = new JLabel();
    
    public image_list() {
        // my_label.setOpaque(true);
        //my_panel.setHorizontalAlignment(JLabel.CENTER);
        //my_label.setVerticalAlignment(JLabel.CENTER);
    }

    /*
     * This method finds the image and text corresponding
     * to the selected value and returns the label, set up
     * to display the text and image.
     */
    public Component getListCellRendererComponent(
                                       JList list,
                                       Object value,
                                       int index,
                                       boolean isSelected,
                                       boolean cellHasFocus) {

        view_panel info = null;
        try {
            info = ((view_panel)value);
            if (isSelected) {
                info.setBackground(Color.BLUE);
            } else {
                info.setBackground(Color.CYAN);
            }
        }
        catch (java.lang.ClassCastException cce) {
            System.out.println("image_list: unexpected object");
            return null;
        }
        return info;
    }
}



