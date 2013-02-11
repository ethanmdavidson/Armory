package armory;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.SpringLayout;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.Dimension;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class RemoveModPrompt extends JDialog {

    private ModHandler m;
    private JComboBox comboBox;

    /**
     * Create the dialog.
     */
    public RemoveModPrompt(ModHandler modHandler) {
	setLocationByPlatform(true);
	setModal(true);
	setTitle("Remove a Mod");
	m = modHandler;
	setMinimumSize(new Dimension(200, 120));
	setBounds(100, 100, 350, 120);
	SpringLayout springLayout = new SpringLayout();
	getContentPane().setLayout(springLayout);

	comboBox = new JComboBox();
	springLayout.putConstraint(SpringLayout.NORTH, comboBox, 10, SpringLayout.NORTH, getContentPane());
	springLayout.putConstraint(SpringLayout.WEST, comboBox, 10, SpringLayout.WEST, getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, comboBox, -10, SpringLayout.EAST, getContentPane());
	getContentPane().add(comboBox);

	ArrayList<String> allNames = new ArrayList<String>();
	for (int i = 0; i < m.modList.size(); i++) {
	    if (!m.modList.get(i).isNamed("Default")) { // don't allow default
		// mods to be deleted
		allNames.add(m.modList.get(i).toString());
	    }
	}
	if (allNames.size() < 1) { // if no mods are installed
	    allNames.add("No mods to delete.");
	}
	comboBox.setModel(new DefaultComboBoxModel(allNames.toArray()));

	JButton btnCancel = new JButton("Cancel");
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	springLayout.putConstraint(SpringLayout.WEST, btnCancel, 10, SpringLayout.WEST, getContentPane());
	springLayout.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, getContentPane());
	getContentPane().add(btnCancel);

	JButton btnRemove = new JButton("Remove");
	btnRemove.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.removeMod(comboBox.getSelectedItem().toString());
		dispose();
	    }
	});
	springLayout.putConstraint(SpringLayout.SOUTH, btnRemove, -10, SpringLayout.SOUTH, getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, btnRemove, -10, SpringLayout.EAST, getContentPane());
	getContentPane().add(btnRemove);

	this.setVisible(true);
    }
}
