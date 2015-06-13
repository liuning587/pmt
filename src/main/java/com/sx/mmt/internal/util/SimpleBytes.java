package com.sx.mmt.internal.util;

/**
 * byte操作增强类
 * 解规约神器
 * 
 * author：王瑜甲
 * 
 */

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


public class SimpleBytes implements Serializable{
	private static final long serialVersionUID = 3688664219930088617L;
	private byte[] simpleBytes;
	public SimpleBytes(){
		this.simpleBytes=new byte[0];
	}
	public SimpleBytes(byte b){
		this.simpleBytes=new byte[]{b};
	}
	/**
	 * byte列表构造器
	 * @param bytes
	 */
	public SimpleBytes(List<Byte> bytes){
		int size=bytes.size();
		simpleBytes=new byte[size];
		for(int i=0;i<size;i++){
			simpleBytes[i]=bytes.get(i);
		}
	}
	/**
	 * byte数组构造器
	 * @param bytes
	 */
	public SimpleBytes(byte[] bytes){
		this.simpleBytes=new byte[bytes.length];
		System.arraycopy(bytes, 0, this.simpleBytes, 0, bytes.length); 
	}
	
	/**
	 * 将二进制，或16进制字符串转化为byte数组
	 * @param s
	 * @param radix
	 */
	public SimpleBytes(String s,int radix){
		s=s.replace(" ", "").replace("-", "").trim();
		if(radix==2){
			int length=s.length();
			if(length%8!=0){
				throw new IllegalArgumentException("bin string length is not divisible by eight");
			}
			this.simpleBytes=new byte[length/8];
			for(int i=length-1;i>=0;i=i-8){
				this.simpleBytes[(length-1-i)/8]=Integer.valueOf(s.substring(i-7, i+1),2).byteValue();
			}
		}else if(radix==16){
			int length=s.length();
			if(length%2!=0){
				throw new IllegalArgumentException("hex string length is not even");
			}
			this.simpleBytes=new byte[length/2];
			for(int i=length-1;i>=0;i=i-2){
				this.simpleBytes[(length-1-i)/2]=Integer.valueOf(s.substring(i-1, i+1),16).byteValue();
			}
		}else{
			throw new IllegalArgumentException("only support 2 or 16");
		}
	}
	//同上一个方法
	public SimpleBytes(String s,int radix,boolean IsReverse){
		s=s.replace(" ", "").replace("-", "").trim();
		if(IsReverse){
			if(radix==2){
				int length=s.length();
				if(length%8!=0){
					throw new IllegalArgumentException("bin string length is not divisible by eight");
				}
				this.simpleBytes=new byte[length/8];
				for(int i=0;i<length;i=i+8){
					this.simpleBytes[(i)/8]=Integer.valueOf(s.substring(i, i+8),2).byteValue();
				}
			}else if(radix==16){
				int length=s.length();
				if(length%2!=0){
					throw new IllegalArgumentException("hex string length is not even");
				}
				this.simpleBytes=new byte[length/2];
				for(int i=0;i<length;i=i+2){
					this.simpleBytes[(i)/2]=Integer.valueOf(s.substring(i, i+2),16).byteValue();
				}
			}else{
				throw new IllegalArgumentException("only support 2 or 16");
			}
		}else{
			if(radix==2){
				int length=s.length();
				if(length%8!=0){
					throw new IllegalArgumentException("bin string length is not divisible by eight");
				}
				this.simpleBytes=new byte[length/8];
				for(int i=length-1;i>=0;i=i-8){
					this.simpleBytes[(length-1-i)/8]=Integer.valueOf(s.substring(i-7, i+1),2).byteValue();
				}
			}else if(radix==16){
				int length=s.length();
				if(length%2!=0){
					throw new IllegalArgumentException("hex string length is not even");
				}
				this.simpleBytes=new byte[length/2];
				for(int i=length-1;i>=0;i=i-2){
					this.simpleBytes[(length-1-i)/2]=Integer.valueOf(s.substring(i-1, i+1),16).byteValue();
				}
			}else{
				throw new IllegalArgumentException("only support 2 or 16");
			}
		}
	}

