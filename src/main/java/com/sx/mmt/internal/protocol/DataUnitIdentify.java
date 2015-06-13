package com.sx.mmt.internal.protocol;

import com.google.common.collect.Lists;
import com.sx.mmt.internal.api.PacketSegment;
import com.sx.mmt.internal.util.SimpleBytes;

/**
 * 数据单元为按数据单元标识所组织的数据，包括参数、命令等。
 * 数据组织的顺序规则：先按pn从小到大、再按Fn从小到大的次序，
 * 即：完成一个信息点pi的所有信息类Fn的处理后，再进行下一个pi+1的处理。
 * 终端在响应主站对终端的参数或数据请求时，
 * 如终端没有所需的某个数据项，则将应答报文中DT的对应标志位清除；
 * 如终端仅是没有某个数据项中的部分内容，则应将该数据项中的所缺部分内容的每个字节填写“EEH”。
 * @author 王瑜甲
 *
 */

public class DataUnitIdentify extends PacketSegmentBase {
	public static final String NAME="DataUnitIdentify";
	
	//信息点DA pn（n=1～2040）——当DA1和DA2全为“0”时，表示终端信息点，用p0表示
	private int pn;
	//信息类DT Fn（n=1～248）
	private int fn;
		
	public DataUnitIdentify(){}
	public DataUnitIdentify(SimpleBytes b){
		this.rawValue=b;
	}
	
	@Override
	public PacketSegment decode(String protocolType,String protocolArea) {
		if(rawValue.toShortArray()[0]==0){
			pn=0;
		}else{
			pn=(rawValue.getAt(1).toInt()-1)*8;
			int[] k=rawValue.getAt(0).toBits();
			for(int i=0;i<8;i++){
				if(k[i]==1){
					pn+=i+1;
				}
			}
		}
		fn=rawValue.getAt(3).toInt()*8;
		int[] k1=rawValue.getAt(2).toBits();
		for(int i=0;i<8;i++){
			if(k1[i]==1){
				fn+=i+1;
			}
		}
		return this;
	}

	@Override
	public PacketSegment encode(String protocolType,String protocolArea) {
		rawValue.clear();
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
		rawValue.add(Lists.newArrayList(b1,b2,b3,b4));
		return this;
	}
	

	
	@Override
	public void clear(){
		pn=0;
		fn=0;
		rawValue.clear();
	}
	
	@Override
	public int getSegmentLength(String protocolType,String protocolArea){
		return 4;
	}

	public int getPn() {
		return pn;
	}
	public void setPn(int pn) {
		this.pn = pn;
	}
	public int getFn() {
		return fn;
	}
	public void setFn(int fn) {
		this.fn = fn;
	}

	@Override
	public boolean check() {

		return true;
	}
	
	
	
}
