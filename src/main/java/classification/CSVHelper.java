package classification;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class CSVHelper {
    CSVWriter writer = null;
    CSVReader reader = null;
    public CSVReader getReader() {
        return reader;
    }

    public void setReader(String filename) {
        try {
	    this.reader = new CSVReader(new FileReader(filename));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }

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
    public CSVHelper(){
	
    }
    public void write(List<String[]> lines) throws IOException{
	writer.writeAll(lines);
	writer.close();
    }
    
    public List<String[]> read() throws IOException{
	List<String[]> data = reader.readAll();
	
	return data;
    }
    
    public void writeToCSV(List<double[]> binsList) throws IOException{
	String[] line = null;
	List<String[]> lines = new ArrayList<String[]>();
	for(double[] bins:binsList){
	    line = new String[bins.length];
	    for (int i = 0; i < bins.length; i++) {
		line[i] = String.valueOf(bins[i]);
	    }
	    lines.add(line);
	}
	
	write(lines);
    }
    
    public List<double[]> readFromCSV() throws IOException{
	double[] line = null;
	List<double[]> lines = new ArrayList<double[]>();
	
	List<String[]> data = read();
	
	for(String[] bins:data){
	    line = new double[bins.length];
	    for (int i = 0; i < bins.length; i++) {
		line[i] = Double.valueOf(bins[i]);
	    }
	    lines.add(line);
	}
	
	return lines;
    }
    
    public static double[][] convert(List<String[]> toconvert){
	int FEATURE_SIZE = 7; 
	double[][] data = new double[toconvert.size()][];
	int row_ = 0;
	int col_ = 0;
	for(String[] row:toconvert){
	    data[row_] = new double[FEATURE_SIZE];
	    for(String col:row){
		data[row_][col_] = Double.valueOf(col);
		col_++;
	    }
	    col_ = 0;
	    row_++;
	}
	return data;
    }
    
}
