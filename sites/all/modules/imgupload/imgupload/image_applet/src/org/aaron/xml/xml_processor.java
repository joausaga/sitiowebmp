package org.aaron.xml;

import java.io.*;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.util.logging.*;
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * An interface that can process an xml_tree.  Used when
 * calling the bytes_to_xml_tree interface, which takes care
 * of the SAX parser and the input stream.
 * **/
public class xml_processor
{
    private InputStream stream;
    /**
     * The XML document has been parsed.  Return the parent object.
     * **/
    // public xml_processor(String filename) {
    //     my_file = new File(filename);
    // }
    public xml_processor(InputStream file) {stream = file;}
    
    private void err(Exception e) {
        System.out.println("Exception: " + e.toString());
    }
    
    public xml_tree parse() {
        try {
            // FileInputStream stream = new FileInputStream(my_file);
            
            SAXParser saxParser = null;
            SAXParserFactory factory = SAXParserFactory.newInstance(  );
            saxParser = factory.newSAXParser(  );
            sax_parser p = new sax_parser();
            saxParser.parse(stream,p);
            return p.myCurrentTree;
        }
        catch (FileNotFoundException ec) { err(ec);    }
        catch (SAXException e)  {err(e);}
        catch (ParserConfigurationException e)  {err(e);}
        catch (IOException ioe) {err(ioe);}
        
        return null;
    }
}
