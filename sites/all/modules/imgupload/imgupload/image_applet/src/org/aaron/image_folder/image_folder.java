package org.aaron.image_folder;

import org.aaron.xml.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
// import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Contains all the main logic for displaying the upload list and launching the
 * upload.
 * 
 * @author Owner
 * 
 */
public class image_folder implements view_panel.ActionHandler {
    
    view_panel my_panel;
    boolean started = false;
    javax.swing.Timer my_timer;
    icon_container my_icon_container = new icon_container();
    xml_tree my_image_xml_pane = get_new_image_spec();
    new_file_notifier my_notifier;
    BufferedImage my_preview_image = null;
    JPanel my_preview_panel = null;
    HashMap<File,image_file_info> image_map = new HashMap<File,image_file_info>();
    HashMap<String,view_panel> image_button_map = new HashMap<String,view_panel> ();
    PriorityBlockingQueue<File> imgs_to_upload = new PriorityBlockingQueue<File>();
    boolean is_uploading = false;
    double pct = 0.0;
    
    private int upload_file_count = 0;
    
    private BufferedImage get_preview_image() {return my_preview_image;}
    
    public void stopTimer() {
    	if (my_timer != null) {
    		my_timer.stop();
    	}
    }
    
    /**
     * Set the fields in the GUI based on the information in the file we are reading.
     * @param vp
     * @param info
     * @param setSlider
     */
    private void setFieldsFromInfo(view_panel vp,image_file_info info,boolean setSlider) {
        vp.textFieldSet(gui_names.preview_image_height, new Integer(info.height).toString());
        vp.textFieldSet(gui_names.preview_image_width, new Integer(info.width).toString());
        
        double compression = 1.0* info.width;
        compression /= (1.0 * info.original_width);
        compression = 100.0 - (100.0 * compression);
        compression = Math.round(100.0 * compression)/100.0;
                
        vp.textFieldSet(gui_names.compression_text, new Integer((int)compression).toString() + "%");
        if (setSlider)
            vp.sliderSetValue(gui_names.compression_slider,(int)compression);
        
        if (info.height == info.original_height && info.width == info.original_width) {
            vp.sliderEnable(gui_names.compression_slider, false);
            vp.checkBoxSetSelected(gui_names.to_compress, false);
        }
        image_map.put(info.file, info);        
    }

    /**
     * Show the preview image as the default - first in list.
     */
    private void reset_to_first_image() {
    	Set<File> files = this.image_map.keySet();
    	if (files != null && files.size() > 0) {
    		File[] ar = files.toArray(new File[]{});
    		set_preview_from_file(ar[0].getAbsolutePath());
    	} else {
    		my_preview_image = null;
    	}
    	update_preview_pane();
    }
    
    /**
     * Set the preview to be this specific file.
     * @param file
     */
    private void set_preview_from_file(String file) {
        image_file_info info = my_icon_container.getImageInfo(file);
        image_sizer is = new image_sizer(info.file,
                info.width,info.height,info.rotate,false);
        my_preview_image = is.getImage();
        my_panel.subPanelGet(gui_names.preview_pane).blankFieldSetSize(
                gui_names.image_preview, new Dimension(my_preview_image.getWidth(),my_preview_image.getHeight()));
    }

    /**
     * Start the upload timer.  We upload one file at a time 'til complete.
     */
    private void start_upload() {
        is_uploading = true;
        Set<File> files = image_map.keySet();
        Iterator<File> it = files.iterator();
        while (it.hasNext()) {
            imgs_to_upload.put(it.next());
        }
        upload_file_count = imgs_to_upload.size();
        my_timer.start();
    }
    
    /**
     * Put a new image in the preview pane.
     */
    private void update_preview_pane() {
        view_panel childp = my_panel.subPanelGet(gui_names.preview_pane);
        // childp.repaint();
        my_panel.repaint();
    }
    
