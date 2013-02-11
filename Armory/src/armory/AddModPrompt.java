package armory;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@SuppressWarnings("serial")
public class AddModPrompt extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField pathTextField;
    private JTextField nameTextField;
    private ModHandler m;
    private Logger l;

    /**
     * Create the dialog.
     * 
     * @param modHandler
     */
    public AddModPrompt(ModHandler modHandler, Logger logger) {
	setLocationByPlatform(true);
	setModal(true);
	setMinimumSize(new Dimension(200, 170));
	m = modHandler;
	l = logger;
	setTitle("Add a Mod!");
	setBounds(100, 100, 450, 170);
	getContentPane().setLayout(new BorderLayout());
	contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
	getContentPane().add(contentPanel, BorderLayout.CENTER);
	SpringLayout sl_contentPanel = new SpringLayout();
	contentPanel.setLayout(sl_contentPanel);

	JButton btnCancel = new JButton("Cancel");
	btnCancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dispose();
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.WEST, btnCancel, 10, SpringLayout.WEST, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnCancel, -10, SpringLayout.SOUTH, contentPanel);
	contentPanel.add(btnCancel);

	JButton btnAddMod = new JButton("Add Mod");
	btnAddMod.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		File newFile = new File(pathTextField.getText());
		String newName = nameTextField.getText();
		l.log("Adding \"" + newName + "\" from " + newFile.toString());

		if (m.nameIsValid(newName) && m.fileIsValid(newFile)) {
		    File newMod = new File(pathTextField.getText());
		    ArrayList<File> files = null; // filepaths of files in zip
		    ArrayList<String> types = new ArrayList<String>(0); // mod types
		    // String killMsg = "";

		    try { // get info about zip archive
			ZipFile newModZip = new ZipFile(newMod);
			Enumeration<? extends ZipEntry> e = newModZip.entries();
			files = new ArrayList<File>();
			while (e.hasMoreElements()) {
			    ZipEntry entry = (ZipEntry) e.nextElement();
			    files.add(new File(entry.getName()));
			}
		    } catch (IOException ex) {
			l.log(ex);
		    }
		    // determine the types of the mod
		    for (int i = 0; i < files.size(); i++) {
			files.set(i, new File(m.cleanFilePath(files.get(i).toString())));
			String fileType = m.fileKey(files.get(i));
			if (!types.contains(fileType) && !fileType.isEmpty()) {
			    types.add(fileType);
			}
		    }

		    // extract files to the modLibrary
		    final int BUFFER = 2048;
		    try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(newMod);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
			    int count;
			    byte data[] = new byte[BUFFER];
			    // write the files to the disk
			    String path = m.cleanFilePath(entry.getName());
			    newFile = new File(m.homeDirectory + m.modLibrary + newName + path);
			    if (!newFile.exists() && entry.isDirectory()) {
				newFile.mkdirs();
			    } else if (!path.isEmpty()) {
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile, false);
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
				    dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			    }
			}
			zis.close();
		    } catch (Exception ex) {
			l.log(ex);
		    }

		    // add new mod object to modList
		    m.modList.add(new Modification(newName, types, files));

		    m.rewriteMenus();
		    m.reloadComboboxes();
		    m.setStatus("Added \"" + newName + "\"");
		    dispose();
		}
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.SOUTH, btnAddMod, -10, SpringLayout.SOUTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, btnAddMod, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(btnAddMod);

	pathTextField = new JTextField();
	sl_contentPanel.putConstraint(SpringLayout.NORTH, pathTextField, 15, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.WEST, pathTextField, 10, SpringLayout.WEST, contentPanel);
	contentPanel.add(pathTextField);
	pathTextField.setColumns(10);

	JButton btnSelectFile = new JButton("Select File");
	btnSelectFile.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		FileChooser fc = new FileChooser();
		fc.setFilterModification();
		String filePath = fc.showFileChooser();
		pathTextField.setText(filePath);
	    }
	});
	sl_contentPanel.putConstraint(SpringLayout.NORTH, btnSelectFile, 14, SpringLayout.NORTH, contentPanel);
	sl_contentPanel.putConstraint(SpringLayout.EAST, pathTextField, -6, SpringLayout.WEST, btnSelectFile);
	sl_contentPanel.putConstraint(SpringLayout.EAST, btnSelectFile, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(btnSelectFile);

	nameTextField = new JTextField();
	sl_contentPanel.putConstraint(SpringLayout.NORTH, nameTextField, 15, SpringLayout.SOUTH, btnSelectFile);
	sl_contentPanel.putConstraint(SpringLayout.EAST, nameTextField, -10, SpringLayout.EAST, contentPanel);
	contentPanel.add(nameTextField);
	nameTextField.setColumns(10);

	JLabel lblModName = new JLabel("Mod Name:");
	sl_contentPanel.putConstraint(SpringLayout.WEST, nameTextField, 10, SpringLayout.EAST, lblModName);
	sl_contentPanel.putConstraint(SpringLayout.NORTH, lblModName, 3, SpringLayout.NORTH, nameTextField);
	sl_contentPanel.putConstraint(SpringLayout.WEST, lblModName, 0, SpringLayout.WEST, btnCancel);
	contentPanel.add(lblModName);

	this.setVisible(true);
    }
}
