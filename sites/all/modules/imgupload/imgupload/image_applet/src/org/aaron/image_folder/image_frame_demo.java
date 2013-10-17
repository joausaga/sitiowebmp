package org.aaron.image_folder;

import java.awt.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.net.*;

/**
 * Just a class that I use to post my questions to Java
 * forums.
 * 
 * @author aaron
 *
 */
public class image_frame_demo   {
    BufferedImage getXformedImage(BufferedImage src) {
        double theta = 0.0;
        int width = src.getHeight();
        int height = src.getWidth();
        double dh = 1.0*height;
        double dw = 1.0*width;
        BufferedImage copy = new BufferedImage(height, width,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graph = copy.createGraphics();
        graph.rotate(Math.toRadians(theta),width/2,height/2);
        graph.translate((dw-dh)/2.0*Math.sin(Math.toRadians(theta)),
                ((dw-dh)/2.0)*Math.sin(Math.toRadians(theta)));
        //graph.rotate(Math.toRadians(180),width/2,height/2);
        // graph.rotate(Math.toRadians(270),width/2,height/2);
        graph.drawRenderedImage(src,null);
        return copy;
    }
        
    image_frame_demo() {
        JFrame my_frame;        
        JFrame.setDefaultLookAndFeelDecorated(true);
        my_frame = new JFrame("Demo");        
        my_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BufferedImage img = null;
        try {
            URL url = new URL("http://www.towncats.org/images/image_library/image1.jpg");
            img = ImageIO.read(url);
            img = getXformedImage(img);
        } catch (IOException e) {  }
        
        final BufferedImage fimg = img;
        JLabel label  = new JLabel() {
            public void paint(Graphics g) {
                g.drawImage(fimg, 0,0,null);
            }
        };
        JPanel root = new JPanel(new BorderLayout());
        root.add(label);
        my_frame.setContentPane(root);
        my_frame.pack();
        my_frame.setVisible(true);
        my_frame.setSize(640,480);
        
    }
    
    public static void createAndShowGUI() {
        image_frame_demo frame = new image_frame_demo();
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
