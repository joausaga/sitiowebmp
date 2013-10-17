package org.aaron.image_folder;

import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.util.*;

/**
 * Table model for a table.  This is not used because the 
 * GUI for the module uses normal windows not tables.
 * 
 * @author aaron
 *
 */
public class image_TableModel extends AbstractTableModel {

    TableModelListener my_listener;
    HashMap<Integer,view_panel> contents = new HashMap<Integer,view_panel>();
    image_TableModel() {
        System.out.println("in ctor");
    }
    @Override
    public void addTableModelListener(TableModelListener arg0) {
        my_listener = arg0;
        // TODO Auto-generated method stub
    }

    @Override
    public Class<?> getColumnClass(int arg0) {
        try {
        if (arg0 == 0) {
            Class<?> cl = Class.forName("org.aaron.image_folder.view_panel");
            return cl;
        }
        } catch (ClassNotFoundException cnfe) {
            System.out.println("unknown column type " + cnfe.toString());
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int arg0) {
        if (arg0 == 0) {
            return "Image";
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return contents.size();
    }

    @Override
    public Object getValueAt(int arg0, int arg1) {
        if (arg1 == 0) {
            return contents.get(new Integer(arg0).intValue());
        }
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCellEditable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void removeTableModelListener(TableModelListener arg0) {
        my_listener = null;
    }

    @Override
    public void setValueAt(Object arg0, int arg1, int arg2) {
        if ((arg2 == 0) && (arg0 instanceof view_panel)) {
            view_panel vp = (view_panel) arg0;
            fireTableCellUpdated(arg1, arg2);
            contents.put(new Integer(arg1), vp);
        }
    }
}
