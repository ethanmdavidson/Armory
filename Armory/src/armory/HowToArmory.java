package armory;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class HowToArmory extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public HowToArmory() {
    	setTitle("How to Use Armory");
	setBounds(100, 100, 450, 300);
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
	
	JButton btnClose = new JButton("Close");
	btnClose.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    dispose();
		}
	});
	sl_contentPanel.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnClose, -10, SpringLayout.SOUTH, contentPanel);
	contentPanel.add(btnClose);
	
	JTextArea textArea = new JTextArea();
	textArea.setWrapStyleWord(true);
	textArea.setLineWrap(true);
	textArea.setText("A quick guide to using Armory:\n1) Set your AC home directory.\n        This is the folder in which Armory will switch mods. It will most likely be named \"AssaultCube\" or something similar, and will usually contain folders such as \"config\", \"screenshots\", and/or \"packages\".\n\n2) Add mod(s) to Armory\n        Select the \"Add a mod\" option from the file menu. In the window that pops up, press \"Select File\" and choose the .zip file of the mod you wish to add. Then simply fill in all the necessary information (e.g. name) and press add mod.\n\n3) Select the mods you wish to use.\n        Use the drop-down menus to change which mod is being used in AC. \n\nTo change mods in-game, type \"/showarmorymenu\" in the AC console");
	
	JScrollPane scrollPane = new JScrollPane(textArea);
	sl_contentPanel.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, btnClose);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnClose);
	sl_contentPanel.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(scrollPane);
    }

}
