package org.aaron.image_folder;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import javax.swing.table.*;

import javax.swing.ListCellRenderer;

/**
 * This is the structure that comes from an XML document that defines a GUI.
 * From this structure a view_panel can be notified. This viewpanel can be 
 * the main window in a Java frame window.
 * 
 * @author Owner
 *
 */
public class pane_info {
	public String name = "info";
	// public class subpanel {
        public java.awt.GridBagConstraints gbc = new GridBagConstraints();
    	public class panel_element_info {
    		public class button_info {
    			public String name;
    			public String text;
    			public java.awt.GridBagConstraints gbc = new GridBagConstraints();
    		}
    		public button_info button_info_inst;
    		
    		public class textField_info {
    		    public String name;
    		    public String text;
    		    public String value;
    		    public boolean enable;
    		    public java.awt.GridBagConstraints gbc = new GridBagConstraints();
    		}
    		public textField_info textField_info_inst;
    		
            public class listBox_info {
                public String theName;
                public String list_cell_renderer;
                public String[] choices;
                public GridBagConstraints gbc = new GridBagConstraints();           
            }
            public listBox_info listBox_info_inst;
            
            public class listBox2_info {
                public String theName;
                public String[] choices;
                public GridBagConstraints gbc = new GridBagConstraints();           
            }
            public listBox2_info listBox2_info_inst;
            
            public class table_info {
                public String name;
                public int num_rows;
                public int num_columns;
                public String model;
                public GridBagConstraints gbc = new GridBagConstraints();           
                public String[] columns;
            }
            public table_info table_info_inst;
            
    	    public class checkBox_info {
    	    	public String name;
    	    	public String label;
    	    	public boolean value=false;
    	    	public GridBagConstraints gbc = new GridBagConstraints();	    	
    	    }
    	    public checkBox_info checkBox_info_inst;
    	    
    	    public class statusField_info {
    	        public String status;
    	        public GridBagConstraints gbc = new GridBagConstraints();
    	    }
    	    public statusField_info statusField_info_inst;
    	    
    	    public class blankField_info {
    	        public String name;
    	        public int width = 128;
    	        public int height = 128;
                public GridBagConstraints gbc = new GridBagConstraints();           
            }
            public blankField_info blankField_info_inst;
            
            public class slider_info {
                public String name;
                public int min = 0;
                public int max = 100;
                public GridBagConstraints gbc = new GridBagConstraints();           
            }
            public slider_info slider_info_inst;
            
    	}
    	public panel_element_info[] panel_elements;
//	}
	public pane_info[] subpanels;
    public view_panel get_panel() {
        return get_panel(null);
    }
	
    public view_panel get_panel(GridBagLayout gbl)
    {
        if (gbl == null) {
            gbl = new GridBagLayout();
        }
        
        view_panel vp = new view_panel(name,gbl);
        // For each subpanel, there should be at least one.
        for (int i = 0;
            (panel_elements!= null) && (i < panel_elements.length);
            ++i)
        {
            if (panel_elements[i].button_info_inst!= null)
            {
                pane_info.panel_element_info.button_info bi = 
                    panel_elements[i].button_info_inst;
                vp.buttonAdd(bi.name, bi.text, bi.gbc);
            }
            if (panel_elements[i].checkBox_info_inst != null) {
                pane_info.panel_element_info.checkBox_info ci =
                    panel_elements[i].checkBox_info_inst;
                vp.checkBoxAdd(ci.name, ci.label, ci.gbc);
                vp.checkBoxSetSelected(ci.name, ci.value);
            }

            if (panel_elements[i].slider_info_inst != null) {
                pane_info.panel_element_info.slider_info si =
                    panel_elements[i].slider_info_inst;
                vp.sliderAdd(si.name, si.min, si.max,si.gbc);
            }
            
            if (panel_elements[i].listBox_info_inst != null) {
                ListCellRenderer lcr = null;
                
                pane_info.panel_element_info.listBox_info li = 
                    panel_elements[i].listBox_info_inst;
                
                if (panel_elements[i].listBox_info_inst.list_cell_renderer != null) {
                    String rend = panel_elements[i].listBox_info_inst.list_cell_renderer;
                    rend = rend.replaceAll("\\s+", "");
                    try {
                    Class<?> cl = Class.forName(rend);
                    Constructor<?>[] ctors = cl.getDeclaredConstructors();
                    lcr = (ListCellRenderer) ctors[0].newInstance();
                    }
                    catch (ClassNotFoundException cnfe) {}
                    catch (InstantiationException ie) {}
                    catch (IllegalAccessException iae) {}
                    catch (InvocationTargetException ie) {}
                }
                vp.listBoxAdd(li.theName,li.choices,li.gbc,lcr);
            }
            if (panel_elements[i].table_info_inst != null) {
                pane_info.panel_element_info.table_info ti =
                    panel_elements[i].table_info_inst;
                TableModel mdl = null;
                if (ti.model != null) {
                    String model = ti.model;
                    model = model.replaceAll("\\s+", "");
                    try {
                        Class<?> cl = Class.forName(model);
                        Constructor<?>[] ctors = cl.getDeclaredConstructors();
                        mdl = (TableModel) ctors[0].newInstance();
                        }
                        catch (ClassNotFoundException cnfe) {}
                        catch (InstantiationException ie) {}
                        catch (IllegalAccessException iae) {}
                        catch (InvocationTargetException ie) {}
                }
                vp.tableAdd(ti.name,mdl, ti.columns, null);
            }
            
            if (panel_elements[i].listBox2_info_inst != null) {
                pane_info.panel_element_info.listBox2_info li = 
                    panel_elements[i].listBox2_info_inst;
                vp.listBox2Add(li.theName,li.choices,li.gbc);
            }
            if (panel_elements[i].textField_info_inst != null) {
                pane_info.panel_element_info.textField_info ti =
                    panel_elements[i].textField_info_inst;
                vp.textFieldAdd(ti.name, ti.text, ti.gbc);
                vp.textFieldSet(ti.name, ti.value);
                if (ti.enable) {
                    vp.textFieldEnable(ti.name, ti.enable);
                }
            }
            if (panel_elements[i].statusField_info_inst != null) {
                pane_info.panel_element_info.statusField_info si =
                    panel_elements[i].statusField_info_inst;
                vp.statusFieldSet(si.status,si.gbc);
            }
            if (panel_elements[i].blankField_info_inst != null) {
                pane_info.panel_element_info.blankField_info bi =
                    panel_elements[i].blankField_info_inst;
                vp.blankFieldAdd(bi.name,bi.width,bi.height,bi.gbc);
            }
        }
        for (int i = 0;subpanels != null && i < this.subpanels.length;++i) {
            vp.subPanelAdd(subpanels[i].name,subpanels[i].get_panel(),subpanels[i].gbc);        
        }
        return vp;
    }
	
}
