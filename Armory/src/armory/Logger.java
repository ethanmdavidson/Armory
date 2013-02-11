package armory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.SpringLayout;

import org.apache.commons.io.FileUtils;
import javax.swing.JButton;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class Logger extends JDialog {

    private final JPanel contentPanel = new JPanel();
    long startTime;
    File debugLogFile;
    ArrayList<String> messages;
    private JTextArea textArea;

    /**
     * Create the dialog.
     */
    public Logger() {
	setTitle("Log");
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

	JButton btnClose = new JButton("Close");
	btnClose.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setVisible(false);
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.WEST, btnClose, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnClose, -10, SpringLayout.SOUTH, contentPanel);
	contentPanel.add(btnClose);

	JButton btnSave = new JButton("Save");
	btnSave.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    if (debugLogFile.exists()) {
			debugLogFile.delete();
		    }
		    for (int i = 0; i < messages.size(); i++) {
			FileUtils.writeStringToFile(debugLogFile, messages.get(i), true);
		    }
		    log("debug log successfully saved: " + debugLogFile);
		    new ErrorPrompt("The debug log has been saved to " + debugLogFile + ". This is not an error.");
		} catch (IOException ex) { // errors on error reporting are fail
		    ex.printStackTrace();
		    log(ex);
		}
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnSave, 0, SpringLayout.SOUTH, btnClose);
	sl_contentPanel.putConstraint(SpringLayout.EAST, btnSave, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(btnSave);

	textArea = new JTextArea();
	textArea.setEditable(false);
	
	JScrollPane scrollPane = new JScrollPane(textArea);
	sl_contentPanel.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.NORTH, btnClose);
	sl_contentPanel.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(scrollPane);

	startTime = System.currentTimeMillis();
	messages = new ArrayList<String>();
	debugLogFile = new File(FileSystemView.getFileSystemView().getHomeDirectory().toString() + "/ArmoryDebugLog.txt");
    }

    /*
     * public void enable() { //outdated
     * armory.ArmoryMain.prefs.putBoolean("LOGGING", true);
     * logging = true;
     * log("Logging Enabled");
     * // write all messages that were passed this run before logging was enabled
     * try {
     * for (int i = 0; i < messages.size(); i++) {
     * FileUtils.writeStringToFile(debugLogFile, messages.get(i) + "\n", false);
     * }
     * } catch (IOException ex) {
     * ex.printStackTrace();
     * }
     * }
     */

    /*
     * public void disable() { //outdated
     * log("Logging Disabled");
     * logging = false;
     * armory.ArmoryMain.prefs.putBoolean("LOGGING", false);
     * }
     */

    public void log(Exception ex) { // converts an exception into a string for logging
	ex.printStackTrace();
	log("Exception: " + ex.getMessage());
    }

    public void log(String msg) { // adds the given string to the log's textArea
	// prepend time
	msg = "[" + logTime() + "] " + msg;
	// append eol
	String eol = System.getProperty("line.separator");
	if (eol.isEmpty()) {
	    eol = "\n";
	}
	msg = msg + eol;
	// add to log
	messages.add(msg);
	textArea.setText(textArea.getText() + msg);
    }

    /*
     * private static String absLogTime() { // returns the absolute time (date)
     * DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS");
     * Date date = new Date();
     * return dateFormat.format(date);
     * }
     */

    private String logTime() { // returns the time since start, stolen from Slime Volleyball :D
	long longZero = System.currentTimeMillis() - startTime;
	long longOne = (longZero / 10L) % 100L;
	long longTwo = (longZero / 1000L) % 60L;
	long longThree = (longZero / 60000L) % 60L;
	long longFour = longZero / 3600000L;
	String s = "";
	if (longFour < 10L) {
	    s += "0";
	}
	s += longFour;
	s += ":";
	if (longThree < 10L) {
	    s += "0";
	}
	s += longThree;
	s += ":";
	if (longTwo < 10L) {
	    s += "0";
	}
	s += longTwo;
	s += ":";
	if (longOne < 10L) {
	    s += "0";
	}
	s += longOne;
	return s;
    }
}
