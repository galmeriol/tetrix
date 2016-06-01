package capture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static void copyDirectory(File sourceDir, File destDir, boolean beachild) throws IOException {

	if(!destDir.exists()) {
	    destDir.mkdir();
	}
	File newP = sourceDir;

	if(beachild){
	    newP = new File(destDir.getAbsoluteFile() + "/" + sourceDir.getName());
	    if(!newP.exists())
		newP.mkdir();
	    else{
		int file_counter = 1;
		while((newP = new File(destDir.getAbsoluteFile() + "/action_" + file_counter++)).exists());
		newP.mkdir();
	    }
	}



	File[] children = sourceDir.listFiles();

	for(File sourceChild : children) {
	    String name = sourceChild.getName();
	    File destChild = new File(newP, name);
	    if(sourceChild.isDirectory()) {
		copyDirectory(sourceChild, destChild, false);
	    }
	    else {
		copyFile(sourceChild, destChild);
	    }
	}	
    }
    public static void copyFile(File source, File dest) throws IOException {

	if(!dest.exists()) {
	    dest.createNewFile();
	}
	InputStream in = null;
	OutputStream out = null;
	try {
	    in = new FileInputStream(source);
	    out = new FileOutputStream(dest);

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	}
	finally {
	    in.close();
	    out.close();
	}

    }

    public static boolean deleteDirectory(File directory) {
	if(directory.exists()){
	    File[] files = directory.listFiles();
	    if(null!=files){
		for(int i=0; i<files.length; i++) {
		    if(files[i].isDirectory()) {
			deleteDirectory(files[i]);
		    }
		    else {
			files[i].delete();
		    }
		}
	    }
	}
	return(directory.delete());
    }
}
