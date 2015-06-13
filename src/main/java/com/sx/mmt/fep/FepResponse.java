package com.sx.mmt.fep;

import com.sx.mmt.internal.protocol.ControlField;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.protocol.Seq;
import com.sx.mmt.internal.util.SimpleBytes;

public class FepResponse {
	public static SimpleBytes getComfirmPacket(SimpleBytes requestPacket,int fn){
		SimpleBytes response=new SimpleBytes();
		SimpleBytes body=new SimpleBytes();
		ControlField cf=new ControlField();
		cf.setDIR(ControlField.DIR_PACKET_FROM_STATION);
		cf.setPRM(ControlField.PRM_PACKET_FROM_SLAVE);
		cf.setFunctionCode(ControlField.PRM0_LINK_STATUS);
		cf.encode("","");
		Seq seq=new Seq();
		seq.setIsHaveTimeTag(false);
		seq.setIsFirstFrame(true);
		seq.setIsLastFrame(true);
		seq.setIsNeedConfirm(false);
		int[] i=requestPacket.getSubByteArray(13, 14).toBits();//Seq.INDEX_BEGIN, Seq.INDEX_END
		int serialNo= (new SimpleBytes(new int[]{i[0],i[1],i[2],i[3], 0,0,0,0})).toInt();
		seq.setSerialNo(serialNo);
		seq.encode("","");
		body.add(cf.getRawValue())
			.add(requestPacket.getSubByteArray(7, 12))//Address.INDEX_BEGIN, Address.INDEX_END
			.add((byte)0)
			.add(seq.getRawValue())
			.add((short)0)
			.add((short)4)//部分确认否认
			.add((byte)2)//功能码，链路测试
			.add((short)0);
		if(fn==1){
			body.add((short)1);//确认单元
		}else if(fn==2){
			body.add((short)2);//确认单元
		}else{
			body.add((short)4);//确认单元
		}
		body.add((byte)0);//确认
		Head head=new Head();
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		response.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return response;
	}
}