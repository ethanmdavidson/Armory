package armory;

import java.awt.EventQueue;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.Box;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.commons.exec.OS;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;

public class Main {

    private JFrame mainJFrame;
    private ModHandler m;
    private Logger l;
    private JLabel label;
    private JComboBox knifeComboBox;
    private JComboBox pistolComboBox;
    private JComboBox carbineComboBox;
    private JComboBox shotgunComboBox;
    private JComboBox subgunComboBox;
    private JComboBox sniperComboBox;
    private JComboBox assaultComboBox;
    private JComboBox grenadeComboBox;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
	EventQueue.invokeLater(new Runnable() {
	    public void run() {
		try {
		    Main window = new Main();
		    window.mainJFrame.setVisible(true);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    /**
     * Create the application.
     */
    public Main() {
	initialize();
	l = new Logger();
	m = new ModHandler(this, l);
	m.reloadComboboxes();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
	mainJFrame = new JFrame();
	mainJFrame.setLocationByPlatform(true);
	mainJFrame.setTitle("Armory");
	mainJFrame.setBounds(100, 100, 400, 400);
	mainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	SpringLayout springLayout = new SpringLayout();
	mainJFrame.getContentPane().setLayout(springLayout);

	JMenuBar menuBar = new JMenuBar();
	springLayout.putConstraint(SpringLayout.NORTH, menuBar, 0, SpringLayout.NORTH, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.WEST, menuBar, 0, SpringLayout.WEST, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.SOUTH, menuBar, 21, SpringLayout.NORTH, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, menuBar, 0, SpringLayout.EAST, mainJFrame.getContentPane());
	mainJFrame.getContentPane().add(menuBar);

	JMenu mnFile = new JMenu("File");
	menuBar.add(mnFile);

	JMenuItem mntmAddAMod = new JMenuItem("Add a Mod");
	mntmAddAMod.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		m.addMod();
	    }
	});
	mnFile.add(mntmAddAMod);

	JMenuItem mntmRemoveAMod = new JMenuItem("Remove a Mod");
	mntmRemoveAMod.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		new RemoveModPrompt(m);
	    }
	});
	mnFile.add(mntmRemoveAMod);

	JMenuItem mntmEditModInfo = new JMenuItem("Edit Mod Info");
	mntmEditModInfo.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		new EditModPrompt(m, l);
	    }
	});
	mnFile.add(mntmEditModInfo);

	JMenu mnEdit = new JMenu("Edit");
	menuBar.add(mnEdit);

	JMenuItem mntmChangeHomeDirectory = new JMenuItem("Change Home Directory");
	mntmChangeHomeDirectory.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		FileChooser fc = new FileChooser();
		fc.setFilterFolder();
		String newHomeDir = fc.showFileChooser();
		BooleanPrompt gp = new BooleanPrompt("Are you sure you want to move your home directory? This will copy all the files from it's current location to " + newHomeDir);
		if (!newHomeDir.isEmpty() && gp.showPrompt()) {
		    l.log("Changing HomeDir to: " + newHomeDir);
		    if (OS.isFamilyMac()) { // manipulate to the gamedata folder
			if (newHomeDir.contains("/AssaultCube.app")) {
			    newHomeDir = newHomeDir.substring(0, newHomeDir.indexOf("/AssaultCube.app")) + "/AssaultCube.app/Contents/gamedata";
			}
		    }
		    if (new File(newHomeDir).exists()) {
			m.migrateHomeDir(newHomeDir);
		    }
		}
	    }
	});
	mnEdit.add(mntmChangeHomeDirectory);

	JMenuItem mntmClearSavedData = new JMenuItem("Clear Saved Data");
	mntmClearSavedData.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		Boolean confirm = new BooleanPrompt("Are you sure you want to reset the saved data? This will reset Armory's preference cache. You should not do this unless you know what you are doing.").showPrompt();
		if (confirm) {
		    try {
			m.prefs.clear();
			l.log("Prefs reset");
			new ErrorPrompt("Armory has successfully been reset and will now exit");
			System.exit(0);
		    } catch (BackingStoreException ex) {
			l.log(ex);
		    }
		}
	    }
	});
	mnEdit.add(mntmClearSavedData);

	JMenu mnHelp = new JMenu("Help");
	menuBar.add(mnHelp);

	JMenuItem mntmShowLogger = new JMenuItem("Show Logger");
	mntmShowLogger.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		l.setVisible(true);
	    }
	});
	mnHelp.add(mntmShowLogger);

	JMenuItem mntmHowToUse = new JMenuItem("How to use Armory");
	mntmHowToUse.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    new HowToArmory();
		}
	});
	mnHelp.add(mntmHowToUse);

	JMenuItem mntmAboutArmory = new JMenuItem("About Armory");
	mntmAboutArmory.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    new AboutArmory();
		}
	});
	mnHelp.add(mntmAboutArmory);

	label = new JLabel("Welcome to the Armory!");
	springLayout.putConstraint(SpringLayout.NORTH, label, 6, SpringLayout.SOUTH, menuBar);
	springLayout.putConstraint(SpringLayout.WEST, label, 10, SpringLayout.WEST, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, label, -10, SpringLayout.EAST, mainJFrame.getContentPane());
	label.setFont(new Font("Tahoma", Font.PLAIN, 14));
	mainJFrame.getContentPane().add(label);

	JSeparator separator = new JSeparator();
	springLayout.putConstraint(SpringLayout.NORTH, separator, 6, SpringLayout.SOUTH, label);
	springLayout.putConstraint(SpringLayout.SOUTH, separator, 16, SpringLayout.SOUTH, label);
	springLayout.putConstraint(SpringLayout.WEST, separator, 10, SpringLayout.WEST, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, separator, -10, SpringLayout.EAST, mainJFrame.getContentPane());
	mainJFrame.getContentPane().add(separator);

	Box horizontalBox = Box.createHorizontalBox();
	springLayout.putConstraint(SpringLayout.NORTH, horizontalBox, 6, SpringLayout.NORTH, separator);
	springLayout.putConstraint(SpringLayout.WEST, horizontalBox, 10, SpringLayout.WEST, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.SOUTH, horizontalBox, -10, SpringLayout.SOUTH, mainJFrame.getContentPane());
	springLayout.putConstraint(SpringLayout.EAST, horizontalBox, -10, SpringLayout.EAST, mainJFrame.getContentPane());
	mainJFrame.getContentPane().add(horizontalBox);

	Box labelVerticalBox = Box.createVerticalBox();
	horizontalBox.add(labelVerticalBox);

	Component verticalGlue_5 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_5);

	JLabel lblKnife = new JLabel("Knife");
	labelVerticalBox.add(lblKnife);

	Component verticalGlue = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue);

	JLabel lblNewLabel = new JLabel("Pistol");
	lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblNewLabel);

	Component verticalGlue_3 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_3);

	JLabel lblCarbine = new JLabel("Carbine");
	lblCarbine.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblCarbine);

	Component verticalGlue_4 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_4);

	JLabel lblShotgun = new JLabel("Shotgun");
	lblShotgun.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblShotgun);

	Component verticalGlue_13 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_13);

	JLabel lblSubgun = new JLabel("Subgun");
	lblSubgun.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblSubgun);

	Component verticalGlue_17 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_17);

	JLabel lblSniper = new JLabel("Sniper");
	lblSniper.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblSniper);

	Component verticalGlue_16 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_16);

	JLabel lblAssault = new JLabel("Assault");
	lblAssault.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblAssault);

	Component verticalGlue_15 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_15);

	JLabel lblGrenades = new JLabel("Grenade");
	lblGrenades.setVerticalAlignment(SwingConstants.TOP);
	labelVerticalBox.add(lblGrenades);

	Component verticalGlue_14 = Box.createVerticalGlue();
	labelVerticalBox.add(verticalGlue_14);

	Component horizontalStrut = Box.createHorizontalStrut(10);
	horizontalBox.add(horizontalStrut);

	Box comboVerticalBox = Box.createVerticalBox();
	horizontalBox.add(comboVerticalBox);

	Component verticalGlue_6 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_6);

	knifeComboBox = new JComboBox();
	knifeComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		m.changeMod(knifeComboBox.getSelectedItem().toString(), 0);
	    }
	});
	comboVerticalBox.add(knifeComboBox);

	Component verticalGlue_1 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_1);

	pistolComboBox = new JComboBox();
	pistolComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(pistolComboBox.getSelectedItem().toString(), 1);
	    }
	});
	comboVerticalBox.add(pistolComboBox);

	Component verticalGlue_2 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_2);

	carbineComboBox = new JComboBox();
	carbineComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(carbineComboBox.getSelectedItem().toString(), 2);
	    }
	});
	comboVerticalBox.add(carbineComboBox);

	Component verticalGlue_7 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_7);

	shotgunComboBox = new JComboBox();
	shotgunComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(shotgunComboBox.getSelectedItem().toString(), 3);
	    }
	});
	comboVerticalBox.add(shotgunComboBox);

	Component verticalGlue_8 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_8);

	subgunComboBox = new JComboBox();
	subgunComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(subgunComboBox.getSelectedItem().toString(), 4);
	    }
	});
	comboVerticalBox.add(subgunComboBox);

	Component verticalGlue_12 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_12);

	sniperComboBox = new JComboBox();
	sniperComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(sniperComboBox.getSelectedItem().toString(), 5);
	    }
	});
	comboVerticalBox.add(sniperComboBox);

	Component verticalGlue_11 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_11);

	assaultComboBox = new JComboBox();
	assaultComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(assaultComboBox.getSelectedItem().toString(), 6);
	    }
	});
	comboVerticalBox.add(assaultComboBox);

	Component verticalGlue_10 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_10);

	grenadeComboBox = new JComboBox();
	grenadeComboBox.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		m.changeMod(assaultComboBox.getSelectedItem().toString(), 8);
	    }
	});
	comboVerticalBox.add(grenadeComboBox);

	Component verticalGlue_9 = Box.createVerticalGlue();
	comboVerticalBox.add(verticalGlue_9);
    }

    void setLabel(String msg) {
	label.setText(msg);
    }

    void setKnifeComboBox(Object[] weps, String cur) {
	knifeComboBox.setModel(new DefaultComboBoxModel(weps));
	knifeComboBox.setSelectedItem(cur);
    }

    void setPistolComboBox(Object[] weps, String cur) {
	pistolComboBox.setModel(new DefaultComboBoxModel(weps));
	pistolComboBox.setSelectedItem(cur);
    }

    void setCarbineComboBox(Object[] weps, String cur) {
	carbineComboBox.setModel(new DefaultComboBoxModel(weps));
	carbineComboBox.setSelectedItem(cur);
    }

    void setShotgunComboBox(Object[] weps, String cur) {
	shotgunComboBox.setModel(new DefaultComboBoxModel(weps));
	shotgunComboBox.setSelectedItem(cur);
    }

    void setSubgunComboBox(Object[] weps, String cur) {
	subgunComboBox.setModel(new DefaultComboBoxModel(weps));
	subgunComboBox.setSelectedItem(cur);
    }

    void setSniperComboBox(Object[] weps, String cur) {
	sniperComboBox.setModel(new DefaultComboBoxModel(weps));
	sniperComboBox.setSelectedItem(cur);
    }

    void setAssaultComboBox(Object[] weps, String cur) {
	assaultComboBox.setModel(new DefaultComboBoxModel(weps));
	assaultComboBox.setSelectedItem(cur);
    }

    void setGrenadeComboBox(Object[] weps, String cur) {
	grenadeComboBox.setModel(new DefaultComboBoxModel(weps));
	grenadeComboBox.setSelectedItem(cur);
    }
}
