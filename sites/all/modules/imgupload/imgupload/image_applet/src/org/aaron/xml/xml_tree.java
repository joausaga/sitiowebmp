

package org.aaron.xml;



import java.lang.String;
import java.util.HashMap;

import java.lang.reflect.*;

/**
 * This is an abstraction of an XML document tree.   Probaby this should 
 * just use DOM... 
 * 
 * <p>The document is in tree form.  If you have an xml_tree instance,
 * you have the root node of a subtree.  You can get to the very top
 * of the subtree, and hence the root node of the XML document,
 *  by calling .</p>
 */
public class xml_tree
{
    /**
     * The stuff in the <> tags.
     * */
    private String myTag;

    /**
     * The stuff between the tags.  SAX calls these 'characters'.
     * */
    private String myValue;

    private org.xml.sax.Attributes myAttributes;
    
    private String package_name;

    /**
     * The parent tree will keep track if unique node IDs that can be used to
     * create parent relationships.
     */
    HashMap<String,xml_tree> nid_tree;
    
    private void set_package_name() {
        for (int ix = 0; myAttributes != null && ix <myAttributes.getLength(); ++ix) {
            String name = myAttributes.getQName(ix);
            String value =myAttributes.getValue(ix);
            if (name.contentEquals("package")) {
                package_name = value;
                return;
            }
        }
        
        if (this.myParent != null) {
            package_name = myParent.package_name;
        }
    }
    /**
     * The child nodes are stored as an array
     * */
    private xml_tree[] myChildren;

    /**
     * The parent for traversing the tree
     * */
    private xml_tree myParent;

    public enum node_type {
        Single,
        Multiple,
        Optional;

        public static node_type fromString(String str)
        {
            for (node_type p : node_type.values())
            {
                if (p.toString().matches(str))
                {
                    return p;
                }
            }
            return Single;
        }
    }

    /**
     * Accessor the the child nodes.
     * @return the nodes
     * */
    public xml_tree[] GetChildren() 
    {
        return myChildren;
    }

    /**
     * Create a new xml document with root node theTag
     * 
     * @param theTag
     */
    public xml_tree(String theTag,org.xml.sax.Attributes attributes)
    {
        myTag = theTag;
        myAttributes = attributes;
        this.set_package_name();
    }

    /**
     * We have received some characters between the tags.  
     * Store them
     * */
    public void SetValue(String s)
    {
        if (myValue == null)
        {
            myValue = s;
        }
        else
        {
            myValue += s;
        }
    }

    /** 
     * If this XML doc represents a known java structure, this method
     * will return a populated structure that can be cast to that type
     * Used in message marshaling - XML converts to an instance ofa class,
     * and visa-versa
     * **/
    public Object getStructure()
    {
        Class<?> cl = null;
        Object obj = null;
        try {
            cl = getClassFromTag();
            obj = cl.newInstance();
            getStructureRecurse(obj);
        }
        catch (ClassNotFoundException cnfe){
            System.out.println("cnfe is " + cnfe.getMessage());
            cnfe.printStackTrace();
        } catch (IllegalAccessException iae) {
            System.out.println("iae is " + iae.getMessage());
            iae.printStackTrace();            
        }
        catch (InstantiationException ie) {
            System.out.println("ie is " + ie.getMessage());
            ie.printStackTrace();            
        }

        return obj;
    }

