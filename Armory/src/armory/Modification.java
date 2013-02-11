package armory;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author Ethan "Lantry" Davidson
 */
public class Modification {

    private String Name; // name of mod
    // private String killmsg; //mod's kill message
    private ArrayList<String> Types; // types of mod
    private ArrayList<File> Files; // arraylist of filepaths

    public Modification(String name, ArrayList<String> types, ArrayList<File> files) {
        this.Name = name;
        // killmsg = msg;
        this.Types = new ArrayList<String>();
        this.Types.addAll(types);
        this.Files = new ArrayList<File>();
        this.Files.addAll(files);
    }

    // getset name
    @Override
    public String toString() {
        return Name;
    }

    public void setName(String s) {
        Name = s;
    }

    public boolean isNamed(String s) {
        return s.equalsIgnoreCase(Name);
    }

    // getset killmsg
	/*
     * public String getKillmsg() { return killmsg; }
     * 
     * public void setKillmsg(String a) { killmsg = a; }
     */
    // get types
    public ArrayList<String> getTypes() {
        return Types;
    }

    public int getTypeAmt() {
        return Types.size();
    }

    public String getType(int i) {
        return Types.get(i);
    }

    // get path(s)
    public String getPathStr(int i) {
        return Files.get(i).toString().replace("\\", "/");
    }

    public File getFile(int i) {
        return Files.get(i);
    }

    public void setPath(int i, String path) {
        Files.set(i, new File(path));
    }

    public int getPathAmt() {
        return Files.size();
    }
//	public void updateRifle() {
//		if (types.contains("rifle") || types.contains("carbine")) {
//			boolean needsRewrite = false;
//			for (int j = 0; j < getPathAmt(); j++) {
//				String old = getPath(j);
//				String fixed = cleanFilePath(getPath(j));
//				if (!old.equals(fixed)) {
//					new File(armory.armoryMain.homeDirectory + armory.armoryMain.modLibrary + name + old).renameTo(new File(homeDirectory + modLibrary
//							+ name + fixed));
//					setPath(j, fixed);
//					needsRewrite = true;
//				}
//			}
//			if (needsRewrite) {
//				armory.Logger.log("Corrected \"" + name + "\"");
//				rewriteRegistry();
//			}
//		}
//	}
}
