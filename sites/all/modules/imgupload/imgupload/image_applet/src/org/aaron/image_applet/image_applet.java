package org.aaron.image_applet;

import javax.swing.*;

import org.aaron.image_folder.*;

import java.io.*;
import java.net.*;
import java.util.*;

import netscape.javascript.JSObject;
import java.awt.event.*;
import java.awt.*;

/**
 * The applet that performs the image scaling and downloading.
 * The actual loading and scaling of images is done in the image_folder
 * package, this part takes care of the upload to the web server.
 * 
 */
public class image_applet extends JApplet implements new_file_notifier {
    JTextField field;
    boolean my_is_uploading = false;
    int retries = 0;
    int bandwidth = 0;
    static final int RETRY_MAX=5;
    String current_filename;
    String current_alias;
    boolean my_debug = false;

    public void notify_new_file(String filename,String alias) {
        if (my_is_uploading == false) {
            my_is_uploading = true;
            current_filename = filename;
            current_alias = alias;
            
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    post_file();
                }
            }, 10);
        }
    }
    
    public int get_bandwidth() {
    	return bandwidth;
    }
    public boolean is_uploading() {
        return my_is_uploading;
    }
    
    public String getHeader(int size,URL url,String path) {
        String str = "POST "+ path + " HTTP/1.1\r\n";
        // str = "POST /php/upload.processor.php HTTP/1.1\r\n";

        str += "Host: " + url.getHost()+ "\r\n";
        str += "Accept: */*\r\n";
        str += "Referer: http://" + url.getHost() + path + "\r\n";
        str += "Accept-Language: en-us\r\n";
        str += "Content-Type: multipart/form-data; boundary=" + getSmallDelimiter();
        str += "UA-CPU: x86\r\n";
        str += "Accept-Encoding: gzip, deflate\r\n";
        str += "User-Agent: Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727)\r\n";
        str += "Content-Length: " + size + "\r\n";
        str += "Connection: Keep-Alive\r\n";
        str += "Cache-Control: no-cache\r\n";
        str += "Cookie: " + getCookie() + "\r\n\r\n";
        return str;
    }
    
    public String getSmallDelimiter() {
        return "---------------------------7d829f23360494\r\n";
    	
    }
    public String getDelimiter() {
        return "--" + getSmallDelimiter();
    }
    public String getForm1(String filename,String form_token) {
        String str = getDelimiter();
        str += "Content-Disposition: form-data; name=\"MAX_FILE_SIZE\"\r\n\r\n";
        str += "3000000\r\n";
        str += getDelimiter();
        str += "Content-Disposition: form-data; name=\"form_token\"\r\n\r\n";

        str += form_token + "\r\n";
        str += getDelimiter();
        str += "Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n";
        str += "Content-Type: image/gif\r\n\r\n";  
        return str;
    }
    
    public String getForm2() {

        String str = "\r\n"+getDelimiter();
        str += "Content-Disposition: form-data; name=\"submit\"\r\n\r\n";

        str += "Upload me!\r\n";
        str += getDelimiter();
        return str;
    }
    
    public void setFormButtons(boolean set) {
        try {
        JSObject myBrowser = (JSObject) JSObject.getWindow(this);
        if (set)
        	myBrowser.eval("enableGUIButtons()");
        else 
        	myBrowser.eval("disableGUIButtons()");
        	
        }
        catch (Exception e){
          System.out.println("Navigated away from page");
         }
    }
    public String getCookie() {
        try {
            JSObject myBrowser = (JSObject) JSObject.getWindow(this);
            JSObject myDocument =  (JSObject) myBrowser.getMember("document");
            String myCookie = (String)myDocument.getMember("cookie");
            if (myCookie.length() > 0) 
               return myCookie;
            }
          catch (Exception e){
            System.out.println("Navigated away from page");
            }
          return "?";
      
    }
    public void post_file() {
    	// getCookie();
        while (my_is_uploading && (retries < RETRY_MAX)) {
	        String filename = current_filename;
	        String alias = current_alias;
	        try {
	            // Construct data
	            // String cookie_value = getParameter("cookie");
	            ++retries;
	            URL url = getCodeBase();
	            String my_form_token = getParameter("form_token");
	            String my_path = getParameter("path");
	            my_debug = getParameter("debug").contentEquals("TRUE") ?
	            		true : false;
	            InetAddress ina = InetAddress.getByName(url.getHost());
	            Socket sock = new Socket(ina,80);
	            OutputStream os = sock.getOutputStream();
	            
	            InputStream is = new FileInputStream(new File(filename));
	            int gif_size = is.available();
	            String form1 = getForm1(alias,my_form_token);
	            String form2 = getForm2();
	            int size = gif_size + form1.length() + form2.length();
	            String output = getHeader(size,url,my_path);
	            
	            byte gifbyte[] = new byte[gif_size];
	            int i = 0;
	            while (is.available() > 0) {
	                gifbyte[i] = (byte)is.read();
	                ++i;
	                bandwidth = i;
	            }
	            byte form1byte[] = form1.getBytes();
	            byte form2byte[] = form2.getBytes();
	            os.write(output.getBytes());
	            os.write(form1byte);
	            os.write(gifbyte);
	            os.write(form2byte);
	            
	            // Get response
	            BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	            if (my_debug) {
		            String line;
		            while ((line = rd.readLine()) != null) {
		                System.out.println("line: " + line);
		            }
	            }
	            os.close();
	            rd.close();
	            my_is_uploading = false;
	            retries = 0;
	            
	        } catch (Exception e) { 
	        	System.out.println("exception: " + e.toString());
	        	my_is_uploading = false;
	        }
        }
    }
    
    public void start() {
        gui_names.default_width = new Integer(getParameter("scale_width")).intValue();
        gui_names.default_height = new Integer(getParameter("scale_height")).intValue();
        
        //Execute a job on the event-dispatching thread:
        //creating this applet's GUI.
        try {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't successfully complete, exception is " + 
                    e.getMessage() + " string: " + e.toString());
        }

    }
    public void init() {
    }
    org.aaron.image_folder.image_folder my_folder = null;
    
    private void stopTimer() {
    	if (my_folder != null)
    		my_folder.stopTimer();
    }
    private void createGUI() {        
        //Create the text field and make it uneditable.
        // org.aaron.image_folder.view_panel vp = (new org.aaron.image_folder.image_folder(this)).get_panel();
        
        //Set the layout manager so that the text field will be
        //as wide as possible.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame my_frame = new JFrame("Picture Downloader");
        my_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        my_folder = new image_folder(this); 
        
        my_frame.setContentPane(my_folder.get_panel());
        my_frame.pack();
        my_frame.setVisible(true);
        my_frame.setSize(gui_names.default_width,gui_names.default_height);

        /**
         * Try to avoid confusing the user and disable navigation buttons
         * while the upload is taking place.
         */
        setFormButtons(false);
        
        my_frame.addWindowListener(new WindowListener() {
        	 public void 	windowActivated(WindowEvent e) {}
        	 public void 	windowClosed(WindowEvent e) {setFormButtons(true);stopTimer();}
        	 public void 	windowClosing(WindowEvent e){}
        	 public void 	windowDeactivated(WindowEvent e){}
        	 public void 	windowDeiconified(WindowEvent e){}
        	 public void 	windowIconified(WindowEvent e){}
        	 public void 	windowOpened(WindowEvent e){}         	
        });

        my_folder.openFileDialog();
    }

}