    private Class<?> getClassFromTag() throws ClassNotFoundException {
        try {
            Class<?> cl = Class.forName(myTag);
            return cl;
        }
        catch (ClassNotFoundException t) {  }
        
        try {
            String name = package_name + "." + myTag;
            Class<?> cl = Class.forName(name);
            return cl;
        }
        catch (ClassNotFoundException t) {
            String name = package_name + "." + myTag;
            System.out.println("xml_tree: not found: name = " + name);
            return null;
        }
    }
    /** 
     * Does the work of getStructure.  Recursively go through a xml doc
     * and try to convert it into a java class, where the tag names of the 
     * XML match the field names of the structure.
     * **/
    private void getStructureRecurse(Object parent) {
        Class<?> cl = parent.getClass();
        Field[] fields = cl.getFields();
        String parent_name = cl.getName();
        int child_index = 0;
        /** If this is an array context, we want to only go through the 
         * children at this array level.  If not an array, we assume that 
         * all the children are unique
         * **/
        for (child_index = 0;
              (myChildren != null) && (child_index < myChildren.length);
              ++child_index)
        {
            xml_tree tmp_subtree = myChildren[child_index];
            String new_tag_string = parent_name;
            new_tag_string = parent_name + "$" + tmp_subtree.GetTag();

            /**
             * I have some number of children, some of the sub-fields of this object
             * might match children of mine.  The other fields are left null.  e.g.
             * XML:
             *  A---\
             *   ---D
             *   
             * Object:
             * A--\
             *  --B
             *  --C
             *  --D     <-- we will return A.D but A.B will be null
             * **/
            Object inner_obj = loopThroughFields(fields,parent,new_tag_string,child_index);
            if (inner_obj != null)
            {
                /**
                 * If this is an array object, we have already looped through
                 * the children that are instances of the array.  For instance:
                 * A---\
                 *  ---B
                 *  ---B
                 *  ---B
                 *  ---C
                 * 
                 * Array of B, array processing would handle children 0-2.
                 * **/
                if (inner_obj.getClass().isArray())
                {
                    child_index += Array.getLength(inner_obj);
                }
                if (child_index < myChildren.length)
                {
                    myChildren[child_index].getStructureRecurse(inner_obj);
                }

                // If this is a leaf node with a string value,
                // assign it to the structure.
            }
        }
    }
    
    private String getNonArrayName(Class<?> cl) {
        String name = cl.getName();
        if (cl.isArray()) {
            name = name.replaceFirst("^L+", "");
        }
        return name;
    }
    
    private String getNonArraySimpleName(Class<?> cl) {
        String name = cl.getSimpleName();
        if (cl.isArray()) {
            name = name.replace("[]", "");
        }
        return name;
    }