    /**
     * Some GUI widget got an event, deal.
     */
    public void handleAction(String comp_name, JComponent comp, AWTEvent e,view_panel vp) {
        JButton but = null;
        JList list = null;
        JCheckBox box = null;
        JSlider slider = null;
        try {
            if (comp instanceof JButton)
                but = (JButton) comp;
            if (comp instanceof JList)
                list = (JList) comp;
            if (comp instanceof JCheckBox)
                box = (JCheckBox) comp;
            if (comp instanceof JSlider) 
                slider = (JSlider)comp;
        } catch (ClassCastException cce) {
        }
        if (comp_name.contentEquals(gui_names.upload_button)) {
            if (is_uploading == true) {
                is_uploading = false;
                my_panel.subPanelGet(gui_names.button_panel).buttonChangeText(gui_names.upload_button, "Start Upload");
            }
            else {
                my_panel.subPanelGet(gui_names.button_panel).buttonChangeText(gui_names.upload_button, "Cancel");
                start_upload();                
            }
        }
        /**
         * Ignore events that occur during upload, except a cancel of the upload.
         */
        if (this.is_uploading == true) {
            return;
        }
        if (comp_name.contentEquals(gui_names.image_button)) {
            String file = vp.textFieldGetText(gui_names.image_name);
            set_preview_from_file(file);
            update_preview_pane();
        }
        if (comp_name.contentEquals(gui_names.rotate_button)) {
            String fn = vp.textFieldGetText(gui_names.image_name); 
            image_file_info info = my_icon_container.getImageInfo(fn);
            info.rotate = (info.rotate + 1) % 4;
            my_icon_container.replace_image(fn, info);
            vp.buttonSetImage(gui_names.image_button, info.image);
            set_preview_from_file(info.file.getAbsolutePath());
            update_preview_pane();
        }
        if (comp_name.contentEquals(gui_names.compression_slider)) {
            int pct = slider.getValue();
            System.out.println("pct = " + pct);
            String file = vp.textFieldGetText(gui_names.image_name);
            image_file_info info = my_icon_container.getImageInfo(file);
            double coeff = (pct*1.0);
            coeff = 100.0-coeff;
            coeff /= 100.0;
            // coeff = Math.sqrt(coeff);
            if (info != null) {
                info.height = (int) (info.original_height * coeff);
                info.width = (int) (info.original_width * coeff);
                setFieldsFromInfo(vp,info,false);
                set_preview_from_file(info.file.getAbsolutePath());
            }
            update_preview_pane();
        }
        if (comp_name.contentEquals(gui_names.remove_box)) {
            view_panel parent = my_panel.subPanelGet(gui_names.list_panel);
            String fn = vp.textFieldGetText(gui_names.image_name);
            File file = new File(fn);
            view_panel removed = this.image_button_map.remove(fn);
            parent.blankFieldRemoveComponent("image_panel", removed);
            image_map.remove(file);
            reset_to_first_image();
        }
        if (comp_name.contentEquals(gui_names.to_compress)) {
            ItemEvent evt = (ItemEvent)e;
            boolean checked = (evt.getStateChange() == ItemEvent.SELECTED);            
            String file = vp.textFieldGetText(gui_names.image_name);
            image_file_info info = my_icon_container.getImageInfo(file);
            if (info != null) {
                if (!checked) {
                    info.height = info.original_height;
                    info.width = info.original_width;
                }
                else {
                    info.height = 480;
                    info.width = 640;
                }
                setFieldsFromInfo(vp,info,true);
            }
            vp.sliderEnable(gui_names.compression_slider,checked);
            update_preview_pane();
        }
        if (comp_name.contentEquals("File")) {
            File[] selFiles = file_chooser.getFiles();
            if ((selFiles == null) || (selFiles.length == 0))
                return;
            for (int idx = 0; idx < selFiles.length; ++idx) {
                File selFile = selFiles[idx];                    
                my_icon_container.add_if_unique(selFile);
            }
            my_timer = new javax.swing.Timer(100, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handle_timer();
                }
            });
            my_panel.subPanelGet(gui_names.button_panel).buttonEnable(gui_names.upload_button, false);
            my_timer.start();
        } else if (comp_name.contentEquals("Commit")) {
            
        }
        else if (list != null) {
            System.out.println("selected!");
        }
    }

    /**
     * Get a resource from the .jar file as an input source.
     * 
     * @param path
     * @return
     */
    static public InputStream get_file_resource(String path) {
        java.io.InputStream  is = null;
        is = getClassLoader().getResourceAsStream(path);
        
        if (is == null) {
        	System.out.println("is is null!!!!");
        }

        return is;
    }

    /**
     * Given a path into the jar file, get an image out.  For icons and stuff.
     * @param path
     * @return
     */
    static public ImageIcon get_image_from_path(String path) {
        ImageIcon rv= null;
        InputStream is = image_folder.get_file_resource(path);
        try {
            BufferedImage img = ImageIO.read(is);
            rv = new ImageIcon(img);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rv;
    }
    
    static private ClassLoader getClassLoader() {
        try {
            Class<?> claz = Class.forName("org.aaron.image_folder.image_folder");
            ClassLoader loader = claz.getClassLoader();
            return loader;
        } catch (ClassNotFoundException cfe) {}
        
        return null;
    }
    
    /**
     * Create the GUI out of the xml schema.
     * @return
     */
    static private org.aaron.xml.xml_tree get_new_image_spec() {
        xml_processor xml = new xml_processor(get_file_resource("org/aaron/image_folder/gui_image.xml"));
        org.aaron.xml.xml_tree tree = xml.parse();
        return tree;
    }

    /**
     * ctor.  create the GIU window and populate the main buttons.
     * 
     * @param notifier
     */
    public image_folder(new_file_notifier notifier) {
        my_notifier = notifier;
        
        my_preview_panel = new JPanel() {
            public void paint(Graphics g) {
                if (get_preview_image() != null)
                    g.drawImage(get_preview_image(), 0,0,null);
            }
        };
        
        InputStream file = get_file_resource("org/aaron/image_folder/gui_panel.xml");
        xml_processor xml = new xml_processor(file);
        org.aaron.xml.xml_tree tree = xml.parse();
        org.aaron.image_folder.pane_info pi = null;
        if (tree != null) {
            Object obj = tree.getStructure();
            pi = (org.aaron.image_folder.pane_info)obj;
        }
        
        my_panel = pi.get_panel();
        my_panel.addActionHandler("File", this);
        my_panel.addActionHandler(gui_names.upload_button,this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = num_panels;
        num_panels++;
        gbc.weighty = 0.8;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.BOTH;        
        my_panel.subPanelGet(gui_names.preview_pane).blankFieldAddComponent(
                gui_names.image_preview,my_preview_panel,gbc);
        my_panel.statusFieldSet("No File Chosen");
        my_panel.subPanelGet(gui_names.button_panel).buttonEnable(gui_names.upload_button, false);
    }

    /**
     * Fake an open event, so the user can insert some files in the GUI 
     * right away.
     */
    public void openFileDialog() {
        view_panel bpanel = my_panel.subPanelGet(gui_names.button_panel);
        bpanel.buttonClick("File");
    }
    
    public view_panel get_panel() {
        return my_panel;
    }
    private int num_panels = 0;
    
    /**
     * There may be lots of files, so read the files in one at 
     * a time to improve response time of GUI.
     */
    public void handle_timer() {
        if (is_uploading == false) {
            update_pics();
        }
        else {
            upload_pics();
        }
    }

    /**
     * Timer event for uploading a new picture.  Remove from the queue,
     * compress it, and send it to the applet logic.
     */
    public void upload_pics() {
        if (my_notifier.is_uploading() == true) {
            my_panel.statusFieldSet("Uploading Images (" + pct + "% Complete) " + 
            		my_notifier.get_bandwidth() + " bytes sent.");
            return;
        }
        if (imgs_to_upload.isEmpty() == false) {
            File next = imgs_to_upload.remove();
            if (next != null) {
                image_file_info info = image_map.remove(next);
                image_sizer sizer = new image_sizer(info.file,info.width,info.height,info.rotate,false);
                String original_fn = info.file.getName();
                String ext = original_fn.substring(original_fn.lastIndexOf('.')+1,original_fn.length());
                
                try {
                    my_panel.statusFieldSet("Uploading Images (" + pct + "% Complete) - compressing");
                    File tmpfile = File.createTempFile("tmp", "."+ext);
                    tmpfile.deleteOnExit();
                    ImageIO.write(sizer.getImage(), ext, tmpfile);
                    view_panel vp = my_panel.subPanelGet(gui_names.list_panel);
                    view_panel uploaded = this.image_button_map.remove(info.file.getAbsolutePath());
                    vp.blankFieldRemoveComponent("image_panel", uploaded);
                    my_preview_image = sizer.getImage();
                    update_preview_pane();
                    my_notifier.notify_new_file(tmpfile.getAbsolutePath(),info.file.getName());
                    
                    pct = ((imgs_to_upload.size() + 1)*1.0)/(upload_file_count *1.0);
                    pct *= 100.0;
                    pct = 100.0 - pct;
                    pct *= 100.0;
                    pct = Math.round(pct);
                    pct /= 100.0;
                    my_panel.statusFieldSet("Uploading Images (" + pct + "% Complete)");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    System.out.println("can't create tempfile in upload_pics");
                }
            }
        } else {
            my_timer.stop();
            my_preview_image = null;            
            is_uploading = false;

            /**
             * Auto-close the window.
             */
            Component c = SwingUtilities.getRoot((Component) my_panel);
            try {
                JFrame frame = (JFrame) c;
                if (frame != null) {
                	Toolkit tk = Toolkit.getDefaultToolkit();
                    EventQueue evtQ = tk.getSystemEventQueue();
                    evtQ.postEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));                }
            } catch (ClassCastException cce) {
            }
        	
            /*my_panel.subPanelGet(gui_names.button_panel).buttonChangeText(gui_names.upload_button, "Start Upload");
            my_panel.subPanelGet(gui_names.button_panel).buttonEnable(gui_names.upload_button, false);
            my_panel.statusFieldSet("Uploading Images Complete"); */
        }
    }

    /**
     * The user has selected some files.  Read them into the GUI so the user can 
     * manipulate them if she likes...
     */
    public void update_pics() {
        my_timer.stop();
        image_file_info info = my_icon_container.iconize_a_file(128, 128,false);
        
        if (info != null) {
            image_map.put(info.file, info);
            view_panel vp = my_panel.subPanelGet(gui_names.button_panel);
            if (vp != null) {
                pane_info imgpn = (pane_info)this.my_image_xml_pane.getStructure();
                view_panel imgvp = imgpn.get_panel();
                imgvp.setBorder(BorderFactory.createLoweredBevelBorder());
                view_panel childp = imgvp.subPanelGet("subpanel");
                childp.addActionHandler(gui_names.to_compress, this);
                childp.addActionHandler(gui_names.remove_box, this);
                childp.addActionHandler(gui_names.rotate_button, this);
                childp.buttonSetImage(gui_names.image_button, info.image);
                childp.addActionHandler(gui_names.image_button, this);
                childp.textFieldSet(gui_names.image_name, info.file.getAbsolutePath());
                image_button_map.put(info.file.getAbsolutePath(), imgvp);
                childp.textFieldSet(gui_names.preview_image_width, 
                        new Integer(info.width).toString());
                childp.textFieldSet(gui_names.preview_image_height, 
                        new Integer(info.height).toString());
                childp.textFieldEnable(gui_names.compression_text,false); 
                childp.textFieldEnable(gui_names.preview_image_height,false); 
                childp.textFieldEnable(gui_names.preview_image_width,false);
                childp.buttonSetImage(gui_names.rotate_button, 
                        get_image_from_path("org/aaron/image_folder/rotate.gif"));

                // vp.tableSetValueAt("image_table",imgvp,1,0);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = num_panels;
                num_panels++;
                gbc.weighty = 0.8;
                gbc.fill = GridBagConstraints.BOTH;
                my_panel.subPanelGet(gui_names.list_panel).blankFieldAddComponent(
                        "image_panel", imgvp,gbc);

                if (this.my_preview_image == null) {
                    set_preview_from_file(info.file.getAbsolutePath());
                    
                }
                double pct = (100.0*my_icon_container.getFilesLeft())/
                   (my_icon_container.getIconCount() + my_icon_container.getFilesLeft()); 
                pct = 100.0 - pct;
                pct = Math.round(100.0*pct)/100.0;
                
                setFieldsFromInfo(childp,info,true);
 
                childp.addActionHandler(gui_names.compression_slider, this);
                my_panel.statusFieldSet("Reading Images (" + pct + "% Complete)");
                my_timer.start();
            }
        } else {
            my_panel.subPanelGet(gui_names.button_panel).buttonEnable(gui_names.upload_button, true);
            my_timer.stop();
        }
    }
}
