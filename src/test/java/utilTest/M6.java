package utilTest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.UIManager;

public class M6 {
	public static void main(String[] args) {
		Object[] list = (Object[]) UIManager.getDefaults().entrySet().toArray();
		try {
		  FileWriter fw = new FileWriter(new File("d:/UIManagerDefaults.txt"));
		  BufferedWriter bw = new BufferedWriter(fw);
		  for(Object o:list){
		    bw.write(o.toString());
		    bw.newLine();
		  }
		  bw.flush();
		  bw.close();
		} catch (IOException e1) {
		  e1.printStackTrace();
		}
	}
}