    private void err(Exception e) {
        System.out.println("Exception in xml_tree: " + e.toString());
        e.printStackTrace();
    }
    private boolean setTerminalValue(Object parent,String tag,Field parent_field) {
        Field[] fields = parent.getClass().getFields();
        for (Field field:fields) {
            if (field.getName().contentEquals(tag) && 
                    Modifier.toString(field.getModifiers()).contains("final"))
            {
                try {
                    Object obj = field.get(parent);
                    parent_field.set(parent, obj);
                } 
                catch (IllegalAccessException iae) {err(iae);}
                return true;
            }
        }
        return false;
    }
    /**
     * The object has a set of fields and the xml doc has a tag.  Find out if one of the fields
     * matches one of the tag names, and if so instantiate the field in the object and assign
     * its value according to the value of this tag and child nodes (recursively).
     * **/
    private Object loopThroughFields(Field[] fields,Object parent,String new_tag_string,int child_index)
    {
        Object inner_obj = null;
        for (int field_index = 0;field_index < fields.length;++field_index)
        {
            try {
            Field field = fields[field_index];
            String inner_field_name = field.getName();
            Class<?> inner_class = field.getType();
            boolean isArray = inner_class.isArray();
            String inner_class_name = getNonArrayName(inner_class);
            String parent_class_name = getNonArrayName(parent.getClass());
            
            /**
             * Is this field a nested class or a separate class?
             */
            String simple_name = getNonArraySimpleName(inner_class);
            boolean is_nested_class = doesArrayMatchNonArrayName(inner_class_name,
                    new String(parent_class_name).concat("$").concat(simple_name));

            /**
             * Does the field name match the XML tag name? If so, get the values
             * from the child XML node.
             */
            if (inner_field_name.contentEquals(myChildren[child_index].GetTag())) {               
                /**
                 * If this is a simple type, the value of the string is the value of
                 * the child (leaf) node. Just set and return for the next field.
                 */
                if (isArray == false) {
                    if (inner_class_name.contentEquals("java.lang.String") ||
                         inner_class_name.contentEquals("java.lang.Integer"))
                    {
                        Constructor<?> ctor = inner_class.getDeclaredConstructor(Class.forName("java.lang.String"));
                        field.set(parent,ctor.newInstance(myChildren[child_index].GetValue()));
                        break;
                    } else if (inner_class_name.contentEquals("int")) {
                        if (setTerminalValue(parent,myChildren[child_index].GetValue(),field) == false)
                            field.setInt(parent, new Integer(myChildren[child_index].GetValue()).intValue());
                        break;
                    }else if (inner_class_name.contentEquals("long")) {
                        if (setTerminalValue(parent,myChildren[child_index].GetValue(),field) == false)
                            field.setLong(parent, new Long(myChildren[child_index].GetValue()).longValue());
                        break;
                    }else if (inner_class_name.contentEquals("boolean")) {
                        if (setTerminalValue(parent,myChildren[child_index].GetValue(),field) == false)
                            field.setBoolean(parent, new Boolean(myChildren[child_index].GetValue()).booleanValue());
                        break;
                    }else if (inner_class_name.contentEquals("double")) {
                        if (setTerminalValue(parent,myChildren[child_index].GetValue(),field) == false)
                            field.setDouble(parent, new Double(myChildren[child_index].GetValue()).doubleValue());
                        break;                    
                    } 
                    else {
                        Constructor<?>[] ctors = inner_class.getDeclaredConstructors();
                        if (is_nested_class)
                            inner_obj = ctors[0].newInstance(parent);
                        else 
                            inner_obj = ctors[0].newInstance();
                            
                        field.set(parent,inner_obj);
                        break;               
                    }
                }
                else if ((isArray == true) && 
                        (doesArrayMatchNonArrayName(inner_class_name,"java.lang.String"))) {
                    
                    /**
                     * Not sure if this is right.  Why are string arrays treated specially?
                     */
                    Class<?> array_type = inner_class.getComponentType();
                    int array_size = countChildrenWithTag(myChildren[child_index].GetTag());
                    Object ar_obj = Array.newInstance(array_type,array_size);
                    field.set(parent,ar_obj);
                    for (int ar_index = 0;
                          ar_index < array_size;
                          ++ar_index)
                    {
                        Object ar_inst = myChildren[child_index + ar_index].GetValue();
                        Array.set(ar_obj,ar_index,ar_inst);
                    }
                    inner_obj = ar_obj;
                    break;
                } else if (isArray == true) {
                    Class<?> array_type = inner_class.getComponentType();
                    int array_size = countChildrenWithTag(myChildren[child_index].GetTag());
                    Object ar_obj = Array.newInstance(array_type,array_size);
                    field.set(parent,ar_obj);
                    fillInArrayType(ar_obj,array_type,parent,child_index,is_nested_class);
                    inner_obj = ar_obj;
                }
                /**
                 * Else this is a nested class with a default constructor. Create
                 * the new object and fill in the values from the subsequent fields.
                 */
            }
            if ((inner_class_name.contentEquals(new_tag_string)) &&
                    (isArray == false))
            {
                Constructor<?>[] ctors = inner_class.getDeclaredConstructors();
                inner_obj = ctors[0].newInstance(parent);
                field.set(parent,inner_obj);
                break;
            } // end if
            else if ((isArray == true) &&
                     (doesArrayMatchNonArrayName(inner_class_name,new_tag_string)))
            {
                Class<?> array_type = inner_class.getComponentType();

                // We need to figure out which of the child nodes are fields in
                // the array type vs additional instances in the array.  In xml 
                // form they are all at the same level.
                if (myChildren[child_index].GetChildren() == null) {
                    System.out.println("myChildren Children are 0!\n");
                } else {
                    int array_size = countChildrenWithTag(myChildren[child_index].GetTag());
                    Object ar_obj = Array.newInstance(array_type,array_size);
                    field.set(parent,ar_obj);
                    fillInArrayType(ar_obj,array_type,parent,child_index,is_nested_class);
                    inner_obj = ar_obj;
                }
                break;
            }
            } // end try
            catch (InstantiationException ie) {
                System.out.println("th is th" + ie.getMessage());
                ie.printStackTrace();
            }
            catch (IllegalAccessException iae) {
                System.out.println("iae is " + iae.getMessage());
                iae.printStackTrace();
            } catch (InvocationTargetException ite) {
                System.out.println("ite is " + ite.getMessage());
                ite.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                System.out.println("cnfe is " + cnfe.getMessage());
                cnfe.printStackTrace();
            } catch (NoSuchMethodException nsme) {
                System.out.println("nsme is " + nsme.getMessage());
                nsme.printStackTrace();
            } catch (Exception e) {
                err(e);
            }
            
        } // end for field...

        return inner_obj;
    }

