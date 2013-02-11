package armory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class EditModPrompt extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private ModHandler m;
    private Logger l;
    private JTextField textField;
    private JComboBox comboBox;

    /**
     * Create the dialog.
     */
    public EditModPrompt(ModHandler modHandler, Logger logger) {
	setLocationByPlatform(true);
	setMinimumSize(new Dimension(175, 155));
	this.m = modHandler;
	l = logger;
	setBounds(100, 100, 450, 155);
	SpringLayout springLayout = new SpringLayout();
	springLayout.putConstraint(SpringLayout.NORTH, contentPanel, 0, SpringLayout.NORTH, getContentPane());
	springLayout.putConstraint(SpringLayout.WEST, contentPanel, 0, SpringLayout.WEST, getContentPane());
	springLayout.putConstraint(SpringLayout.SOUTH, contentPanel, 0, SpringLayout.SOUTH, getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, contentPanel, 0, SpringLayout.EAST, getContentPane());
	getContentPane().setLayout(springLayout);
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel);
	SpringLayout sl_contentPanel = new SpringLayout();
	contentPanel.setLayout(sl_contentPanel);

	JButton btnCancel = new JButton("Cancel");
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		dispose();
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.WEST, btnCancel, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, contentPanel);
	contentPanel.add(btnCancel);

	JButton btnSave = new JButton("Save");
	btnSave.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String oldName = comboBox.getSelectedItem().toString();
		String newName = textField.getText();
		if (m.nameIsValid(newName)) {
		    l.log("Renaming \"" + oldName + "\" to \"" + newName + "\"");
		    for (int i = 0; i < m.modList.size(); i++) {
			if (m.modList.get(i).toString().equals(oldName)) {
			    Modification tempMod = m.modList.get(i);
			    // set new values
			    tempMod.setName(newName);
			    // save back to modList and refresh everything with
			    // new data
			    m.modList.set(i, tempMod);
			    m.rewriteMenus();
			    for (int j = 0; j < tempMod.getTypeAmt(); j++) {
				if (m.prefs.get(tempMod.getType(j), "").equals(oldName)) {
				    m.prefs.put(tempMod.getType(j), newName);
				}
			    }
			    m.reloadComboboxes();
			    // rename library folder
			    File oldFolder = new File(m.homeDirectory + m.modLibrary + oldName);
			    File newFolder = new File(m.homeDirectory + m.modLibrary + newName);
			    oldFolder.renameTo(newFolder);
			    break;
			}
		    }
		    dispose();
		}
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.NORTH, btnSave, 0, SpringLayout.NORTH, btnCancel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, btnSave, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(btnSave);

	textField = new JTextField();
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, textField, -10, SpringLayout.NORTH, btnCancel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, textField, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(textField);
	textField.setColumns(10);

	JLabel lblName = new JLabel("Name: ");
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblName, -3, SpringLayout.SOUTH, textField);
	sl_contentPanel.putConstraint(SpringLayout.WEST, textField, 6, SpringLayout.EAST, lblName);
	sl_contentPanel.putConstraint(SpringLayout.WEST, lblName, 0, SpringLayout.WEST, btnCancel);
	contentPanel.add(lblName);

	JLabel lblSelectMod = new JLabel("Select Mod:");
	sl_contentPanel.putConstraint(SpringLayout.NORTH, lblSelectMod, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, lblSelectMod, 0, SpringLayout.WEST, btnCancel);
	contentPanel.add(lblSelectMod);

	comboBox = new JComboBox();
	comboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		textField.setText(comboBox.getSelectedItem().toString());
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.NORTH, comboBox, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, comboBox, 11, SpringLayout.EAST, lblSelectMod);
	sl_contentPanel.putConstraint(SpringLayout.EAST, comboBox, -10, SpringLayout.EAST, contentPanel);

	ArrayList<String> allNames = new ArrayList<String>();
	for (int i = 0; i < m.modList.size(); i++) {
	    if (!m.modList.get(i).isNamed("Default")) { // don't allow default
		// mods to be deleted
		allNames.add(m.modList.get(i).toString());
	    }
	}
	if (allNames.size() < 1) { // if no mods are installed
	    allNames.add("No mods to edit.");
	}
	comboBox.setModel(new DefaultComboBoxModel(allNames.toArray()));

	contentPanel.add(comboBox);
	this.setVisible(true);
    }
}
