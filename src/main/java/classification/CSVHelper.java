package classification;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVWriter;

public class CSVHelper {
    CSVWriter writer = null;
    
    public CSVWriter getWriter() {	
        return writer;
    }

    public void setWriter(String file) throws IOException {
        this.writer = new CSVWriter(new FileWriter(file, true));;
    }

    String file;
    
    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public CSVHelper(String file) throws IOException {
	setWriter(file);
    }
    
    public void write(List<String[]> lines) throws IOException{
	writer.writeAll(lines);
	writer.close();
    }
    
}
