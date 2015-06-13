package com.sx.mmt.internal.protocol.afn;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.bidimap.DualHashBidiMap;

import com.google.common.collect.Lists;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.DataBody;
import com.sx.mmt.internal.util.SimpleBytes;

public class AFN00Hp0f3 extends DataBody{
	public static final String NAME=AFN00H.CONFIRM_ONE_BY_ONE;
	private String confirmAfn;
	private Map<String,String> confirmDetail;
	public static final String CONFIRM="Confirm";
	public static final String AlreadyInUpdate="正在升级，不得重复启动";
	public static final String PasswordAuthWrong="密码权限不足";
	public static final String NotEnoughDiskSapce="磁盘空间不足";
	public static final String FileNameWrong="文件名无效";
	public static final String FileTypeIllegal="文件类型无效";
	public static final String FileLengthWrong="文件长度不对";
	public static final String FrameLengthWrong="帧长度错误";
	public static final String FrameTotalWrong="帧总数错误";
	public static final String FrameTotalFileLengthWrong="帧总数和文件长度不符";
	public static final String Md5CodeWrong="Md5码非法";
	public static final String VersionWrong="版本号非法";
	public static final String ReadFileFailed="读文件出错";
	public static final String CheckMD5Wrong="检查MD5出错";
	public static final String UnzipWrong="解压缩出错";
	public static final String FileIllegal="文件非法";
	public static final String UserCancel="用户取消";
	public static final String WriteFileFail="写文件出错";
	public static final String ParameterFileMissing="升级参数文件丢失";
	public static final String UpdateFrameIndexIllegal="升级报文帧号非法";
	public static final String ReadFlashFailed="读取flash失败";

	
	public static DualHashBidiMap dic=new DualHashBidiMap();
	static{
		dic.put(0, CONFIRM);
		dic.put(1, AlreadyInUpdate);
		dic.put(2, PasswordAuthWrong);
		dic.put(3, NotEnoughDiskSapce);
		dic.put(4, FileNameWrong);
		dic.put(5, FileTypeIllegal);
		dic.put(6, FileLengthWrong);
		dic.put(7, FrameLengthWrong);
		dic.put(8, FrameTotalWrong);
		dic.put(9, FrameTotalFileLengthWrong);
		dic.put(10, Md5CodeWrong);
		dic.put(11, VersionWrong);
		dic.put(12, ReadFileFailed);
		dic.put(13, CheckMD5Wrong);
		dic.put(14, UnzipWrong);
		dic.put(15, FileIllegal);
		dic.put(16, UserCancel);
		dic.put(17, WriteFileFail);
		dic.put(18, ParameterFileMissing);
		dic.put(19, UpdateFrameIndexIllegal);
		dic.put(20, ReadFlashFailed);
	}
	@Override
	public PacketSegment decode(String protocolType,String protocolArea){
		confirmDetail=new HashMap<String,String>();
		confirmAfn=Afn.getAfnSymbol(rawValue.getAt(0).toInt());
		int seg=(rawValue.getLength()-1)/5;
		for(int i=0;i<seg;i++){
			String pnfn=getPnfnString(rawValue.getSubByteArray(1+5*i, 5+5*i).getBytesArray());
			String errorCode=(String) dic.get(rawValue.getAt(5+5*i).toInt());
			if(errorCode==null){
				errorCode="未知错误";
			}
			confirmDetail.put(pnfn,errorCode);
		}
		return this;
	}
	
	@Override
	public PacketSegment encode(String protocolType,String protocolArea){
		rawValue.clear();
		SimpleBytes value=new SimpleBytes((byte)Afn.getAfnCode(confirmAfn));
		for(Entry<String,String> entry:confirmDetail.entrySet()){
			value.add(topnfn(entry.getKey()));
			value.add(((Integer)dic.getKey(entry.getValue())).byteValue());
		}
		rawValue.add(value);
		return this;
	}
	public String getConfirmAfn() {
		return confirmAfn;
	}
	public void setConfirmAfn(String confirmAfn) {
		this.confirmAfn = confirmAfn;
	}
	public Map<String, String> getConfirmDetail() {
		return confirmDetail;
	}
	public void setConfirmDetail(Map<String, String> confirmDetail) {
		this.confirmDetail = confirmDetail;
	}
	
	private String getPnfnString(byte[] bytes){

		if(bytes.length<4){
			return "";
		}
		StringBuilder sb=new StringBuilder();
		int p=0;
		int f=1;
		
		if(bytes[0]==0 && bytes[1]==0){
			sb.append("p0");
		}else{
			p=(bytes[1]-1)*8;
			int[] k=new SimpleBytes(bytes[0]).toBits();
			for(int i=0;i<8;i++){
				if(k[i]==1){
					p+=i+1;
				}
			}
			sb.append("p").append(p);
		}
		f=bytes[3]*8;
		int[] k1=new SimpleBytes(bytes[2]).toBits();
		for(int i=0;i<8;i++){
			if(k1[i]==1){
				f+=i+1;
			}
		}
		sb.append("f").append(f);
		return sb.toString();
	}
	
	private SimpleBytes topnfn(String pnfn){
		int pn=Integer.valueOf(pnfn.substring(pnfn.indexOf("p")+1, pnfn.indexOf("f")));
		int fn=Integer.valueOf(pnfn.substring(pnfn.indexOf("f")+1));
		byte b1;
		byte b2;
		if(pn==0){
			b1=0x0;
			b2=0x0;
		}else{
			b1=(byte)(1 << (pn-1) % 8);
			b2=(byte)((pn) / 8);
		}

		byte b3=(byte)(1 << (fn-1) % 8);
		byte b4=(byte)((fn-1) / 8);
		SimpleBytes sb=new SimpleBytes();
		sb.add(Lists.newArrayList(b1,b2,b3,b4));
		return sb;
	}
	
	@Override
	public void clear(){
		confirmAfn=null;
		confirmDetail=null;
		rawValue.clear();
	}
	
}
