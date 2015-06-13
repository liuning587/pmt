package utilTest;

import com.sx.mmt.internal.protocol.Afn;
import com.sx.mmt.internal.protocol.ProtocolAttribute;
import com.sx.mmt.internal.protocolBreakers.DecodedPacket;
import com.sx.mmt.internal.protocolBreakers.GBDataPacketBuilder;
import com.sx.mmt.internal.protocolBreakers.GBDecodedPacketFactory;

public class M2 {
	public static void main(String[] args) throws Exception {
		GBDataPacketBuilder gBDataPacketBuilder=new GBDataPacketBuilder();
		DecodedPacket dp=new GBDecodedPacketFactory().getDefaultGBPacketFromStation();
		dp.put(ProtocolAttribute.DATAUNITIDENTIFY_PN, 0);
		dp.put(ProtocolAttribute.DATAUNITIDENTIFY_FN, 1);
		dp.put(ProtocolAttribute.AFN_FUNCTION, Afn.AFN_FILE_TRANSFER_FOR_UPDATE);
//		gBDataPacketBuilder.build(dp);
	}
}
