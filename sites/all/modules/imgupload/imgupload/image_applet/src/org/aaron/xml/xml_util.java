
package org.aaron.xml;

import java.awt.GridBagConstraints;
import java.lang.reflect.*;

/**
 * Some string utilities to make parse/output of xml easier.
 * @author Owner
 *
 */
public class xml_util {

    public static final StringBuffer gt = new StringBuffer(">");
    public static StringBuffer lt = new StringBuffer("<");
    public static StringBuffer slash = new StringBuffer("/");
    public static StringBuffer cr = new StringBuffer("\n");
    public static StringBuffer sb() {return new StringBuffer();}

    public static StringBuffer tagit(StringBuffer tag,StringBuffer text)
    {
        StringBuffer rr = sb().append(lt).append(tag).append(gt).append(text).append(lt);
        // System.out.println("val is " + rr.toString());
        rr.append(slash).append(tag).append(gt).append(cr);
        // System.out.println("val is " + rr.toString());
        return rr;
    }

    /**
     * Dump object to XML file, which could be read in and 
     * converted to a java object (kind of like SOAP).
     * @param obj
     * @return
     */
    public static String getXML(Object obj)
    {
        Field[] fields = obj.getClass().getFields();
        Class<?> clazz = obj.getClass();
        String class_name = clazz.getName();
        StringBuffer sb = new StringBuffer();
        
        for (Field field : fields) {
            try {
            Object fobj = field.get(obj);
            if (fobj != null)
                sb.append(getXMLRecurse(fobj,field.getName(),false));
            } catch (IllegalAccessException iae){}
        }
        
        return tagit(sb2(class_name),sb).toString();
    }

    private static String getNonArrayName(Class<?> cl) {
        String name = cl.getName();
        if (cl.isArray()) {
            name = name.replaceFirst("^L+", "");
        }
        return name;
    }
    
    private static String getNonArraySimpleName(Class<?> cl) {
        String name = cl.getSimpleName();
        if (cl.isArray()) {
            name = name.replace("[]", "");
        }
        return name;
    }
    
    private static boolean Modifiable(Field field) {
        String mods = Modifier.toString(field.getModifiers());
        if (mods.contains("abstract") ||
            mods.contains("static") ||
            mods.contains("final") ||
            mods.contains("private") ||
            mods.contains("transient") ||
            mods.contains("volatile")) {
            return false;
        }
        return true;
    }

    private static StringBuffer sb2(int x) {
        return new StringBuffer(new Integer(x).toString());
    }
    private static StringBuffer sb2(double x) {
        return new StringBuffer(new Double(x).toString());
    }
    private static StringBuffer sb2(String s) {
        return new StringBuffer(s);
    }
    private static StringBuffer getGridBagAsString(GridBagConstraints gbc) {
        StringBuffer sb = new StringBuffer();
        sb.append(tagit((new StringBuffer("gridx")),(sb2(gbc.gridx))));
        sb.append(tagit((new StringBuffer("gridy")),(sb2(gbc.gridy))));
        sb.append(tagit((new StringBuffer("weightx")),(sb2(gbc.weightx))));
        sb.append(tagit((new StringBuffer("weightx")),(sb2(gbc.weighty))));
        sb.append(tagit((new StringBuffer("anchor")),(sb2(gbc.anchor))));
        sb.append(tagit((new StringBuffer("fill")),(sb2(gbc.fill))));
        
        return sb;
    }
    
    private static StringBuffer getXMLRecurse(Object obj,String tag_name,boolean suppress_tag)
    {
        if (obj != null)
        {
        //try {
            Class<?> cl =obj.getClass();
            Field[] fields = cl.getFields();
            StringBuffer tag = new StringBuffer(tag_name);
            StringBuffer nested = new StringBuffer();

            String cname = obj.getClass().getName();
            // Is this a leaf node (value of String)?  If 
            // so we just return the value without tagging it.
            if (cname.contentEquals("java.lang.String") || cname.contentEquals("int") ||
                    cname.contentEquals("long") || (cname.contentEquals("double")))  
            {
                return new StringBuffer(obj.toString());
            } else if (cname.contentEquals("java.awt.GridBagConstraints")) {
                return tagit(sb2(tag_name),getGridBagAsString((GridBagConstraints)obj));
            }

            // If this is an array, remove the [] from the 
            // title as per java spec.

            if (cl.isArray())
            {
                String str_tag = getNonArraySimpleName(cl);
                if (str_tag.contentEquals("String")) {
                    for (int j=0;j<Array.getLength(obj);++j) {
                        nested.append(tagit(new StringBuffer(str_tag),
                                            new StringBuffer(Array.get(obj,j).toString())));
                    }
                }
                else {
                    for (int j=0;j<Array.getLength(obj);++j)
                    {
                        nested.append(getXMLRecurse(Array.get(obj,j),tag_name,
                                                    false));
                    }
                }
                suppress_tag = true;
            }

            // Otherwise, we tag the value.
            for (int i = 0;i < fields.length;++i)
            {
                Field field = fields[i];
                if (Modifiable(field) == false) {
                    continue;
                }
                Object inner_obj = null;
                try {
                    inner_obj = field.get(obj);
                    if (inner_obj != null) {
                        // String field_type_name = field.getType().getName();
                        String tmpName = field.getName();
                        nested.append(tagit(new StringBuffer(tmpName),
                                           getXMLRecurse(inner_obj,tmpName,false)));
                    }
                }
                catch (IllegalAccessException ie) { System.out.println("Illegal access!!");}
            }
            if (suppress_tag == false)
            {
                return tagit(tag,nested);
            }
            else {
                return nested;
            }
        //} // end of try block
        //catch (java.io.IOException le){
          //  System.out.println("Cast failed: " + le.getMessage());
        //    }
      
        }
        // Must have been null object
        return new StringBuffer();
    }

    public static final String hdr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    
}
