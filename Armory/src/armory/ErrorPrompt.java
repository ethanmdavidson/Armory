package armory;

import javax.swing.JDialog;
import javax.swing.SpringLayout;
import javax.swing.JTextPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class ErrorPrompt extends JDialog {

    /**
     * Create the dialog.
     */
    public ErrorPrompt(String msg) {
	setMinimumSize(new Dimension(200, 150));
	setModal(true);
	setTitle("Error!");
	setBounds(100, 100, 300, 200);
	SpringLayout springLayout = new SpringLayout();
	getContentPane().setLayout(springLayout);

	JTextPane textPane = new JTextPane();
	springLayout.putConstraint(SpringLayout.NORTH, textPane, 10, SpringLayout.NORTH, getContentPane());
	springLayout.putConstraint(SpringLayout.WEST, textPane, 10, SpringLayout.WEST, getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, textPane, -10, SpringLayout.EAST, getContentPane());
	textPane.setEditable(false);
	textPane.setText(msg);
	getContentPane().add(textPane);

	JButton btnContinue = new JButton("Continue");
	btnContinue.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	springLayout.putConstraint(SpringLayout.EAST, btnContinue, -10, SpringLayout.EAST, getContentPane());
	springLayout.putConstraint(SpringLayout.SOUTH, textPane, -6, SpringLayout.NORTH, btnContinue);
	springLayout.putConstraint(SpringLayout.SOUTH, btnContinue, -10, SpringLayout.SOUTH, getContentPane());
	getContentPane().add(btnContinue);

	this.setVisible(true);
    }
}
