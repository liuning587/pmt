package com.sx.mmt.internal.task.command;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.FileTool;
import com.sx.mmt.internal.util.SimpleBytes;
/**
 * 文件类，负责从磁盘中加载，切割文件，并保存在内存中
 * @author 王瑜甲
 *
 */

public final class FileManager {
	private static Logger logger = LoggerFactory.getLogger(FileManager.class);
	private static Map<String,FileManager> cache=new ConcurrentHashMap<String, FileManager>();
	private boolean isExist=true;
	private String fileName;
	private int fileType;
	private String version;
	private String fileMD5;
	private int length=0;
	private int compressedLength=0;
	private boolean isCompressed=false;
	private boolean isReboot=false;
	private int rebootWaitTime=5;
	private int totalSegment=0;
	private int segmentDataLength=0;
	private List<SimpleBytes> dataSegments=new ArrayList<SimpleBytes>();
	public static FileManager getFile(Map<String,String> config){
		FileManager file=cache.get(config.get(ConfigConstants.FilePath));
		if(file==null){
			FileManager newfile=new FileManager();
			newfile.loadFile(config);
			if(newfile.isExist){
				cache.put(config.get(ConfigConstants.FilePath), newfile);
				return newfile;
			}else{
				return null;
			}
		}else{
			return file;
		}
	}
	
	public void loadFile(Map<String,String> config){
		String filepath=config.get(ConfigConstants.FilePath);
		File file=new File(filepath);
		if(file.exists() && file.length()<Integer.MAX_VALUE){
			this.fileName=file.getName();
			this.fileType=Integer.parseInt(config.get(ConfigConstants.FileType));
			this.version=config.get(ConfigConstants.Version);
			this.isCompressed=Boolean.parseBoolean(config.get(ConfigConstants.IsCompressed));
			this.isReboot=Boolean.parseBoolean(config.get(ConfigConstants.IsReboot));
			this.rebootWaitTime=Integer.parseInt(config.get(ConfigConstants.RebootWaitTime));
			this.segmentDataLength=Integer.parseInt(config.get(ConfigConstants.SegmentLength));
			this.length=(int) file.length();
			FileInputStream fis=null;
			int lastLength=0;
			try{
				if(isCompressed){
					File zipfile=getZipFile(file);
//					this.fileName=zipfile.getName();
					fis=new FileInputStream(zipfile);
					compressedLength=(int) zipfile.length();
					this.fileMD5=FileTool.getFileMD5String(zipfile);
					lastLength=compressedLength%segmentDataLength;
				}else{
					fis=new FileInputStream(filepath);
					this.fileMD5=FileTool.getFileMD5String(file);
					compressedLength=length;
					lastLength=length%segmentDataLength;
				}
				byte buffer[]=new byte[segmentDataLength];
				while(fis.read(buffer)!=-1){
					SimpleBytes slice =new SimpleBytes(Arrays.copyOf(buffer, buffer.length));
					dataSegments.add(slice);
		        }
				fis.close();
				totalSegment=dataSegments.size();
				//处理最后一段
				if(lastLength>0){
					SimpleBytes last=dataSegments.get(totalSegment-1).poll(lastLength);
					dataSegments.remove(totalSegment-1);
					dataSegments.add(last);
				}
			}catch(Exception e){
				isExist=false;
				logger.error(ErrorTool.getErrorInfoFromException(e));
			}
			
			
		}else{
			isExist=false;
		}
		
	}
	
	public File getZipFile(File file) throws Exception{
		File filezip=new File(file.getAbsolutePath()+".Z");
		if((!filezip.isFile()) || (0==filezip.length())){
			File zlib=new File(System.getProperty("user.dir")+"/lib/zlib.exe");
			if (!zlib.isFile()) {
				throw new Exception("找不到zlib.exe文件");
			}else{
				Process pro=Runtime.getRuntime()
						.exec(zlib.getAbsolutePath()+" d "+file.getAbsolutePath());
				pro.waitFor();
			}
			if((!filezip.isFile()) || (0==filezip.length())){
				throw new Exception("生成压缩文件失败");
			}else{
				return filezip;
			}
		}else{
			return filezip;
		}
	}
	
	
	public String getFileName() {
		return fileName;
	}
	public int getFileType() {
		return fileType;
	}
	public String getVersion() {
		return version;
	}
	public String getFileMD5() {
		return fileMD5;
	}
	public int getLength() {
		return length;
	}
	public int getCompressedLength() {
		return compressedLength;
	}
	public boolean isCompressed() {
		return isCompressed;
	}
	public boolean isReboot() {
		return isReboot;
	}
	
	public int getRebootWaitTime() {
		return rebootWaitTime;
	}
	public int getTotalSegment() {
		return totalSegment;
	}
	public int getSegmentDataLength() {
		return segmentDataLength;
	}
	public List<SimpleBytes> getDataSegments() {
		return dataSegments;
	}
	public boolean isExist() {
		return isExist;
	}
	public void setExist(boolean isExist) {
		this.isExist = isExist;
	}
	
}
