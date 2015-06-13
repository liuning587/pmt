package com.sx.mmt.internal.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVUtil {
	private static Logger logger = LoggerFactory.getLogger(CSVUtil.class); 
	/**
     * 导出
     * 
     * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList){
        boolean isSucess=false;
        
        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(String data : dataList){
                    bw.append(data).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                	logger.error(ErrorTool.getErrorInfoFromException(e));
                } 
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                	logger.error(ErrorTool.getErrorInfoFromException(e));
                } 
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                	logger.error(ErrorTool.getErrorInfoFromException(e));
                } 
            }
        }
        
        return isSucess;
    }
    
    /**
     * 导入
     * 
     * @param file csv文件(路径+文件)
     * @return
     */
    public static List<String> importCsv(File file){
        List<String> dataList=new ArrayList<String>();
        
        BufferedReader br=null;
        try { 
            br = new BufferedReader(new FileReader(file));
            String line = ""; 
            while ((line = br.readLine()) != null) { 
                dataList.add(line);
            }
        }catch (Exception e) {
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                	logger.error(ErrorTool.getErrorInfoFromException(e));
                }
            }
        }
 
        return dataList;
    }
    
	public static List<List<String>> readCSVFile(File f){
		InputStreamReader fr=null;
		try {
			fr = new InputStreamReader(new FileInputStream(f));
		} catch (FileNotFoundException e1) {
			logger.error(ErrorTool.getErrorInfoFromException(e1));
		}
		BufferedReader br = new BufferedReader(fr);
		String rec = null;// 一行
		String str;// 一个单元格
		List<List<String>> listFile = new ArrayList<List<String>>();
		try {
			// 读取一行
			while ((rec = br.readLine()) != null) {
				Pattern pCells = Pattern
						.compile("(\"[^\"]*(\"{2})*[^\"]*\")*[^,]*,");
				Matcher mCells = pCells.matcher(rec);
				List<String> cells = new ArrayList<String>();// 每行记录一个list
				// 读取每个单元格
				while (mCells.find()) {
					str = mCells.group();
					str = str.replaceAll(
							"(?sm)\"?([^\"]*(\"{2})*[^\"]*)\"?.*,", "$1");
					str = str.replaceAll("(?sm)(\"(\"))", "$2");
					cells.add(str);
				}
				listFile.add(cells);
			}
		} catch (Exception e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					logger.error(ErrorTool.getErrorInfoFromException(e));
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(ErrorTool.getErrorInfoFromException(e));
				}
			}
		}
		return listFile;
	}
}
