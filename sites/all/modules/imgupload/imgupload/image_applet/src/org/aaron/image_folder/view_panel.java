package org.aaron.image_folder;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.util.logging.*;
import java.util.*;
import javax.swing.text.*;

/**
 * This is a container for a bunch of GUI artifacts. The artifacts (fields,
 * buttons etc.) are part of a JPanel which is layed out according to grid bag
 * constraints. view_panels can be nested to create more complex GUIs.
 */
public class view_panel extends JPanel implements ActionListener,ItemListener,ChangeListener {

    // avoid warning...
    private static final long serialVersionUID = 1L;

    /***************************************************************************
     * A list of all components. The contents of these can be queried by the
     * client
     **************************************************************************/
    HashMap<TableModel, JComponent> myTableViews = new HashMap<TableModel, JComponent>();
    static HashMap<String, JComponent> myTables = new HashMap<String, JComponent>();
    static HashMap<JComponent, ActionHandler> myActionHandlers = new HashMap<JComponent, ActionHandler>();
    JTextPane myStatus;

    HashMap<String, JComponent> myComponents = new HashMap<String, JComponent>();
    HashMap<JComponent, String> myNames = new HashMap<JComponent, String>();

    private static final Color TF_COLOR = new Color(204, 238, 238);
    private static final Color LABEL_COLOR = new Color(238, 238, 204);
    private static final Color STATUS_COLOR = new Color(128, 64, 64);
    private static final Color BUTTON_BACK_COLOR = new Color(96, 96, 240);
    private static final Color BUTTON_FORE_COLOR = new Color(240, 240, 240);
    private static final Color PANE_BACKGROUND_COLOR = new Color(240, 240, 240);

    private static final int DEFAULT_TEXT_WIDTH = 15;
    
    private void addHash(String str, JComponent comp) {
        myComponents.put(str, comp);
        myNames.put(comp, str);
    }

    private void removeHash(String str, JComponent comp) {
        myComponents.remove(str);
        myNames.remove(comp);
    }

    private void removeHash(String name) {
        removeHash(name, getComponent(name));
    }

    private String getName(JComponent comp) {
        return myNames.get(comp);
    }

    private JComponent getComponent(String name) {
        return myComponents.get(name);
    }
    public void changeComponentName(String old_name,String new_name) {
        JComponent comp = getComponent(old_name);
        removeHash(old_name);
        addHash(new_name,comp);        
    }

    public <T> T getTypedComponent(String name) {
        T thing = null;
        try {
            JComponent comp = myComponents.get(name);
            if (comp != null) {
                thing = (T)comp;
            } 
        }
        catch (ClassCastException cce) {
            System.out.println("Can't cast to T " + cce.toString());
        }
        return thing;
    }
    public void validate_root() {
        validateTree();
        Component c = SwingUtilities.getRoot((Component) this);
        try {
            JFrame frame = (JFrame) c;
            if (frame != null) {
                Container panel = frame.getContentPane();
                if (panel != null) {
                    panel.validate();
                }
            }
        } catch (ClassCastException cce) {
        }
    }

    public interface ActionHandler {
        void handleAction(String comp_name, JComponent comp, AWTEvent e,view_panel vp);
    }

    public void update_layout() {
        validate_root();
        Component c = SwingUtilities.getRoot((Component) this);
        JFrame frame = (JFrame) c;
        if (frame != null) {
            frame.pack();
        }
    }

    /***************************************************************************
     * Helper fcn to stack the label on top of the text field
     **************************************************************************/
    private void textFieldSetInPane(JTextField field, JLabel label,
            GridBagConstraints c) {
        JPanel pan = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBackground(LABEL_COLOR);
        label.setOpaque(true);
        field.setBackground(TF_COLOR);
        field.setHorizontalAlignment(JLabel.CENTER);
        pan.add(label, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        pan.add(field, gbc);
        if (c == null) {
            add(pan);
        } else {
            add(pan, c);
        }
        field.setEnabled(true);
        field.setEditable(true);
    }

    public void tableRemove(TableModel model) {
        JComponent goner = myTableViews.get(model);
        if (goner != null) {
            remove(goner);
            myTables.remove(model);
            myComponents.remove(model);
            myTableViews.remove(model);
        }
    }

    public void tableSetValueAt(String name, Object arg0, int arg1, int arg2) {
        JTable table = (JTable) myTables.get(name);
        if (table != null) {
            TableModel model = table.getModel();
            model.setValueAt(arg0, arg1, arg2);
        }
    }

    public void tableAdd(String name, TableModel model, String[] columnNames,
            ListSelectionListener listener) {
        tableAdd(name, model, columnNames, listener, null);
    }

    public void tableAdd(String name, TableModel model, String[] columnNames,
            ListSelectionListener listener, GridBagConstraints con) {
        JTable table = new JTable(model);

        int i;
        for (i = 0; i < columnNames.length; ++i) {
            table.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);
        }
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        if (listener != null) {
            table.getSelectionModel().addListSelectionListener(listener);
        }

        table.setPreferredScrollableViewportSize(new Dimension(320, 120));
        table.setFillsViewportHeight(true);
        JScrollPane pane = new JScrollPane();
        pane.setViewportView(table);
        pane.revalidate();
        if (con != null) {
            add(pane, con);
        } else {
            add(pane);
        }
        myTableViews.put(model, pane);
        myTables.put(name, table);
        removeHash(name);
    }

