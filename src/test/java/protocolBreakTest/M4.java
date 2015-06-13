package protocolBreakTest;

import com.sx.mmt.internal.util.JDBCHelp;

public class M4 {
	public static void main(String[] args) {
		try {
			JDBCHelp.createConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JDBCHelp.checkTable();
	}
}
