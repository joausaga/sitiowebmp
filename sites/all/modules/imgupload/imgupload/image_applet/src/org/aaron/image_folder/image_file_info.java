package org.aaron.image_folder;

import java.io.*;
import javax.swing.*;
import java.awt.image.*;

/**
 * Struct-like class holding info about an image file.
 * 
 * @author aaron
 *
 */
public class image_file_info {
    public Icon image;
    public File file;
    public int width = gui_names.default_width;
    public int height = gui_names.default_height;
    public int original_height = 0;
    public int original_width = 0;
    public int rotate = 0;
}
