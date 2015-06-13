package utilTest;

import java.util.Arrays;

public class M15 {
	public static void main(String[] args) {
		String mainIpString="0.0.0.0";
		int[] mainIp=new int[4];
		int[] subIp=new int[4];
		String subIpString="10.9.53.89";
		int i=0;
		for(String s:mainIpString.split("\\.")){
			mainIp[i]=Integer.parseInt(s);
			i++;
		}
		i=0;
		for(String s:subIpString.split("\\.")){
			subIp[i]=Integer.parseInt(s);
			i++;
		}
		System.out.println(Arrays.toString(mainIp));

		System.out.println(Arrays.toString(subIp));

		
	}
}
