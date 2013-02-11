package armory;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;

@SuppressWarnings("serial")
public class AboutArmory extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public AboutArmory() {
    	setTitle("About Armory");
    	setLocationByPlatform(true);
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

	JTextArea txtrArmoryIsA = new JTextArea();
	txtrArmoryIsA.setFont(new Font("Arial", Font.PLAIN, 13));
	txtrArmoryIsA.setEditable(false);
	txtrArmoryIsA.setWrapStyleWord(true);
	txtrArmoryIsA.setLineWrap(true);
	txtrArmoryIsA.setText("Armory is a Java application intended to facilitate the use of mods in AssaultCube. It was concieved, designed, and written by Ethan \"Lantry\" Davidson, but he couldn't have done it without these people:\r\n\r\n- Cleaner and all the awesome folks at akimbo.in, for enforcing the packaging rules.\r\n\r\n- a_slow_old_man, Dementium4ever, and anyone else who has ever made a mod.\r\n\r\n- Ronald_Reagan, for helping me get Armory to work on Mac OS.\r\n\r\n- You, for using Armory and reading the about page :D");
	txtrArmoryIsA.setToolTipText("Randall Munroe, for teaching me the awesomeness of alt text.");
	JScrollPane scrollPane = new JScrollPane(txtrArmoryIsA);
	sl_contentPanel.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(scrollPane);

	JLabel label = new JLabel("1.2.0.0");
	label.setToolTipText("This version of Armory was designed for AssaultCube v1.2.0.0");
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, label, -10, SpringLayout.SOUTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, label, 0, SpringLayout.EAST, txtrArmoryIsA);
	contentPanel.add(label);

	JButton btnClose = new JButton("Close");
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnClose);
	btnClose.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnClose, 0, SpringLayout.SOUTH, label);
	contentPanel.add(btnClose);

	this.setVisible(true);
    }
}
