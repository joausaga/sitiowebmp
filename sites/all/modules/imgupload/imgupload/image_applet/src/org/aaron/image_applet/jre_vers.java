package org.aaron.image_applet;

import javax.swing.JApplet;
import java.awt.*;
import java.util.*;

public class jre_vers extends JApplet {
   boolean jre_ok = true;
   public jre_vers() {
	   verify_version();
   }

   String msg;
   private void verify_version()
   {
       final String version = System.getProperty("java.version");
       final StringTokenizer st = new StringTokenizer(version,".");
       final String major = st.nextToken(); // major version number (i.e. 1)
       final String minor = st.nextToken(); // minor version number
       String release = null;
       if (st.hasMoreTokens()) {
         release = ""+st.nextToken().charAt(0); // release number
       }
       msg = "Java version is  "+ version + " > 1.5.  You are good to go.";
       System.out.println(major + " " + minor + " " + release);
       if ((Integer.parseInt(major) == 1) &&
           (Integer.parseInt(minor) < 5)) {
           msg = "Error!  JRE version < 1.5." +
               " The applet requires version 1.5. " + 
               "Visit http://java.sun.com .";
           jre_ok = false;
       }
   }

   public void init() {
	   verify_version();
       repaint();
   }
   
   public void paint(Graphics g) {
		//Draw a Rectangle around the applet's display area.

		//Draw the current string inside the rectangle.
	   if (jre_ok == true)
	      g.setColor(new Color(220,255,220));
	   else {
          g.setColor(new Color(255,220,220));
          Font font = g.getFont();
          int style = font.getStyle();
          style |= Font.BOLD;
          g.setFont(new Font(font.getName(),style,font.getSize()));
	   }

	   g.fillRect(0, 0, 
		   getWidth() - 1,
		   getHeight() - 1);

 	   g.setColor(new Color(0,0,0));
       g.drawRect(0, 0, 
				   getSize().width - 1,
				   getSize().height - 1);
	        g.drawString(msg, 10,getSize().height/2);
    }
}
