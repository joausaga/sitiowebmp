/*
 * sax parser for general xml parsing.
 */

package org.aaron.xml;

import java.io.*;
import org.xml.sax.*;
import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the java code that parses the XML document.
 * 
 * Most of what's in here now is just the generic Sun API for the DefaultHandler
 * class. XML doc elements that this application cares about are stored away.
 */
public class sax_parser extends DefaultHandler
{
    public xml_tree myCurrentTree;
    int myNestingLevel = 0;
    /**
     * Receive notification of character data inside an element.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method to take specific actions for each chunk of character data
     * (such as adding the data to a node or buffer, or printing it to
     * a file).</p>
     *
     * @param buf The characters.
     * @param offset The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#characters
     */
    public void characters(char buf[], int offset, int length) throws SAXException
    {
        String s = new String(buf, offset, length);
        if (myCurrentTree != null)
        {
            myCurrentTree.SetValue(s);
            // Only log non-empty strings.
            if (s.trim().length() > 0)
            {
                showData(s);
            }
        }
    }

    /**
     * Receive notification of the end of the document.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end
     * of a document (such as finalising a tree or closing an output
     * file).</p>
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#endDocument
     */
    public void endDocument() throws SAXException
    {
    }
    /**
     * Receive notification of the beginning of the document.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the beginning
     * of a document (such as allocating the root node of a tree or
     * creating an output file).</p>
     *
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startDocument
     */
    public void startDocument() throws SAXException
    {
    }
    /**
     * Receive notification of the end of an element.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the end of
     * each element (such as finalising a tree node or writing
     * output to a file).</p>
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#endElement
     */
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        showData ("</"+qName+">\n");
        if ((myCurrentTree != null) &&
            (myNestingLevel > 1))
        {
            myCurrentTree = myCurrentTree.GetParentNode();
        }
        --myNestingLevel;
    }
    /**
     * Receive notification of the start of an element.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method in a subclass to take specific actions at the start of
     * each element (such as allocating a new tree node or writing
     * output to a file).</p>
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param qName The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ContentHandler#startElement
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if (myCurrentTree == null)
        {
            myCurrentTree = new xml_tree(qName,attributes);
        }
        else 
        {
            myCurrentTree = myCurrentTree.AddSubtree(qName,attributes);
        }
        StringBuffer tag = new StringBuffer("<").append(qName);
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength (); i++) {
                tag.append (" ");
                tag.append(attributes.getQName(i)).append("=\"").append(
                    attributes.getValue(i)).append("\"");
            }
        }
        myNestingLevel++;
        tag.append(">");
        showData(tag.toString());
    }
    /**
     * Receive notification of a parser warning.
     *
     * <p>The default implementation does nothing.  Application writers
     * may override this method in a subclass to take specific actions
     * for each warning, such as inserting the message in a log file or
     * printing it to the console.</p>
     *
     * @param e The warning information encoded as an exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ErrorHandler#warning
     * @see org.xml.sax.SAXParseException
     */
    public void warning(SAXParseException e) throws SAXException
    {
        throw(new SAXException(e));
    }
    /**
     * Receive notification of a recoverable parser error.
     *
     * <p>The default implementation does nothing.  Application writers
     * may override this method in a subclass to take specific actions
     * for each error, such as inserting the message in a log file or
     * printing it to the console.</p>
     *
     * @param e The warning information encoded as an exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ErrorHandler#warning
     * @see org.xml.sax.SAXParseException
     */
    public void error(SAXParseException e) throws SAXException
    {
        throw(new SAXException(e));
    }
    /**
     * Report a fatal XML parsing error.
     *
     * <p>The default implementation throws a SAXParseException.
     * Application writers may override this method in a subclass if
     * they need to take specific actions for each fatal error (such as
     * collecting all of the errors into a single report): in any case,
     * the application must stop all regular processing when this
     * method is invoked, since the document is no longer reliable, and
     * the parser may no longer report parsing events.</p>
     *
     * @param e The error information encoded as an exception.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see org.xml.sax.ErrorHandler#fatalError
     * @see org.xml.sax.SAXParseException
     */
    public void fatalError(SAXParseException e) throws SAXException
    {
        throw(new SAXException(e));
    }

    /**
     * ctor.  Just get our writer, which will contain our output. 
     * Currently the writer is only used for debugging output.
     * 
     */
    public sax_parser()
    {

    }


    //===========================================================
    // Helpers Methods
    //===========================================================

    public void showData(String s)
    {
        // loggerXml.log(Level.ALL,s);
    }
}