    private int countChildrenWithTag(String tag) {
        int rv = 0;
        for (int i = 0;i < myChildren.length;++i) {
            if (myChildren[i].GetTag().contentEquals(tag)) {
                ++rv;
            }
        }
        return rv;
    }

    private void fillInArrayType(
            Object ar_obj,
            Class<?> array_type,
            Object parent,
            int child_index,
            boolean is_nested_class)
    throws IllegalAccessException,InvocationTargetException,InstantiationException
    
    {
        for (int ar_index = 0;ar_index < Array.getLength(ar_obj);++ar_index)
        {
            Constructor<?>[] ar_ctor = array_type.getConstructors();
            Object ar_inst = null;
            if (is_nested_class)
                ar_inst = ar_ctor[0].newInstance(parent);
            else 
                ar_inst = ar_ctor[0].newInstance();
            
            Array.set(ar_obj,ar_index,ar_inst);
            myChildren[child_index + ar_index].getStructureRecurse(ar_inst);
        }
    }

    private String removeArrayChars(String ar) {
        int index = ar.indexOf("L") + 1;
        if (index > 0)
            ar = (ar.subSequence(index,ar.length() - 1)).toString();
        /* remove array stuff from a simple class name */
        index = ar.indexOf("[]");
        if (index > 0)
            ar = (ar.subSequence(0,index)).toString();
        
        return ar;
    }
    private boolean doesArrayMatchNonArrayName(String ar,String nonar)
    {
        /* Remove array stuff from a fully qualified class name */
        ar = removeArrayChars(ar);
        nonar = removeArrayChars(nonar);

        return ar.contentEquals(nonar);
    }

    public static xml_tree subtree(xml_tree the_tree,String tag,int index)
    {
        if ((the_tree != null) &&
            (the_tree.GetTag() != null) &&
            (the_tree.GetChildren() != null) &&
            (the_tree.GetChildren().length > index) &&
            (the_tree.GetTag().matches(tag)))
        {
            return the_tree.GetChildren()[index];
        }
        return null;
    }

    public static boolean exists(xml_tree the_tree,String tag,int index)
    {
        return subtree(the_tree,tag,index) != null;
    }
    /**
     * We received a new tag, which will become one of the child nodes
     * for the current subnode.  At the new node to the list of children
     * and return the CHILD NODE.
     * 
     * @param theTag
     * 
     * @return The node that we have just added to the tree.
     */
    public xml_tree AddSubtree(String theTag,org.xml.sax.Attributes attributes)
    {
        int tmpCurrentLen = 0;
        if (myChildren != null)
        {
            tmpCurrentLen = myChildren.length;
        }

        xml_tree[] tmpNewArray = new xml_tree[tmpCurrentLen + 1];
        if (myChildren != null)
        {
            System.arraycopy(myChildren,0,tmpNewArray,0,tmpCurrentLen);
        }

        myChildren = tmpNewArray;
        tmpNewArray[tmpCurrentLen] = new xml_tree(theTag,attributes);
        tmpNewArray[tmpCurrentLen].myParent = this;
        tmpNewArray[tmpCurrentLen].package_name = package_name;
        return tmpNewArray[tmpCurrentLen];
    }

    /**
     * Iterate up to the top of the tree and return the root node.
     * 
     * @return The root node of the tree.
     */
    public xml_tree GetRootNode()
    {
        xml_tree tmpTree = this;
        while (tmpTree.myParent != null)
        {
            tmpTree = tmpTree.myParent;
        }
        return tmpTree;
    }

