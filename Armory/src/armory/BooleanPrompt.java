package armory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class BooleanPrompt extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private Boolean result = false;

    /**
     * Create the dialog.
     */
    public BooleanPrompt(String msg) {
	setModal(true);
	setMinimumSize(new Dimension(200, 150));
	setLocationByPlatform(true);
	setBounds(100, 100, 350, 250);
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

	JTextArea textArea = new JTextArea();
	textArea.setWrapStyleWord(true);
	textArea.setEditable(false);
	textArea.setLineWrap(true);
	sl_contentPanel.putConstraint(SpringLayout.NORTH, textArea, 10, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, textArea, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, textArea, -10, SpringLayout.EAST, contentPanel);
	textArea.setText(msg);
	contentPanel.add(textArea);

	JButton btnCancel = new JButton("Cancel");
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		result = false;
		dispose();
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, textArea, -10, SpringLayout.NORTH, btnCancel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, btnCancel, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, contentPanel);
	contentPanel.add(btnCancel);

	JButton btnContinue = new JButton("Continue");
	btnContinue.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		result = true;
		dispose();
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnContinue, 0, SpringLayout.SOUTH, btnCancel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, btnContinue, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(btnContinue);
    }

    public Boolean showPrompt() {
	this.setVisible(true);
	return result;
    }
}