	/**
	 * 位操作低位在前高位在后,顺序填充
	 * @param bits
	 */
	public SimpleBytes(int[] bits){
		int size=bits.length;
		int left=size%8;
		int length=size/8;
		this.simpleBytes=new byte[length+(left==0?0:1)];
		for(int i=0;i<length;i++){
			this.simpleBytes[i]=0;
			for(int j=0;j<8;j++){
				this.simpleBytes[i]+=(byte) (bits[8*i+j]<<j);
			}
		}
		if(left!=0){
			this.simpleBytes[length]=0;
			for(int i=0;i<left;i++){
				this.simpleBytes[length]+=(byte) (bits[8*length+i]<<i);
			}
		}
	}
	/**
	 * 位联合构造器
	 */
	public SimpleBytes(int[] bits1,int[] bits2){
		int size1=bits1.length;
		int size2=bits2.length;
		int size=size1+size2;
		int[] bits=new int[size1+size2];
		System.arraycopy(bits1, 0, bits, 0, size1);
		System.arraycopy(bits2, 0, bits, size1,size2);
		int left=size%8;
		int length=size/8;
		this.simpleBytes=new byte[length+(left==0?0:1)];
		for(int i=0;i<length;i++){
			this.simpleBytes[i]=0;
			for(int j=0;j<8;j++){
				this.simpleBytes[i]+=(byte) (bits[8*i+j]<<j);
			}
		}
		if(left!=0){
			this.simpleBytes[length]=0;
			for(int i=0;i<left;i++){
				this.simpleBytes[length]+=(byte) (bits[8*length+i]<<i);
			}
		}
	}
	/**
	 * short构造器
	 * @param s
	 */
	public SimpleBytes(short s){
		this.simpleBytes=new byte[2];
		for(int j=0;j<2;j++){
			this.simpleBytes[j]=(byte) ((s>>8*j) & 0xff);
		}
	}
	
	/**
	 * int构造器
	 * @param s
	 */
	public SimpleBytes(int i){
		this.simpleBytes=new byte[4];
		for(int j=0;j<4;j++){
			this.simpleBytes[j]=(byte) ((i>>8*j) & 0xff);
		}
	}
	
	/**
	 * long构造器
	 * @param s
	 */
	public SimpleBytes(long l){
		this.simpleBytes=new byte[8];
		for(int j=0;j<8;j++){
			this.simpleBytes[j]=(byte) ((l>>8*j) & 0xff);
		}
	}
	