    /**
     * Recurse and print the document tree to the writer that is passed in.
     * 
     */
    public StringBuffer PrintTree()
    {
        return (PrintSubTree(GetRootNode(),0,new String("")));
    }

    /**
     * A recursive helper function that is used by PrintTree.
     * 
     * @param theTree    The current node of the subtree to print
     * @param theWriter  the output place for the text
     * @param theNesting The nesting level.
     * @param theNestingString
     *                   A string of spaces for tabular output.
     */
    private static StringBuffer PrintSubTree(xml_tree theTree,int theNesting,String theNestingString)
    {
        if (theTree != null)
        {

            if (theTree.myChildren == null)
            {
                if (theTree.myValue == null)
                {
                    return (xml_util.tagit(new StringBuffer(theTree.myTag),
                              new StringBuffer("")));
                }else
                {
                    return (xml_util.tagit(new StringBuffer(theTree.myTag),
                              new StringBuffer(theTree.myValue)));
                }
            }
            
            else
            {
                int i;
                StringBuffer sb = new StringBuffer();
                for (i = 0;i < theTree.myChildren.length;++i)
                {
                    sb.append(PrintSubTree(theTree.myChildren[i],
                                           theNesting + 1,
                                           theNestingString));
                }
                return xml_util.tagit(new StringBuffer(theTree.myTag),sb);
            }
        }
        return new StringBuffer();
    }
    /**
     * Return myParent tree.
     * 
     * @return xml_tree
     */
    public xml_tree GetParentNode()
    {
        return myParent;
    }

     /**
     * Recursive helper function used by GetSubtree
     * **/
    private xml_tree GetSubtreeFromChildren(String theTagName)
    {
        int l = myChildren.length;
        int i;

        for (i = 0;i < l;++i)
        {
            xml_tree tmpSubtree = myChildren[i];
            if (tmpSubtree.myTag == theTagName)
            {
                return tmpSubtree;
            }
            tmpSubtree = myChildren[i].GetSubtreeFromChildren(theTagName);
            if (tmpSubtree != null)
            {
                return tmpSubtree;
            }
        }

        return null;
    }

    /**
     * Recursive helper function used by GetSubtree
     * **/
    private xml_tree GetSubtreeFromImmediateChildren(String theTagName)
    {
        int l = myChildren.length;
        int i;

        for (i = 0;i < l;++i)
        {
            xml_tree tmpSubtree = myChildren[i];
            if (tmpSubtree.myTag == theTagName)
            {
                return tmpSubtree;
            }
        }

        return null;
    }

    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private static void showData (String s)
    {
        // loggerXml.log(Level.ALL,s);        
    }

    /**
     * public accessor function to return the root tag of this 
     * subtree
     * **/
    public String GetTag() {if (myTag != null)
        return new String(myTag);
    else
        return new String();
    }

    /**
     * public accessor to return the element text for this 
     * node.
     * **/
    public String GetValue() {if (myValue != null) 
        return new String(myValue);
    else
        return new String();
    }

    /**
     * Convenience function to split tags into substrings. e.g.
f     * &lt;FOO><BAR> will return "FOO" and "BAR"
     * **/
    public static String[] SplitTags(String Tags)
    {
        char[] chars = Tags.toCharArray();
        int i = 0;
        int l = Tags.length();
        int tmpTagCount = 0;
        for (i = 0;i < l;++i)
        {
            if (Tags.charAt(i) == '>')
            {
                ++tmpTagCount;
            }
        }

        String [] tmpRV = new String[tmpTagCount];
        int tmpStartIndex = 0;
        for (i = 0;i < tmpRV.length;++i)
        {
            int start_char = Tags.indexOf(new String ("<"),tmpStartIndex);
            int end_char = Tags.indexOf(new String (">"),tmpStartIndex);
            if ((start_char > 0) && (end_char > 0))
            {
                tmpRV[i] = new String(chars,start_char,end_char);
                tmpStartIndex = end_char;
            }
            else
            {
                break;
            }
        }
        return tmpRV;
    }
}
