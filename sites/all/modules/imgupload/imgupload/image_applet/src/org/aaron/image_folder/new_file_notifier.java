package org.aaron.image_folder;

/**
 * Act as interface between the Java GUI file part and the Java applet part
 * which actually performs the upload.
 * 
 * @author Owner
 *
 */
public interface new_file_notifier {
    public void notify_new_file(String filename,String alias);
    public boolean is_uploading();
    public int get_bandwidth();
}