	/**
	 * 字符串输入构造器
	 * @param s
	 * @param fixlength
	 * @param charset
	 */
	public SimpleBytes(String s,int fixlength,String charset){
		this.simpleBytes=new byte[fixlength];
		try {
			byte[] string = s.getBytes(charset);
			if(s.length()>fixlength){
				throw new IllegalArgumentException(
						String.format("String was too long '''%s''' for fix length is %s" ,s,fixlength));
			}else{
				System.arraycopy(string, 0, this.simpleBytes, 0, string.length); 
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(
					String.format("charset was not support '''%s'''",charset));
		}	
	}
	
	/**
	 * 布尔构造器
	 * @param b
	 */
	public SimpleBytes(boolean b){
		this.simpleBytes=new byte[1];
		this.simpleBytes[0]=(byte) (b?1:0);
	}
	
	/**
	 * byte数组转换为16进制字符串，高位在前低位在后
	 * @return
	 */
	public String toHexString(String spilt){		
		int length=getLength();
		StringBuilder sb=new StringBuilder();
		for(int i=length-1;i>=0;i--){
			String hex=StringUtils.leftPad(Integer.toHexString(this.simpleBytes[i]), 2, '0');
			sb.append(hex.substring(hex.length()-2, hex.length()).toUpperCase()).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * byte数组转换为16进制字符串，低位在前高位在后
	 * @return
	 */
	public String toReverseHexString(String spilt){
		StringBuilder sb=new StringBuilder();
		for(byte b:this.simpleBytes){
			String hex=StringUtils.leftPad(Integer.toHexString(b), 2, '0');
			sb.append(hex.substring(hex.length()-2, hex.length()).toUpperCase()).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * 子16进制字符串，从高位到低位截取
	 * @param begin
	 * @param end
	 * @return
	 */
	public String toSubHexString(int begin,int end,String spilt){
		int length=getLength();
		if(end>length || begin<0 || begin>end){
			throw new IllegalArgumentException(
					String.format("substring is not exist for begin=%s and end=%s.total length is %s",
							begin,end,length));
		}
		StringBuilder sb=new StringBuilder();
		for(int i=length-1-begin;i>=length-end;i--){
			String hex=StringUtils.leftPad(Integer.toHexString(this.simpleBytes[i]), 2, '0');
			sb.append(hex.substring(hex.length()-2, hex.length()).toUpperCase()).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * 子16进制字符串，从低位到高位截取
	 * @param begin
	 * @param end
	 * @return
	 */
	public String toSubReverseHexString(int begin,int end,String spilt){
		int length=getLength();
		if(end>length || begin<0 || begin>end){
			throw new IllegalArgumentException(
					String.format("substring is not exist for begin=%s and end=%s.total length is %s",
							begin,end,length));
		}
		StringBuilder sb=new StringBuilder();
		for(int i=begin;i<end;i++){
			String hex=StringUtils.leftPad(Integer.toHexString(this.simpleBytes[i]), 2, '0');
			sb.append(hex.substring(hex.length()-2, hex.length()).toUpperCase()).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * 输出到二进制字符串
	 * @return
	 */
	
	public String toBinString(String spilt){
		StringBuilder sb=new StringBuilder();
		int length=getLength();
		int Unassignedbyte=0;
		for(int i=length-1;i>=0;i--){
			Unassignedbyte=this.simpleBytes[i] & 0xff;
			sb.append(StringUtils.leftPad(Integer.toBinaryString(Unassignedbyte),8,'0')).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * 输出到二进制字符串,低位在前
	 * @return
	 */
	public String toReverseBinString(String spilt){
		StringBuilder sb=new StringBuilder();
		int length=getLength();
		int Unassignedbyte=0;
		for(int i=0;i<length;i++){
			Unassignedbyte=this.simpleBytes[i] & 0xff;
			sb.append(StringUtils.leftPad(Integer.toBinaryString(Unassignedbyte),8,'0')).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * 输出到反转子二进制字符串
	 * @param begin
	 * @param end
	 * @return
	 */
	public String toSubReverseBinString(int begin,int end,String spilt){
		int length=getLength();
		if(end>length || begin<0 || begin>end){
			throw new IllegalArgumentException(
					String.format("substring is not exist for begin=%s and end=%s.total length is %s",
							begin,end,length));
		}
		StringBuilder sb=new StringBuilder();
		int Unassignedbyte=0;
		for(int i=begin;i<end;i++){
			Unassignedbyte=this.simpleBytes[i] & 0xff;
			sb.append(StringUtils.leftPad(Integer.toBinaryString(Unassignedbyte),8,'0')).append(spilt);
		}
		return sb.toString();
	}
	/**
	 * 输出到子二进制字符串
	 * @param begin
	 * @param end
	 * @return
	 */
	public String toSubBinString(int begin,int end,String spilt){
		int length=getLength();
		if(end>length || begin<0 || begin>end){
			throw new IllegalArgumentException(
					String.format("substring is not exist for begin=%s and end=%s.total length is %s",
							begin,end,length));
		}
		StringBuilder sb=new StringBuilder();
		int Unassignedbyte=0;
		for(int i=length-1-begin;i>=length-end;i--){
			Unassignedbyte=this.simpleBytes[i] & 0xff;
			sb.append(StringUtils.leftPad(Integer.toBinaryString(Unassignedbyte),8,'0')).append(spilt);
		}
		return sb.toString();
	}
	
	/**
	 * 取中某个字节
	 * @param index
	 * @return
	 */
	public SimpleBytes getAt(int index){
		if(index>getLength()-1 || index<0){
			throw new IllegalArgumentException(String.
					format("index out of boundary ,index is %s ,length is %s", index,getLength()));
		}else{
			byte[] by=new byte[]{this.simpleBytes[index]};
			return new SimpleBytes(by);
		}
	}
	
	/**
	 * 对list进行切片，支持小于零切片
	 * @param indexBegin
	 * @param indexEnd
	 * @return
	 */
	
	public SimpleBytes getSubByteArray(int indexBegin,int indexEnd){
		int size=getLength();
		if(indexBegin>=0 && indexEnd>0){
			return getSubGreaterThanZero(indexBegin,indexEnd);
		}
		else if(indexBegin<0 && indexEnd>0){
			return getSubGreaterThanZero(indexBegin+size,indexEnd);
		}else if(indexBegin>=0 && indexEnd<=0){
			return getSubGreaterThanZero(indexBegin,size+indexEnd);
		}else{
			return getSubGreaterThanZero(size+indexBegin, size+indexEnd);
		}
	}
	/**
	 * 接上个方法
	 * @param indexBegin
	 * @param indexEnd
	 * @return
	 */
	private SimpleBytes getSubGreaterThanZero(int indexBegin,int indexEnd){
		int size=getLength();
		if(size==0) return new SimpleBytes();
		if(indexBegin>=size){
			return new SimpleBytes();
		}else if(indexEnd>size){
			byte[] newBytes=new byte[size-indexBegin];
			System.arraycopy(simpleBytes, indexBegin, newBytes, 0, size-indexBegin);
			return new SimpleBytes(newBytes);
		}else{
			byte[] newBytes=new byte[indexEnd-indexBegin];
			System.arraycopy(simpleBytes, indexBegin, newBytes, 0, indexEnd-indexBegin);
			return new SimpleBytes(newBytes);
		}
	}
	
	/**
	 * 取得切片后的长度
	 * @param indexBegin
	 * @param indexEnd
	 * @return
	 */
	
	public int getSubByteArraySize(int indexBegin,int indexEnd){
		return getSubByteArray(indexBegin,indexEnd).getLength();
	}
	
	
	/**
	 * 输出到位数组
	 * @return
	 */
	public int[] toBits(){
		int length=getLength();
		int[] i=new int[length*8];
		for(int j=0;j<length;j++){
			i[7+j*8]=(this.simpleBytes[j] & 0x80)>>7;
			i[6+j*8]=(this.simpleBytes[j] & 0x40)>>6;
			i[5+j*8]=(this.simpleBytes[j] & 0x20)>>5;
			i[4+j*8]=(this.simpleBytes[j] & 0x10)>>4;
			i[3+j*8]=(this.simpleBytes[j] & 0x8)>>3;
			i[2+j*8]=(this.simpleBytes[j] & 0x4)>>2;
			i[1+j*8]=(this.simpleBytes[j] & 0x2)>>1;
			i[0+j*8]=(this.simpleBytes[j] & 0x1);
		}
		return i;
	}
	
	
	/**
	 * 输出字串到位数组
	 * @return
	 */
	public int[] toSubBits(int begin,int end){
		int[] i=toBits();
		if(begin>=end || begin<0 || end>i.length){
			throw new IllegalArgumentException(
					String.format("substring is not exist for begin=%s and end=%s.total length is %s",
							begin,end,i.length));
		}
		int[] j=new int[end-begin];
		System.arraycopy(i, begin, j, 0, end-begin);
		return j;
	}
	
	/**
	 * 得到位字串的值
	 * @param begin
	 * @param end
	 * @return
	 */
	public SimpleBytes getSubBitsValue(int begin,int end){
		return new SimpleBytes(toSubBits(begin,end));
	}
	
	/**
	 * 将低1字节输出为byte
	 * @return
	 */
	public byte toByte(){
		if(getLength()>=1){
			return this.simpleBytes[0];
		}else{
			return 0;
		}
		
	}
	
	/**
	 * 将低2字节输出为short
	 * @return
	 */
	public short toShort(){
		int length=getLength();
		short s=0;
		int Unassignedbyte=0;
		if(length<2){
			for(int i=0;i<length;i++){
				Unassignedbyte=0;
				s+=((Unassignedbyte+this.simpleBytes[i]) & 0xff)<<(8*i);
			}
		}else{
			for(int i=0;i<2;i++){
				Unassignedbyte=0;
				s+=((Unassignedbyte+this.simpleBytes[i]) & 0xff)<<(8*i);
			}
		}
		return s;
	}
	
	/**
	 * 将低4字节输出为int
	 * @return
	 */
	public int toInt(){
		int length=getLength();
		int k=0;
		int Unassignedbyte=0;
		if(length<4){
			for(int i=0;i<length;i++){
				Unassignedbyte=0;
				k+=((Unassignedbyte+this.simpleBytes[i]) & 0xff)<<(8*i);
			}
		}else{
			for(int i=0;i<4;i++){
				Unassignedbyte=0;
				k+=((Unassignedbyte+this.simpleBytes[i]) & 0xff)<<(8*i);
			}
		}
		return k;
		
	}
	
	/**
	 * 将低8字节输出为long
	 * @return
	 */
	public long toLong(){
		int length=getLength();
		long l=0;
		long Unassignedbyte=0;
		if(length<8){
			for(int i=0;i<length;i++){
				Unassignedbyte=0;
				l+=((Unassignedbyte+this.simpleBytes[i]) & 0xff)<<(8*i);
			}
		}else{
			for(int i=0;i<8;i++){
				Unassignedbyte=0;
				l+=((Unassignedbyte+this.simpleBytes[i]) & 0xff)<<(8*i);
			}
		}
		return l;
	}
	
	/**
	 * 转化为short数组
	 * @return
	 */
	public short[] toShortArray(){
		int left=getLength()%2;
		int length=getLength()/2;
		short[] s=new short[length+(left==0?0:1)];
		int Unassignedbyte=0;
		for(int i=0;i<length;i++){
			Unassignedbyte=0;
			for(int j=0;j<2;j++){
				s[i]+=(((Unassignedbyte+this.simpleBytes[2*i+j])) & 0xff)<<8*j;
			}
		}
		if(left!=0){
			Unassignedbyte=0;
			for(int i=0;i<left;i++){
				s[length]+=(((Unassignedbyte+this.simpleBytes[2*length+i])) & 0xff)<<8*i;
			}
		}
		return s;	
	}
	
	/**
	 * 转化为Int数组
	 * @return
	 */
	
	public int[] toIntArray(){
		int left=getLength()%4;
		int length=getLength()/4;
		int[] l=new int[length+(left==0?0:1)];
		int Unassignedbyte=0;
		for(int i=0;i<length;i++){
			Unassignedbyte=0;
			for(int j=0;j<4;j++){
				l[i]+=((Unassignedbyte+this.simpleBytes[4*i+j]) & 0xff)<<8*j;
			}
		}
		if(left!=0){
			Unassignedbyte=0;
			for(int i=0;i<left;i++){
				l[length]+=((Unassignedbyte+this.simpleBytes[4*length+i]) & 0xff)<<8*i;
			}
		}
		return l;	
	}
	
	/**
	 * 转化为long数组
	 */
	
	public long[] toLongArray(){
		int left=getLength()%8;
		int length=getLength()/8;
		long[] l=new long[length+(left==0?0:1)];
		int Unassignedbyte=0;
		for(int i=0;i<length;i++){
			Unassignedbyte=0;
			for(int j=0;j<8;j++){
				l[i]+=((Unassignedbyte+this.simpleBytes[8*i+j]) & 0xff)<<8*j;
			}
		}
		if(left!=0){
			Unassignedbyte=0;
			for(int i=0;i<left;i++){
				l[length]+=((Unassignedbyte+this.simpleBytes[8*length+i]) & 0xff)<<8*i;
			}
		}
		return l;
	}
	
	/**
	 * 插入bits
	 * @param bits
	 * @param location
	 * @return
	 */
	
	public SimpleBytes insertBits(int[] bits,int location){
		int[] bts =toBits();
		int[] a3=new int[bts.length+bits.length];
		System.arraycopy(bts, 0, a3, 0, location);
		System.arraycopy(bits, 0, a3, location, bits.length);
		System.arraycopy(bts, 0, a3, location+bits.length, bts.length-location);
		return new SimpleBytes(a3);
	}
	
	/**
	 * 覆盖部分bits
	 * @param bits
	 * @param location
	 * @return
	 */
	public SimpleBytes replaceBits(int[] bits,int location){
		int[] bts =toBits();
		int length=0;
		if(bts.length-location>bits.length){
			length=bts.length;
		}else{
			length=location+bits.length;
		}
		int[] newbits=new int[length];
		System.arraycopy(bts, 0, newbits, 0, location);
		System.arraycopy(bits, 0, newbits, location, bits.length);
		if(location+bits.length<bts.length){
			System.arraycopy(bts, 0, newbits, location+bits.length,length-location-bits.length);
		}
		return new SimpleBytes(newbits);
	}
	
	/**
	 * 添加一个byte
	 * @param b
	 * @return
	 */
	public SimpleBytes add(byte b){
		byte[] newBytes=new byte[]{b};
		add(newBytes);
		return this;
	}
	
	/**
	 * 添加一个boolean
	 * @param b
	 * @return
	 */
	public SimpleBytes add(boolean b){
		byte[] newBytes=new byte[1];
		newBytes[0]=(byte) (b?1:0);
		add(newBytes);
		return this;
	}
	
	/**
	 * 添加一个short
	 * @param l
	 * @return
	 */
	public SimpleBytes add(short s){
		byte[] newBytes=new byte[2];
		for(int j=0;j<2;j++){
			newBytes[j]=(byte) ((s>>8*j) & 0xff);
		}
		add(newBytes);
		return this;
	}
	
	
	/**
	 * 添加一个int
	 * @param l
	 * @return
	 */
	public SimpleBytes add(int i){
		byte[] newBytes=new byte[4];
		for(int j=0;j<4;j++){
			newBytes[j]=(byte) ((i>>8*j) & 0xff);
		}
		add(newBytes);
		return this;
	}
	
	
	/**
	 * 添加一个long
	 * @param l
	 * @return
	 */
	public SimpleBytes add(long l){
		byte[] newBytes=new byte[8];
		for(int j=0;j<8;j++){
			newBytes[j]=(byte) ((l>>8*j) & 0xff);
		}
		add(newBytes);
		return this;
	}
	
	
	/**
	 * 数组合并
	 * @param bytes
	 * @return
	 */
	public SimpleBytes add(SimpleBytes sb){
		byte[] newBytes=sb.getBytesArray();
		return add(newBytes);
	}
	
	/**
	 * 数组合并
	 * @param bytes
	 * @return
	 */
	public SimpleBytes add(byte[] bytes){
		int oldLength=getLength();
		int addLength=bytes.length;
		byte[] newBytes=new byte[oldLength+addLength];
		System.arraycopy(this.simpleBytes, 0, newBytes, 0, oldLength); 
		System.arraycopy(bytes, 0, newBytes, oldLength, addLength);
		this.simpleBytes=newBytes;
		return this;
		
	}
	
	/**
	 * 数组合并
	 * @param bytes
	 * @return
	 */
	public SimpleBytes add(List<Byte> bytes){
		int oldLength=getLength();
		int addLength=bytes.size();
		byte[] newBytes=new byte[oldLength+addLength];
		System.arraycopy(this.simpleBytes, 0, newBytes, 0, oldLength); 
		for(int i=oldLength;i<oldLength+addLength;i++){
			newBytes[i]=bytes.get(i-oldLength);
		}
		this.simpleBytes=newBytes;
		return this;
	}
	
	/**
	 * 批量添加
	 * @param bytes
	 * @return
	 */
	public SimpleBytes addAll(SimpleBytes... bytes ){
		for(SimpleBytes s:bytes){
			this.add(s);
		}
		return this;
	}
	
	/**
	 * 获得一字节的校验和
	 * @return
	 */
	public byte getCheckSum(){
		byte checksum=0;
		for(byte b:this.simpleBytes){
			checksum+=b;
		}
		return checksum;
	}
	
	
	/**
	 * 获得两字节或4字节的校验和
	 * @return
	 */
	public byte[] getCheckSum(int ByteNumber){
		int Unassignedbyte=0;
		if(ByteNumber==2){
			short checksum=0;
			for(byte b:this.simpleBytes){
				checksum+=(Unassignedbyte+b) & 0xff;
			}
			byte[] bs=new byte[2];
			for(int j=0;j<2;j++){
				bs[j]=(byte) ((checksum>>8*j) & 0xff);
			}
			return bs;
		}else if(ByteNumber==4){
			int checksum=0;
			for(byte b:this.simpleBytes){
				checksum+=(Unassignedbyte+b) & 0xff;;
			}
			byte[] bs=new byte[4];
			for(int j=0;j<4;j++){
				bs[j]=(byte) ((checksum>>8*j) & 0xff);
			}
			return bs;
		}else{
			throw new IllegalArgumentException("not support checkSum length");
		}
	}
	
	/**
	 * 从头部截取指定长度的byte
	 */
	public SimpleBytes poll(int number){
		if(simpleBytes.length<number){
			byte[] poll=new byte[simpleBytes.length];
			System.arraycopy(simpleBytes, 0, poll, 0, simpleBytes.length);
			simpleBytes=new byte[0];
			return new SimpleBytes(poll);
		}else{
			byte[] poll=new byte[number];
			System.arraycopy(simpleBytes, 0, poll, 0, number);
			int newLength=simpleBytes.length-number;
			byte[] left=new byte[newLength];		
			System.arraycopy(simpleBytes, number, left, 0, newLength);
			simpleBytes=left;
			return new SimpleBytes(poll);
		}
	}
	
	public SimpleBytes pollRear(int number){
		if(simpleBytes.length<number){
			byte[] poll=new byte[simpleBytes.length];
			System.arraycopy(simpleBytes, 0, poll, 0, simpleBytes.length);
			simpleBytes=new byte[0];
			return new SimpleBytes(poll);
		}else{
			byte[] poll=new byte[number];
			System.arraycopy(simpleBytes, simpleBytes.length-number, poll, 0, number);
			int newLength=simpleBytes.length-number;
			byte[] left=new byte[newLength];		
			System.arraycopy(simpleBytes, 0, left, 0, newLength);
			simpleBytes=left;
			return new SimpleBytes(poll);
		}
	}
	
	public String toAscii(){
		int length = 0;
		for (int i = 0; i < simpleBytes.length; i++) {
			if (simpleBytes[i] != 0x00) {
				length += 1;
			}
		}

		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = (char) simpleBytes[i];
		}

		return String.valueOf(chars);
	}
	
	public String toAscii(int start,int end){
		byte[] data=new byte[end-start];
		System.arraycopy(simpleBytes, start, data, 0, data.length);
		int length = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != 0x00) {
				length += 1;
			}
		}

		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = (char) data[i];
		}

		return String.valueOf(chars);
	}
	
	public int toBCD(){
		int a1=(new SimpleBytes(this.toSubBits(0, 4))).toInt();
		int a2=(new SimpleBytes(this.toSubBits(4, 8))).toInt();
		return a1+a2*10;
	}
	
	public void clear(){
		simpleBytes=new byte[0];
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(simpleBytes);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass()){
			return false;
		}else{
			SimpleBytes other = (SimpleBytes) obj;
			int[] a1=this.toBits();
			int[] a2=other.toBits();
			if(a1.length!=a2.length){
				return false;
			}
			int length=a1.length;
			for(int i=0;i<length;i++){
				if(a1[i]!=a2[i]){
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	@Override
	public String toString() {
		return toReverseHexString(" ");
	}
	
	public int getLength(){
		return this.simpleBytes.length;
	}
	
	public byte[] getBytesArray() {
		return simpleBytes;
	}
	
	
	
}
