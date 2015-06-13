package com.sx.mmt.internal.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileTool {
    public static String readFile(File file,String charset) {  
        StringBuilder resultStr = new StringBuilder();  
        try {
        	InputStreamReader ir=new InputStreamReader(new FileInputStream(file), charset);
        	BufferedReader bReader = new BufferedReader(ir);  
            String line = bReader.readLine();  
            while (line != null) {  
                resultStr.append(line).append("\r\n");  
                line = bReader.readLine();  
            }  
            bReader.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return resultStr.toString();  
    }
    
    public static List<String> readFileToList(File file){
        List<String> list=new ArrayList<String>(); 
        try {  
            BufferedReader bReader = new BufferedReader(new FileReader(file));  
            String line = bReader.readLine();  
            while (line != null) {  
            	list.add(line); 
                line = bReader.readLine();  
            }  
            bReader.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return list;  
    }
  
    public static void writeFile(File file, String str) {  
        try {  
          BufferedWriter bWriter = new BufferedWriter(new FileWriter(file));  
          bWriter.write(str);  
          bWriter.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    
    public static String getFileMD5String(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);  
        FileChannel fch = fis.getChannel();  
        MappedByteBuffer byteBuffer = fch.map(FileChannel.MapMode.READ_ONLY, 0,  
                file.length());
        MessageDigest messagedigest = MessageDigest.getInstance("MD5");
        messagedigest.update(byteBuffer);  
        String fileMD5=bufferToHex(messagedigest.digest());
        fis.close();
        return fileMD5;
    }
    
    private static String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    }
    
    private static String bufferToHex(byte bytes[], int m, int n) {  
        StringBuffer stringbuffer = new StringBuffer(2 * n);  
        int k = m + n;  
        for (int l = m; l < k; l++) {  
            appendHexPair(bytes[l], stringbuffer);  
        }  
        return stringbuffer.toString();  
    }
    
    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
    	char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6',  
                '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char c0 = hexDigits[(bt & 0xf0) >> 4];  
        char c1 = hexDigits[bt & 0xf];  
        stringbuffer.append(c0);  
        stringbuffer.append(c1);  
    } 
}
