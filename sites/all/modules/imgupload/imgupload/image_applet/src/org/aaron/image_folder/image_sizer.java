package org.aaron.image_folder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.ImageIcon;
import java.io.*;

/**
 * Use Java2D API to load and resize and possibly rotate an image.
 * 
 * @author aaron
 *
 */
public class image_sizer {
    private File selFile;
    private int myx;
    private int myy;
    private int my_rotate;
    private boolean my_quick;
    
    public int original_width;
    public int original_height;
    
    BufferedImage my_image = null;
    ImageIcon icon = null;
    image_sizer(File f,int dimx,int dimy,int rotate,boolean quick) {
        selFile = f;
        myx = dimx;
        myy = dimy;
        my_rotate = rotate;
        my_quick = quick;
    }
    public static ImageReader getReaderFromExtension(File f) {
        String name = f.getName();
        name = (name.substring(name.lastIndexOf('.')+1,name.length())).toLowerCase();
        ImageReader reader = ImageIO.getImageReadersByFormatName(name).next();
        return reader;
    }
    public BufferedImage getImage() 
    {
        
        if (my_image== null) {
            try {
                BufferedImage img = null;
                ImageReader reader = getReaderFromExtension(selFile);
                ImageReadParam irp = reader.getDefaultReadParam();
                ImageInputStream iis = ImageIO.createImageInputStream(selFile);
                reader.setInput(iis);
                int height = reader.getHeight(0);
                int width = reader.getWidth(0);
                if (my_quick) {
                	width = 8;height = 8;
                }
                if (myy != 0) {
                    height = reader.getHeight(0)/myy;
                    height++;
                }

                if (height < 1) height = 1;
                
                if (myx != 0) {
                    width = reader.getWidth(0)/myx;
                    width++;
                }

                if (width < 1) width = 1;
                
                original_width = reader.getWidth(0);
                original_height = reader.getHeight(0);
                
                if (my_quick)
                    irp.setSourceSubsampling(width, height, 0, 0);
                else
                    irp.setSourceSubsampling(1, 1, 0, 0);
                
                img = reader.read(0,irp);
                if (my_quick) {
                    my_image = img;
                } else {
                    BufferedImage copy = null;
                    double dh = 1.0*myy;
                    double dw = 1.0*myx;                    
                    double theta = 90.0*my_rotate;
                    if (my_rotate %2 != 0) {
                        copy = new BufferedImage(myy, myx,                               
                            BufferedImage.TYPE_INT_RGB);
                    }
                    else
                        copy = new BufferedImage(myx, myy,                               
                                BufferedImage.TYPE_INT_RGB);
                        
                    Graphics2D graph = copy.createGraphics();                    
                    graph.rotate(Math.toRadians(theta),myx/2,myy/2);
                    graph.translate((dw-dh)/2.0*Math.sin(Math.toRadians(theta)),
                ((dw-dh)/2.0)*Math.sin(Math.toRadians(theta)));
                    graph.scale(dw/(original_width*1.0),dh/(original_height*1.0));
                    graph.drawRenderedImage(img,null);
                    my_image = copy;
                }
                reader.dispose();
            } catch (IOException ioe) { }
        }
        return my_image;
    }
    public void WriteImage(String fn) {
        BufferedImage img = getImage();
        ImageReader reader = getReaderFromExtension(selFile);
        ImageWriter writer = ImageIO.getImageWriter(reader);
        try {
            writer.write(img);
        } catch (IOException ioe) {System.out.println("ioe in image_sizer" + ioe.toString());}
    }
    
    public ImageIcon getIcon() {
        if (icon == null) {
            Image img = getImage();
            if (img != null)
                icon = new ImageIcon(img);
            else
                System.out.println("Image is null!");
        }
        return icon;
    }
    
}
