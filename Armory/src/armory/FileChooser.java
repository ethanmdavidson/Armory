package armory;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.BoxLayout;

@SuppressWarnings("serial")
public class FileChooser extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JFileChooser fc;

    /**
     * Create the dialog.
     */
    public FileChooser() {
	setLocationByPlatform(true);
	setTitle("Choose a file");
	setModal(true);
	setBounds(100, 100, 450, 300);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));
	{
	    JFileChooser fileChooser = new JFileChooser();
	    fc = fileChooser;
	    contentPanel.add(fileChooser);
	}
    }

    public String showFileChooser() {
	contentPanel.setVisible(true);
	int returnVal = fc.showOpenDialog(this);
	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    File chosen = fc.getSelectedFile();
	    return chosen.toString();
	}
	return "";
    }

    public void setFilterFolder() {
	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public void setFilterModification() {
	fc.setFileFilter(new FileFilter() { // set the zip file filter

	    @Override
	    public boolean accept(File file) {
		// choose what file types to allow (add a similar ||
		// statement for each additional type)
		return file.getAbsolutePath().endsWith(".zip") || file.isDirectory();
	    }

	    @Override
	    public String getDescription() {
		return "ZIP archive (*.zip)";
	    }
	});
    }

}
