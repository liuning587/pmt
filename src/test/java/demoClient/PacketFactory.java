package demoClient;

import com.sx.mmt.internal.protocol.Address;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.util.SimpleBytes;

public class PacketFactory {
	public static SimpleBytes getLoginPacket(){
		Address address=new Address();
		SimpleBytes dis=new SimpleBytes("2105",16);
		SimpleBytes add=new SimpleBytes("AAAA",16);
		address.setDistrict(dis.toReverseHexString(""));
		address.setTerminalAddress(add.toReverseHexString(""));
		address.setIsGroupAddress(false);
		address.setMsa(0);
		address.encode("","");
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address.getRawValue()).add((byte)2).add((byte)0x79).add((short)0).add((short)1);
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public static SimpleBytes getPacket(SimpleBytes address){
		SimpleBytes body=new SimpleBytes((byte)0x44);
		body.add(address).add((byte)0x13).add((byte)0x64).add((short)0).add((short)0x4);
		SimpleBytes data=new SimpleBytes("02000002837b866c24ca44787b29653034f16b653fe46c6f6dba687be3666c261b27bd722acf414523f0e02767469d6f2499664355865a691a3e4ab6e0ba6a46076be112b446e554b4642de0b40d3edd3065ec9495999b639f95c4b9958a9624537558989e96383a6f6c969b92939e9294949394a294eb482026d50214e1e58922909b95998f909695b0958ea4c9c49395f0662a8cc4c49e499844d893b59795969a908f4f6190999e995b4150e69e60a495c49e2b027f1e70c43a959e159899ddeb6244459e999b999ae38d8b8d98997ce59e742663a69e9967bd846c172d98fb6c9e1d3fc8978a969a6826070f9e6c117b3c179bb49d939e929494bf94a294656812e46fe115cefcc46e132495aedf9796930f908ea495c4edab6ef955e50039cd9c71fbd8b3e8a70a849a908ff49f60e219269a9e9b999ed8a42b04a1a9739bc49399c46d9d959bbd9e999b9b3e229467265cd7236cbc8c71640f4366723c6a899ee4dc870e65a1a04fdbcd5e6e4da8ad096c426f6fa7482b94bfa2d0692a86ec6b313a829bc539706d692bfd8d755b93405c3aff6602e6bbeff19e927920144158ea2f6578f0304138f39e6298b727e67ec95aea7b75ea25b9fc3f9e3a29fb0a246d976125a47e9e61a4bc9363129e7277c25cfe61d3a7fee6406476f02b7e55c2677c2c20ee3fba6b558dfc6ee607744d688b7c77cc6b246d7625422f4659495d68",16,true);
		body.add(data);
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
	
	public static SimpleBytes getLoginPacket(SimpleBytes address){
		SimpleBytes body=new SimpleBytes((byte)0xc9);
		body.add(address).add((byte)2).add((byte)0x79).add((short)0).add((short)1);
		Head head=new Head();
		head.setProtocolType(Head.PROTOCOL_GB09);
		head.setTotalDataLength(body.getLength());
		head.encode("","");
		SimpleBytes packet=new SimpleBytes();
		packet.add(head.getRawValue()).add(body).add(body.getCheckSum()).add((byte)0x16);
		return packet;
	}
}
