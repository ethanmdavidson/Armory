package armory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.JLabel;

import org.apache.commons.exec.OS;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

@SuppressWarnings("serial")
public class HomeDirPrompt extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField textField;
    private JButton button;
    private ModHandler m;
    private Logger l;

    /**
     * Create the dialog.
     */
    public HomeDirPrompt(ModHandler modHandler, Logger logger) {
	setLocationByPlatform(true);
	m = modHandler;
	l = logger;
	setModal(true);
	setBounds(100, 100, 297, 178);
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
	{
	    textField = new JTextField();
	    sl_contentPanel.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, contentPanel);
	    contentPanel.add(textField);
	    textField.setColumns(10);
	}
	{
	    button = new JButton("...");
	    button.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
		    FileChooser fc = new FileChooser();
		    fc.setFilterFolder();
		    String filePath = fc.showFileChooser();
		    textField.setText(filePath);
		}
	    });
	    sl_contentPanel.putConstraint(SpringLayout.EAST, textField, -6, SpringLayout.WEST, button);
	    sl_contentPanel.putConstraint(SpringLayout.NORTH, button, -1, SpringLayout.NORTH, textField);
	    contentPanel.add(button);
	}
	{

	    JButton btnExitArmory = new JButton("Exit Armory");
	    btnExitArmory.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });
	    sl_contentPanel.putConstraint(SpringLayout.WEST, btnExitArmory, 10, SpringLayout.WEST, contentPanel);
	    sl_contentPanel.putConstraint(SpringLayout.SOUTH, textField, -6, SpringLayout.NORTH, btnExitArmory);
	    sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnExitArmory, -10, SpringLayout.SOUTH, contentPanel);
	    contentPanel.add(btnExitArmory);

	    JButton btnContinue = new JButton("Continue");
	    btnContinue.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
		    String newDir = textField.getText();
		    if (OS.isFamilyMac()) { // manipulate to the gamedata folder
			if (newDir.contains("/AssaultCube.app")) {
			    newDir = newDir.substring(0, newDir.indexOf("/AssaultCube.app")) + "/AssaultCube.app/Contents/gamedata";
			}
		    }
		    File newDirFile = new File(newDir);
		    if (!newDir.isEmpty() && newDirFile.exists() && newDirFile.isDirectory()) {
			// save the home directory
			m.prefs.put("HOMEDIR", newDir);
			m.homeDirectory = newDir;
			l.log("Home dir changed to: " + newDir);

			dispose();
		    }
		}
	    });
	    sl_contentPanel.putConstraint(SpringLayout.EAST, btnContinue, -10, SpringLayout.EAST, contentPanel);
	    sl_contentPanel.putConstraint(SpringLayout.EAST, button, 0, SpringLayout.EAST, btnContinue);
	    sl_contentPanel.putConstraint(SpringLayout.NORTH, btnContinue, 0, SpringLayout.NORTH, btnExitArmory);
	    contentPanel.add(btnContinue);
	}

	JLabel lblArmoryWasUnable = new JLabel("Armory was unable to locate your AC home directory.");
	sl_contentPanel.putConstraint(SpringLayout.NORTH, lblArmoryWasUnable, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, lblArmoryWasUnable, 10, SpringLayout.WEST, contentPanel);
	contentPanel.add(lblArmoryWasUnable);

	JLabel lblPleaseLocateIt = new JLabel("Please locate it now.");
	sl_contentPanel.putConstraint(SpringLayout.NORTH, lblPleaseLocateIt, 6, SpringLayout.SOUTH, lblArmoryWasUnable);
	sl_contentPanel.putConstraint(SpringLayout.WEST, lblPleaseLocateIt, 10, SpringLayout.WEST, contentPanel);
	contentPanel.add(lblPleaseLocateIt);
    }
}
