package armory;

/* Change with each version of AC:
 * -aboutVersionLabel (and it's tooltip)
 * -add old prefs string to oldPrefs list and update prefs object
 * -check for any changes in folder names
 * -update "default" mod package
 */

/* How to add a new mod to be controlled with Armory:
 * 1) add the combobox and label objects
 * 2) add it's case to intToKey() in the first available slot
 * 3) add event listener actions for the combobox
 * 4) add it's combobox to reloadComboboxes()
 * 5) add it's folder to cleanFilePath()
 * 6) add it's files to fileKey()
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class ModHandler {

    int KEY_AMT = 8; // TODO: this must match the number of weapon types armory is tracking
    String homeDirectory = null;
    String modLibrary;
    ArrayList<Modification> modList;
    Preferences prefs;
    Main m;
    Logger l;

    public ModHandler(Main main, Logger logger) {
	modLibrary = "/packages/models/weapons/modLibrary/";
	modList = new ArrayList<Modification>(1);
	prefs = Preferences.userRoot().node("Armory1200");
	m = main;
	l = logger;
	KEY_AMT = countKeyCases();

	updatePrefs();
	homeDirCheck();
	updateModLibrary();
	loadModLibrary();
	updateRifles();
	rewriteMenus();
	l.log("Armory fully loaded!");
    }

    private void homeDirCheck() {
	// checks for and loads the homeDir path from prefs, prompts if nonexistant
	if (prefs.get("HOMEDIR", "").isEmpty() || !new File(prefs.get("HOMEDIR", "")).exists()) {
	    l.log("HomeDir not found, prompting");
	    HomeDirPrompt hdp = new HomeDirPrompt(this, l);
	    hdp.setVisible(true);
	} else {
	    homeDirectory = prefs.get("HOMEDIR", "");
	    if (homeDirectory.isEmpty()) {
		l.log("getHomeDir returned empty");
		m.setLabel("Error: homedir not found.");
	    } else {
		l.log("Using homedir: " + homeDirectory);
	    }
	}
    }

    public void setStatus(String msg) {
	m.setLabel(msg);
    }

    public void addMod() {
	new AddModPrompt(this, l);
    }

    String intToKey(int type) { // returns the name of the given type.
	// used for prefs keys, folder names,
	switch (type) {
	case 0:
	    return "knife";
	case 1:
	    return "pistol";
	case 2:
	    return "carbine";
	case 3:
	    return "shotgun";
	case 4:
	    return "subgun";
	case 5:
	    return "sniper";
	case 6:
	    return "assault";
	case 7:
	    return "cpistol"; // TODO: update this with the correct cpistol key
	case 8:
	    return "grenade";
	default: // return empty if it isn't a mod type
	    l.log("typeToKey() hit default: " + type);
	    return "";
	}
    }

    private String cleanFilePath(File path) {
	// runs cleanFilePath with the given path, after correcting backslashes
	String fixedPath = path.toString().replace("\\", "/");
	return cleanFilePath(fixedPath);
    }

    String cleanFilePath(String path) { // normalize the filepaths (/packages/models/weapons/etc...)
	String result = "";
	if (path.indexOf("packages/") > 0) { // if below packages, reduce
	    result = "/" + path.substring(path.lastIndexOf("packages/"));
	} else if (path.indexOf("models/") >= 0) { // if above packages, extrapolate
	    result = "/packages/" + path.substring(path.lastIndexOf("models/"));
	} else if (path.indexOf("weapons/") >= 0) {
	    result = "/packages/models/" + path.substring(path.lastIndexOf("weapons/"));
	} else if (path.indexOf("knife/") >= 0) { // clean paths up too, if necessary
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("knife/"));
	} else if (path.indexOf("pistol/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("pistol/"));
	} else if (path.indexOf("assault/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("assault/"));
	} else if (path.indexOf("sniper/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("sniper/"));
	} else if (path.indexOf("subgun/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("subgun/"));
	} else if (path.indexOf("shotgun/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("shotgun/"));
	} else if (path.indexOf("carbine/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("carbine/"));
	} else if (path.indexOf("rifle/") >= 0) { // legacy name for carbine, corrected below
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("rifle/"));
	} else if (path.indexOf("grenade/") >= 0) {
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("grenade/"));
	} else if (path.indexOf("cpistol/") >= 0) { // TODO: update to correct combat pistol name
	    result = "/packages/models/weapons/" + path.substring(path.lastIndexOf("cpistol/"));
	} else if (path.indexOf("audio/") >= 0) {
	    result = "/packages/" + path.substring(path.lastIndexOf("audio/"));
	} else if (path.indexOf("sounds/") >= 0) {
	    result = "/packages/audio/" + path.substring(path.lastIndexOf("sounds/"));
	} else if (path.indexOf("weapon/") >= 0) {
	    result = "/packages/audio/sounds/" + path.substring(path.lastIndexOf("weapon/"));
	} else if (path.indexOf("misc/") >= 0) {
	    result = "/packages/" + path.substring(path.lastIndexOf("misc/"));
	}
	result = result.replace("/weapons/rifle/", "/weapons/carbine/"); // correct old carbine mods
	result = result.replace("/sounds/weapon/rifle", "/sounds/weapon/carbine");
	return result;
    }

    String fileKey(File file) { // runs fileKey with the given file
	// object, after converting to string and correcting backslashes
	String fixedPath = file.toString().replace("\\", "/");
	return fileKey(fixedPath);
    }

    String fileKey(String path) { // checks what mod type the given path belongs to
	if (path.indexOf("/weapons/") > 0 && path.indexOf("/", path.indexOf("/weapons/") + 9) > 0) {
	    return path.substring( // substring containing mod key
	    path.indexOf("/weapons/") + 9, // after "weapons\"
		    path.indexOf("/", path.indexOf("/weapons/") + 9) // until next seperator
	    ); // i.e. weapons/***/ (finds stars)
	} else if (path.indexOf("/sounds/weapon/") > 0 && path.indexOf(".", path.indexOf("/sounds/weapon/") + 15) > 0) {
	    // deal with sound files on a case by case basis
	    String file = new File(path).getName().toString();
	    file = file.substring(0, file.lastIndexOf("."));
	    if (file.equalsIgnoreCase("auto") || file.equalsIgnoreCase("auto_reload")) {
		return "assault";
	    } else if (file.equalsIgnoreCase("grenade_bounce1") || file.equalsIgnoreCase("grenade_bounce2") || file.equalsIgnoreCase("grenade_exp") || file.equalsIgnoreCase("grenade_pull") || file.equalsIgnoreCase("grenade_throw")) {
		return "grenade";
	    } else if (file.equalsIgnoreCase("knife")) {
		return "knife";
	    } else if (file.equalsIgnoreCase("usp") || file.equalsIgnoreCase("pistol_akreload") || file.equalsIgnoreCase("pistol_reload")) {
		return "pistol";
	    } else if (file.equalsIgnoreCase("rifle") || file.equalsIgnoreCase("rifle_reload") || file.equalsIgnoreCase("carbine") || file.equalsIgnoreCase("carbine_reload")) {
		return "carbine";
	    } else if (file.equalsIgnoreCase("shotgun") || file.equalsIgnoreCase("shotgun_reload")) {
		return "shotgun";
	    } else if (file.equalsIgnoreCase("sniper") || file.equalsIgnoreCase("sniper_reload")) {
		return "sniper";
	    } else if (file.equalsIgnoreCase("sub") || file.equalsIgnoreCase("sub_reload")) {
		return "subgun";
	    } else if (file.equalsIgnoreCase("cpistol") // purely hypothetical ;)
		    || file.equalsIgnoreCase("cpistol_reload")) { // TODO: update these values to w/e they turn out to be
		return "cpistol";
	    }
	} else if (path.indexOf("/misc/") > 0 && path.indexOf(".", path.indexOf("/misc/") + 6) > 0) {
	    // support for files in misc folder, same strategy as with sound files
	    String file = new File(path).getName().toString();
	    file = file.substring(0, file.lastIndexOf("."));
	    if (file.equalsIgnoreCase("scope") || file.equalsIgnoreCase("smoke")) {
		return "sniper";
	    } else if (file.equalsIgnoreCase("scorch") || file.equalsIgnoreCase("explosion")) {
		return "grenade";
	    }
	}
	// armory.Logger.log("fileKey() empty: " + path);
	return ""; // return empty if file doesn't fit any key
    }

    void rewriteMenus() {
	// creates an array of lists of mods for each type, passes it to the real method
	Collections.sort(modList, new Comparator<Object>() {
	    // alphabetize
	    @Override
	    public int compare(Object o1, Object o2) {
		Modification m1 = (Modification) o1;
		Modification m2 = (Modification) o2;
		return m1.toString().compareToIgnoreCase(m2.toString());
	    }
	});
	// then create the list of mods sorted by type
	@SuppressWarnings("unchecked")
	ArrayList<String>[] weapons = new ArrayList[KEY_AMT];
	for (int i = 0; i < weapons.length; i++) {
	    weapons[i] = new ArrayList<String>();
	}
	for (int i = 0; i < modList.size(); i++) {
	    Modification mod = modList.get(i);
	    for (int k = 0; k < KEY_AMT; k++) {
		if (mod.getTypes().contains(intToKey(k))) {
		    weapons[k].add(mod.toString());
		}
	    }
	}
	rewriteMenus(weapons);
    }

    private void rewriteMenus(ArrayList<String>[] weps) {
	// creates cubescript menus for changing weapons in-game
	l.log("Writing menus");
	File wepMenu = new File(homeDirectory + "/scripts/ArmoryWeaponMenu.cfg");
	new File(wepMenu.getParent()).mkdirs();
	try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(wepMenu, false));
	    writer.write("showarmorymenu = [ showmenu Armory ]");
	    writer.newLine();
	    writer.write("newmenu \"Armory\"");
	    writer.newLine();
	    for (int i = 0; i < weps.length; i++) {
		if (weps[i].size() > 0) {
		    writer.write("menuitem \"" + intToKey(i) + "\" [ showmenu " + intToKey(i) + " ]");
		    writer.newLine();
		}
	    }
	    writer.write("menuitem \"\" []");
	    writer.newLine();
	    writer.write("menuitem \"close\" [ closemenu Armory ]");
	    writer.newLine();
	    for (int i = 0; i < weps.length; i++) {
		if (weps[i].size() > 0) { // don't create empty menus
		    writer.newLine();
		    writer.write("newmenu \"" + intToKey(i) + "\"");
		    writer.newLine();
		    for (int j = 0; j < weps[i].size(); j++) {
			writer.write("menuitem \"" + weps[i].get(j) + "\" [ persistidents 0; modmdlweap" + Integer.toString(i) + " = \"modLibrary/" + weps[i].get(j) + "/packages/models/weapons/" + intToKey(i) + "\"; echo \"" + intToKey(i) + " changed to " + weps[i].get(j) + "\"; persistidents 1 ]");
			writer.newLine();
		    }
		    writer.write("menuitem \"\" []");
		    writer.newLine();
		    writer.write("menuitem \"close\" [ closemenu " + intToKey(i) + " ]");
		    writer.newLine();
		}
	    }
	    writer.flush();
	    writer.close();
	    l.log("Menu writing successful");
	} catch (IOException ex) {
	    l.log(ex);
	}
    }

    void reloadComboboxes() { // reloads all the comboboxes
	l.log("Reloading comboboxes");
	for (int i = 0; i < KEY_AMT; i++) {
	    ArrayList<String> modNames = new ArrayList<String>(1);
	    for (int j = 0; j < modList.size(); j++) {
		if (modList.get(j).getTypes().contains(intToKey(i))) {
		    modNames.add(modList.get(j).toString());
		}
	    }
	    if (modNames.size() < 1) {
		l.log("Empty modlist for " + intToKey(i));
		modNames.add("Error: No mods found!");
	    } else {
		switch (i) { // set combobox value for new mod
		case 0:
		    m.setKnifeComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 1:
		    m.setPistolComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 2:
		    m.setCarbineComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 3:
		    m.setShotgunComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 4:
		    m.setSubgunComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 5:
		    m.setSniperComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 6:
		    m.setAssaultComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 7:
		    // m.setCpistolComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		case 8:
		    m.setGrenadeComboBox(modNames.toArray(), prefs.get(intToKey(i), "Default"));
		    break;
		}
	    }
	}
    }

    public void removeMod(String name) {
	// removes the mod with the given name (delete from modLibrary, remove from use)
	l.log("Attempting to remove: " + name);
	if (!name.equalsIgnoreCase("No mods to delete.") && !name.equalsIgnoreCase("Default")) {
	    for (int i = 0; i < modList.size(); i++) {
		Modification tempMod = modList.get(i);
		if (tempMod.toString().equalsIgnoreCase(name)) {
		    modList.remove(i); // remove mod from the modlist
		    for (int j = 0; j < tempMod.getPathAmt(); j++) { // delete files in /packages
			File oldFile = new File(homeDirectory + tempMod.getPathStr(j));
			// don't delete files from other mods, or folders
			if (oldFile.isFile() && prefs.get(fileKey(oldFile), "Default").equalsIgnoreCase(name)) {
			    oldFile.delete();
			}
		    }

		    File libData = new File(homeDirectory + modLibrary + tempMod.toString());
		    try {
			// delete the mod's folder in modLibrary
			FileUtils.deleteDirectory(libData);
		    } catch (IOException ex) {
			l.log(ex);
		    }
		    for (int j = 0; j < KEY_AMT; j++) {
			if (prefs.get(intToKey(j), "Default").equalsIgnoreCase(name)) {
			    changeMod("Default", i); // if the mod was selected, set it back to default
			}
		    }
		    rewriteMenus();
		    reloadComboboxes();
		    setStatus("Removed \"" + tempMod.toString() + "\"");
		    break;
		}
	    }
	}
    }

    public void changeMod(String newName, int type) {
	// changes the given weapon type to the mod with the given name
	// delete files from last mod
	if (!newName.equalsIgnoreCase(prefs.get(intToKey(type), "Default"))) {
	    // don't bother doing anything if nothing is changed
	    l.log("Changing " + intToKey(type) + " to " + newName);
	    setStatus("Switching mods, please wait...");
	    for (int i = 0; i < modList.size(); i++) {
		Modification tempMod = modList.get(i);
		if (tempMod.isNamed(prefs.get(intToKey(type), "Default"))) { // find the old/ mod
		    for (int j = 0; j < tempMod.getPathAmt(); j++) { // and delete it's files
			File oldFile = new File(homeDirectory + tempMod.getPathStr(j));
			if (fileKey(oldFile).equalsIgnoreCase(intToKey(type)) && !oldFile.isDirectory()) {
			    if (!oldFile.delete()) { // but only the type being replaced (and not directories)
				l.log("Failed deleting old: " + oldFile.toString());
			    }
			}
		    }
		    break;
		}
	    }

	    // copy in files from new mod
	    for (int i = 0; i < modList.size(); i++) {
		Modification tempMod = modList.get(i);
		if (tempMod.isNamed(newName)) {
		    for (int j = 0; j < tempMod.getPathAmt(); j++) {
			File fromFile = new File(homeDirectory + modLibrary + tempMod.toString() + cleanFilePath(tempMod.getPathStr(j)));
			File toFile = new File(homeDirectory + cleanFilePath(tempMod.getPathStr(j)));
			if (fileKey(toFile).equalsIgnoreCase(intToKey(type))) {
			    // only for the changed weapon type
			    if (toFile.isDirectory() || fromFile.isDirectory()) {
				toFile.mkdirs();
			    } else {
				new File(toFile.getParent()).mkdirs();
				try {
				    FileUtils.copyFile(fromFile, toFile);
				} catch (IOException ex) {
				    l.log(ex);
				}
			    }
			}
		    }
		    // put in missed files from default
		    for (int j = 0; j < modList.size(); j++) {
			if (modList.get(j).toString().equalsIgnoreCase("Default")) {
			    Modification defaultMod = modList.get(j);
			    for (int k = 0; k < defaultMod.getPathAmt(); k++) {
				if (fileKey(defaultMod.getPathStr(k)).equals(intToKey(type)) && !new File(homeDirectory + defaultMod.getPathStr(k)).exists()) {
				    try {
					FileUtils.copyFile(new File(homeDirectory + modLibrary + defaultMod.toString() + defaultMod.getPathStr(k)), new File(homeDirectory + defaultMod.getPathStr(k)));
				    } catch (IOException ex) {
					l.log(ex);
				    }
				}
			    }
			    break;
			}
		    }
		    break;
		}
	    }

	    prefs.put(intToKey(type), newName);
	    reloadComboboxes();
	    setStatus("Changed mod successfully.");
	} else {
	    // armory.Logger.log("changeMod() needlessly called");
	}
    }

    boolean fileIsValid(File newFile) { // performs a cursory check of
	// the selected .zip file
	if ((!newFile.isFile() || newFile.toString().isEmpty())) {
	    l.log("User Error: no file specified");
	    new ErrorPrompt("You did not select a valid file. Please press the \"Select\" button.");
	    return false;
	} else if (!newFile.canRead()) {
	    l.log("User Error: \"" + newFile.toString() + "\" error getting .zip");
	    new ErrorPrompt("Error accessing the selected file. Please check permissions for the chosen file.");
	    return false;
	}
	return true;
    }

    boolean nameIsValid(String newName) { // returns false if the given
	// string is an unacceptable
	// name for a mod
	if (newName.length() < 1) { // if the name field is blank. Can be
	    // modified to easily set a minimum length
	    // for names
	    l.log("User Error: \"" + newName + "\" too short");
	    new ErrorPrompt("You did not enter a name for your mod. Please fill out the \"Mod Name\" field.");
	    return false;
	} else if (((newName.equalsIgnoreCase("Default")) // scan for invalid
		// names and illegal
		// chars
		|| newName.contains("/") || newName.contains("?") || newName.contains("<") || newName.contains(">") || newName.contains(":") || newName.contains("*") || newName.contains("\"") || newName.contains("|") || newName.contains("\\"))) {
	    l.log("User Error: \"" + newName + "\" contains invalid char(s)");
	    new ErrorPrompt("The name you entered was invalid. Please choose a new name");
	    return false;
	} else if (newName.length() > 30) { // set a max name length of 30 characters
	    l.log("User Error: \"" + newName + "\" is too long");
	    new ErrorPrompt("The name you chose was too long. Choose a name with less than 30 characters.");
	    return false;
	}
	for (int i = 0; i < modList.size(); i++) {
	    if (modList.get(i).isNamed(newName)) { // if name is already in use
		l.log("User Error: \"" + newName + "\" already taken");
		new ErrorPrompt("The name you have chosen is already in use. Please choose a new name.");
		return false;
	    }
	}
	for (int i = 0; i < KEY_AMT; i++) { // make sure the name isn't the same as any keys
	    if (newName.equals(intToKey(i))) {
		return false;
	    }
	}
	return true;
    }

    public void migrateHomeDir(String newHomeDir) {
	File from = new File(homeDirectory + modLibrary);
	File to = new File(newHomeDir + modLibrary);
	if (!to.exists()) {
	    to.mkdirs();
	}
	try {
	    if (from.exists() && !from.toString().equals(to.toString())) {
		FileUtils.copyDirectory(from, to);
	    }
	} catch (IOException ex) {
	    l.log(ex);
	}

	// save the new home dir
	prefs.put("HOMEDIR", newHomeDir);
	homeDirectory = newHomeDir;

	// re-extract the default files if needed
	extractDefault();
	rewriteMenus();
    }

    private void extractDefault() { // extracts Default mod to the modLibrary
	boolean hasDefault = false;
	for (int i = 0; i < modList.size(); i++) {
	    if (modList.get(i).toString().equalsIgnoreCase("Default")) {
		hasDefault = true;
	    }
	}
	if (!hasDefault) {
	    l.log("Extracting default mod");
	    ArrayList<File> filepaths = new ArrayList<File>();
	    final int BUFFER = 2048;
	    try {
		BufferedOutputStream dest = null;
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("Default.zip");
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
		    int count;
		    byte data[] = new byte[BUFFER];
		    // write the files to the disk
		    String path = cleanFilePath(entry.getName());
		    File defLibPath = new File(homeDirectory + modLibrary + "Default" + path);
		    if (!defLibPath.exists() && entry.isDirectory()) { // if it doesn't exist and is a directory
			defLibPath.mkdirs(); // create the necessary directories
			filepaths.add(new File(path));
		    } else if (!path.isEmpty() && !entry.isDirectory()) {
			// otherwise create the file (if it's not useless or already there)
			new File(defLibPath.getParent()).mkdirs();
			filepaths.add(new File(path));
			FileOutputStream fos = new FileOutputStream(defLibPath, false);
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1) {
			    dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		    }
		}
	    } catch (Exception ex) {
		l.log(ex);
	    }
	    // set up default mod in modRegistry and modList
	    ArrayList<String> types = new ArrayList<String>();
	    for (int i = 0; i < KEY_AMT; i++) { // add all the types
		types.add(intToKey(i));
	    }
	    modList.add(new Modification("Default", types, filepaths));
	}
    }

    private int countKeyCases() { // automagically counts the number of mod
	// types managed by Armory
	int i = 0;
	while (!intToKey(i).isEmpty()) {
	    i++;
	}
	if (i < KEY_AMT) { // prevent errors from counting incorrectly
	    l.log("countKeyCases() < KEY_AMT");
	    i = KEY_AMT;
	}
	return i;
    }

    private void updateModLibrary() { // update function, to move modLibrary from it's old position
	File oldLibrary = new File(homeDirectory + "/modLibrary/");
	File newLibrary = new File(homeDirectory + modLibrary);
	if (oldLibrary.exists() && !newLibrary.exists()) {
	    l.log("Updating modLibrary position");
	    try {
		FileUtils.copyDirectory(oldLibrary, newLibrary);
		// FileUtils.deleteDirectory(oldLibrary);
	    } catch (IOException ex) {
		l.log(ex);
	    }
	}
    }

    private void loadModLibrary() { // finds all the mods saved in the modLibrary
	// no longer uses the registry :D
	modList.clear();
	File modReg = new File(homeDirectory + modLibrary);
	l.log("Loading mod Library: " + modReg);
	if (modReg.exists()) {
	    String[] mods = modReg.list();
	    for (int i = 0; i < mods.length; i++) {
		File modDir = new File(homeDirectory + modLibrary + mods[i]);
		if (modDir.isDirectory()) {
		    String modName = modDir.getName();
		    List<File> paths = (List<File>) FileUtils.listFiles(modDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		    ArrayList<String> types = new ArrayList<String>();
		    for (int j = 0; j < paths.size(); j++) { // determine mod types
			paths.set(j, new File(cleanFilePath(paths.get(j))));
			String fileType = fileKey(paths.get(j));
			if (!types.contains(fileType) && !fileType.isEmpty()) {
			    types.add(fileType);
			}
		    }
		    modList.add(new Modification(modName, types, new ArrayList<File>(paths)));
		}
	    }
	}
	extractDefault(); // make sure Default mod is in place
    }

    private void updateRifles() {
	// update function, corrects from "rifle" to "carbine" in mods already added
	for (int i = 0; i < modList.size(); i++) {
	    if (modList.get(i).getTypes().contains("rifle") || modList.get(i).getTypes().contains("carbine")) {
		Modification tempMod = modList.get(i);
		boolean needsRewrite = false;
		for (int j = 0; j < tempMod.getPathAmt(); j++) {
		    String old = tempMod.getPathStr(j);
		    String fixed = cleanFilePath(tempMod.getFile(j));
		    if (!old.equals(fixed)) {
			new File(homeDirectory + modLibrary + tempMod.toString() + old).renameTo(new File(homeDirectory + modLibrary + tempMod.toString() + fixed));
			tempMod.setPath(j, fixed);
			needsRewrite = true;
			l.log("corrected: " + old + " > " + fixed);
		    }
		}
		if (needsRewrite) {
		    l.log("Corrected \"" + tempMod.toString() + "\"");
		    rewriteMenus();
		}
	    }
	}
    }

    private void updatePrefs() {
	try {
	    if (!getLastVersion().isEmpty() && prefs.keys().length <= 0) {
		l.log("Prompting user to update prefs");
		Boolean confirm = new BooleanPrompt("Press continue to import your settings and data from a previous version of Armory. You should only use this if your home directory is the same for both installations of AssaultCube.").showPrompt();
		if (confirm) {
		    String lastVersion = getLastVersion();
		    if (!lastVersion.isEmpty()) {
			updatePrefs(lastVersion);
		    } else {
			l.log("Tried to update, no old versions");
		    }
		}
	    } else if (prefs.keys().length <= 0) {
		l.log("No previous version found.");
	    }
	} catch (BackingStoreException ex) {
	    l.log(ex);
	}
    }

    private void updatePrefs(String version) {
	// update function, copies old prefs into new prefs updates prefs from last version's
	Preferences oldPrefs = Preferences.userRoot().node(version);
	l.log("Updating prefs from " + version);
	// HOMEDIR
	if (!oldPrefs.get("HOMEDIR", "").isEmpty() && prefs.get("HOMEDIR", "").isEmpty()) {
	    prefs.put("HOMEDIR", oldPrefs.get("HOMEDIR", ""));
	}
	// MODFOLDER (where user last loaded mod from)
	if (!oldPrefs.get("MODFOLDER", "").isEmpty() && prefs.get("MODFOLDER", "").isEmpty()) {
	    prefs.put("MODFOLDER", oldPrefs.get("MODFOLDER", ""));
	}
	// current weapon prefs
	for (int i = 0; i < KEY_AMT; i++) {
	    if (!oldPrefs.get(intToKey(i), "").isEmpty() && prefs.get(intToKey(i), "").isEmpty()) {
		prefs.put(intToKey(i), oldPrefs.get(intToKey(i), ""));
	    }
	}
	// update prefs from the old term (rifle) to new term (carbine)
	if (!oldPrefs.get("rifle", "").isEmpty() && prefs.get("carbine", "").isEmpty()) {
	    prefs.put("carbine", oldPrefs.get("rifle", ""));
	}
    }

    private String getLastVersion() {
	String[] oldVersions = { "MainJFrame" }; // list the prefs objects of all old versions here,
	// add new version to front of array (position 0)
	// ("MainJFrame" = 1104)
	for (int i = 0; i < oldVersions.length; i++) {
	    Preferences oldPrefs = Preferences.userRoot().node(oldVersions[i]);
	    try {
		if (oldPrefs.keys().length > 0) {
		    return oldVersions[i];
		}
	    } catch (BackingStoreException ex) {
		l.log(ex);
	    }
	}
	return "";
    }
}