    public GridBagConstraints getConstraint(String theLabel) {
        Component comp = myComponents.get(theLabel).getParent();
        LayoutManager layout = this.getLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        try {
            GridBagLayout gbl = (GridBagLayout) layout;
            gbc = gbl.getConstraints(comp);
        } catch (ClassCastException cce) {
        }
        return gbc;
    }

    public int tableGetSelection(TableModel model) {
        JTable table = (JTable) myTables.get(model);
        if (table != null) {
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                return table.convertRowIndexToView(table.getSelectedRow());
            }
        }
        return -1;
    }

    public void listBox2Add(String theName, String[] choices,
            GridBagConstraints gbc) {
        listBox2Add(theName, choices, null, gbc);
    }

    public void listBox2Add(String theName, String[] choices,
            ActionListener listener, GridBagConstraints gbc) {
        JPanel pan = new JPanel(new BorderLayout());
        JComboBox list = null;
        if (choices != null)
            list = new JComboBox(choices);
        else
            list = new JComboBox(new DefaultComboBoxModel());

        JScrollPane listScroller = new JScrollPane(list);
        // listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentY(JScrollPane.RIGHT_ALIGNMENT);

        list.setRenderer(new image_list());
        addHash(theName, list);
        JLabel label = new JLabel(theName);
        label.setBackground(LABEL_COLOR);
        label.setOpaque(true);
        list.setBackground(TF_COLOR);
        pan.add(label, BorderLayout.NORTH);
        // pan.add(list,BorderLayout.SOUTH);
        pan.add(listScroller, BorderLayout.CENTER);
        // if (listener != null) {
        // list.addActionListener(listener);
        // }
        if (gbc != null) {
            add(pan, gbc);
        } else {
            add(pan);
        }
    }

    public void listBox2Show(String name, boolean toshow) {
        JComboBox box = getTypedComponent(name);
        if (box != null) {
            JPanel pan = (JPanel) box.getParent();
            pan.setVisible(toshow);
        }
    }

    public boolean listBox2IsVisible(String name) {
        JComboBox box = getTypedComponent(name);
        if (box != null) {
            return box.isShowing();
        }
        return false;
    }

    public String listBox2GetSelection(String theName) {
        JComboBox box = getTypedComponent(theName);
        if (box != null) {
            Object obj = box.getSelectedItem();

            if (obj != null) {
                return obj.toString();
            }
        }
        return "null";
    }

    public void listBox2Add(String theName, String[] choices) {
        listBox2Add(theName, choices, null, null);
    }

    public void listBox2SetEditable(String theName, boolean editable) {
        JComboBox box = getTypedComponent(theName);
        if (box != null) {
            box.setEditable(editable);
        }
    }

    /***************************************************************************
     * Adds a choice (combo) box to the GUI with 'choices'
     **************************************************************************/
    public void listBox2Remove(String theName) {
        JComboBox box = getTypedComponent(theName);
        if (box != null) {
            JPanel parent = (JPanel) box.getParent();
            if (parent != null) {
                remove(parent);
            }
            removeHash(theName);
        }
    }

    public void listBox2AddChoice(String theName, Object str) {
        JComboBox box = getTypedComponent(theName);
        if (box != null) {
            box.addItem(str);
        }
    }

    public boolean listBoxIsVisible(String name) {
        JList box = getTypedComponent(name);
        if (box != null) {
            return box.isShowing();
        }
        return false;
    }

    public String listBoxGetSelection(String theName) {
        JList box = getTypedComponent(theName);
        if (box != null) {
            if (box.getSelectedValue() != null) {
                return box.getSelectedValue().toString();
            }
        }
        return "null";
    }

    /***************************************************************************
     * Adds a choice (combo) box to the GUI with 'choices'
     **************************************************************************/
    public void listBoxAdd(String theName, String[] choices) {
        listBoxAdd(theName, choices, null, null);
    }

    public void listBoxAdd(String theName, String[] choices,
            GridBagConstraints gbc, ListCellRenderer rend) {
        JPanel pan = new JPanel(new BorderLayout());
        JList list = null;
        if (choices != null)
            list = new JList(choices);
        else
            list = new JList(new DefaultListModel());

        list.setLayoutOrientation(JList.VERTICAL);
        JScrollPane listScroller = new JScrollPane(list);
        // listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentY(JScrollPane.RIGHT_ALIGNMENT);

        if (rend != null)
            list.setCellRenderer(rend);
        addHash(theName, list);
        JLabel label = new JLabel(theName);
        label.setBackground(LABEL_COLOR);
        label.setOpaque(true);
        list.setBackground(TF_COLOR);
        pan.add(label, BorderLayout.NORTH);
        // pan.add(list,BorderLayout.SOUTH);
        pan.add(listScroller, BorderLayout.CENTER);
        // if (listener != null) {
        // list.addActionListener(listener);
        // }
        if (gbc != null) {
            add(pan, gbc);
        } else {
            add(pan);
        }
    }

    public void listBoxSetListener(String theName,
            ListSelectionListener listener) {
        JList box = getTypedComponent(theName);
        if (box != null) {
            box.addListSelectionListener(listener);
        }
    }

    public void listBoxSetEditable(String theName, boolean editable) {
        JList box = getTypedComponent(theName);
        if (box != null) {
            // box.setEditable(editable);
        }
    }

    /***************************************************************************
     * Adds a choice (combo) box to the GUI with 'choices'
     **************************************************************************/
    public void listBoxRemove(String theName) {
        JList box = getTypedComponent(theName);
        if (box != null) {
            JPanel parent = (JPanel) box.getParent();
            if (parent != null) {
                remove(parent);
            }
            removeHash(theName);
        }
    }

    public void listBoxAddChoice(String theName, Object str) {
        JList box = getTypedComponent(theName);
        if (box != null) {
            DefaultListModel model = (DefaultListModel) box.getModel();
            model.addElement(str);
            // box.setSelectedItem(str);
        }
    }

    public void statusFieldSet(String theStatus, GridBagConstraints c) {
        if (my_parent != null) {
            my_parent.statusFieldSet(theStatus, c);
        }
        if (myStatus == null) {
            JLabel label = new JLabel("Status:");
            label.setBackground(LABEL_COLOR);
            label.setOpaque(true);

            myStatus = new JTextPane(new DefaultStyledDocument());
            myStatus.setEditable(false);

            Font font = new Font("Times New Roman", Font.BOLD, 12);
            myStatus.setFont(font);
            StyledDocument doc = myStatus.getStyledDocument();
            MutableAttributeSet attrs = myStatus.getInputAttributes();
            StyleConstants.setForeground(attrs, STATUS_COLOR);
            StyleConstants.setFontFamily(attrs, "Times New Roman");
            StyleConstants.setFontSize(attrs, 12);
            StyleConstants.setBold(attrs, true);
            doc.setParagraphAttributes(0, 0, attrs, true);

            myStatus.setBackground(TF_COLOR);

            JPanel pan = new JPanel(new BorderLayout());
            pan.add(label, BorderLayout.NORTH);
            pan.add(myStatus, BorderLayout.SOUTH);

            if (c == null) {
                add(pan);
            } else {
                add(pan, c);
            }
        }

        myStatus.setText(theStatus);
        validate_root();

    }

    public void statusFieldSet(String theStatus) {
        statusFieldSet(theStatus, null);
    }

    public void statusFieldHide() {
        if (myStatus != null) {
            JPanel pan = (JPanel) myStatus.getParent();
            remove(pan);
            myStatus = null;
        }
    }

    public void checkBoxAdd(String name, String label,
            GridBagConstraints gbc) {
        JCheckBox box = new JCheckBox(label);
        box.setBackground(PANE_BACKGROUND_COLOR);
        box.addItemListener(this);
        addHash(name, box);
        if (gbc != null) {
            add(box, gbc);
        } else
            add(box);
    }

    public void checkBoxSetSelected(String name, boolean value) {
        JCheckBox box = getTypedComponent(name);
        if (box != null) {
            box.setSelected(value);
        }
    }

    public boolean checkBoxSelected(String name) {
        JCheckBox box = getTypedComponent(name);
        if (box != null) {
            String value = (String) box.getAction().getValue(
                    Action.SELECTED_KEY);
            return (new Boolean(value)).booleanValue();
        }
        return false;
    }

    public void checkBoxRemove(String name) {
        JCheckBox box = getTypedComponent(name);
        if (box != null) {
            removeHash(name, getComponent(name));
            removeHash(name);
            remove(box);
        }
    }

    public void checkBoxShow(String name, boolean the) {
        JCheckBox box = getTypedComponent(name);
        if (box != null) {
            box.setVisible(the);
        }
    }

    public void blankFieldAdd(String name, int width, int height,
            GridBagConstraints c) {

        JPanel pane = new JPanel();
        pane.setBackground(PANE_BACKGROUND_COLOR);
        JScrollPane listScroller = new JScrollPane(pane);
        // listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentY(JScrollPane.RIGHT_ALIGNMENT);

        pane.setSize(width, height);
        add(listScroller, c);
        addHash(name,pane);
        pane.setLayout(new GridBagLayout());
    }

    public void blankFieldRemove(String name) {
        JPanel pane = getTypedComponent(name);
        if (pane != null) {
            remove(pane);
        }
        removeHash(name);
    }
    
    public void blankFieldSetSize(String name,Dimension dim) {
        JPanel pane = getTypedComponent(name);
        if (pane != null) {
            pane.setPreferredSize(dim);
        }
    }
    
    public void blankFieldAddComponent(String field_name, JComponent comp,
            GridBagConstraints c) {
        JPanel pane = getTypedComponent(field_name);
        if (pane != null) {
            pane.add(comp, c);
        }
    }

    public void blankFieldRemoveComponent(String field_name, JComponent comp) {
        JPanel pane = getTypedComponent(field_name);
        if (comp == null) {
            System.out.println("remove null component");
            return;
        }
        if (pane != null) {
            pane.remove(comp);
        }
    }

    public void textFieldAdd(String theFieldName, String theLabelName,
            GridBagConstraints c) {
        JTextField field = new JTextField();
        // field.setColumns(DEFAULT_TEXT_WIDTH);

        // myTextFields.put(theFieldName, field);
        addHash(theFieldName, field);
        this.textFieldSetInPane(field, new JLabel(theLabelName), c);
    }

    /***************************************************************************
     * Create a blank text field with the given label
     **************************************************************************/
    public void textFieldAdd(String theFieldName, String theLabelName) {
        textFieldAdd(theFieldName, theLabelName, null);
    }

    public void textFieldSet(String theFieldName, String theContents) {
        // tmpField = myTextFields.get(theFieldName);
        JTextField tmpField = getTypedComponent(theFieldName);
        if (tmpField != null) {
            tmpField.setText(theContents);
        }
    }

    /***************************************************************************
     * Create a blank text field with the given label
     **************************************************************************/
    public void passwordFieldAdd(String theFieldName, String theLabelName) {
        passwordFieldAdd(theFieldName, theLabelName, null);
    }

    public void passwordFieldAdd(String theFieldName, String theLabelName,
            GridBagConstraints gbc) {
        JPasswordField field = new JPasswordField();
        field.setColumns(DEFAULT_TEXT_WIDTH);
        myComponents.put(theFieldName, field);

        textFieldSetInPane(field, new JLabel(theLabelName), gbc);
    }

    public String textFieldGetText(String theFieldName) {
        JTextField tmpField =  getTypedComponent(theFieldName);
        if (tmpField != null) {
            return tmpField.getText();
        }
        return "";
    }

    public void textFieldRemove(String theName) {
        JTextField tmpField = getTypedComponent(theName);
        if (tmpField != null) {
            JPanel parent = (JPanel) tmpField.getParent();
            if (parent != null) {
                remove(parent);
                removeHash(theName);
            }
        }
    }

    public void textFieldShow(String theLabel, boolean theShow) {
        JTextField tmpField = getTypedComponent(theLabel);
        if (tmpField != null) {
            JPanel pan = (JPanel) tmpField.getParent();
            pan.setVisible(theShow);
        }
    }

    public void textFieldEnable(String theLabel, boolean theState) {
        JTextField tmpField = getTypedComponent(theLabel);
        if (tmpField != null) {
            tmpField.setEditable(theState);
        }
    }

    public void buttonClick(String Name) {
        JButton but = getTypedComponent(Name);
        if (but != null) {
             but.doClick();
        }
    }
    public void buttonShow(String Name, boolean toShow) {
        JButton but = getTypedComponent(Name);
        if (but != null) {
            but.setVisible(toShow);
        }
    }

    public void buttonRemove(String theName) {
        JButton but = getTypedComponent(theName);
        if (but != null) {
            remove(but);
            removeHash(theName);
        }
    }

    public void buttonChangeText(String theName, String theNewLabel) {
        JButton but = getTypedComponent(theName);
        if (but != null) {
            but.setText(theNewLabel);
        }
    }

    public void buttonSetImage(String theName, Icon image) {
        JButton but = getTypedComponent(theName);
        if (but != null) {
            but.setIcon(image);
        }
    }

    public void buttonSetDefault(String theName) {
        JButton but = getTypedComponent(theName);
        if (but != null) {
            getRootPane().setDefaultButton(but);
        }
    }

    public void buttonEnable(String theName, boolean theState) {
        JButton but = getTypedComponent(theName);
        if (but != null) {
            but.setEnabled(theState);
        }
    }

    public void buttonAdd(String theName, String theText,
            GridBagConstraints c) {
        JButton but = new JButton(theText);
        but.setBackground(BUTTON_BACK_COLOR);
        but.setForeground(BUTTON_FORE_COLOR);
        but.addActionListener(this);
        but.setFont(new Font("Times New Roman", Font.BOLD, 12));
        addHash(theName, but);
        if (c != null) {
           add(but, c);
        } else {
            add(but);
        }
    }

    public void buttonAdd(String theName, String theText) {
        buttonAdd(theName, theText, null);
    }

    public void sliderAdd(String theName,int min,int max,GridBagConstraints gbc) {
        JSlider slider = new JSlider(min,max);
        slider.addChangeListener(this);
        addHash(theName,slider);
        add(slider,gbc);
    }
    public void sliderRemove(String theName) {
        JSlider slider = getTypedComponent(theName);
        if (slider != null) {
            remove(slider);
            removeHash(theName);
        }
    }
    public void sliderSetValue(String theName,int value) {
        JSlider slider = getTypedComponent(theName);
        if (slider != null) {
            slider.setValue(value);
        }        
    }
    
    public void sliderEnable(String theName,boolean toSet) {
        JSlider slider = getTypedComponent(theName);
        if (slider != null) {
            slider.setEnabled(toSet);
        }        
    }
    view_panel my_parent = null;

    private void setParent(view_panel vp) {
        my_parent = vp;
    }

    public void subPanelAdd(String name, view_panel panel,
            GridBagConstraints gbc) {
        add(panel, gbc);
        panel.setParent(this);
        if (panel.myStatus != null) {
            myStatus = panel.myStatus;
        }
        addHash(name,panel);
    }

    public view_panel subPanelGet(String name) {
        view_panel rv = getTypedComponent(name);
        return rv;
    }

    public static void warningShow(String theTitle, String theMessage) {
        JOptionPane msg = new JOptionPane();
        JOptionPane.showMessageDialog(msg, theMessage, theTitle,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public view_panel(String name) {
        setOpaque(true);
        setBackground(PANE_BACKGROUND_COLOR);
    }

    public view_panel(String name, LayoutManager lo) {
        super(lo);
        setOpaque(true);
        setBackground(PANE_BACKGROUND_COLOR);
    }

    public boolean addActionHandler(String comp,ActionHandler ah) {
        JComponent jcomp = getComponent(comp);
        if (jcomp != null) {
            myActionHandlers.put(getComponent(comp), ah);
            return true;
        }

        Collection<JComponent> subs = myComponents.values();
        Iterator<JComponent> it = subs.iterator();        
        while (it.hasNext()) {
            jcomp = it.next();
            if (jcomp instanceof view_panel) {
                view_panel vp = (view_panel)jcomp;
                if (vp.addActionHandler(comp, ah)) {
                    return true;
                }
            }
        }
        
        return false; //this.my_parent.addActionHandler(comp, ah);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        try {
            JComponent comp = (JComponent) obj;
            ActionHandler ah = myActionHandlers.get(comp);

            if (ah != null) {
                ah.handleAction(getName(comp), comp, e,this);
            }

        } catch (ClassCastException cce) {
            System.out.println("event from non-swing object");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        // TODO Auto-generated method stub
        Object obj = e.getSource();
        try {
            JComponent comp = (JComponent) obj;
            ActionHandler ah = myActionHandlers.get(comp);

            if (ah != null) {
                ah.handleAction(getName(comp), comp, e,this);
            }
        } catch (ClassCastException cce) {
            System.out.println("event from non-swing object");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO Auto-generated method stub
        JSlider source = (JSlider)e.getSource();
        ActionEvent ae = new ActionEvent(source,0,"LabelChanged");
        if (!source.getValueIsAdjusting()) {
            String name = getName(source);
            ActionHandler ah = myActionHandlers.get(source);
            if (ah != null) {
                ah.handleAction(name, source, ae, this);
            }
        }        
    }        
}
