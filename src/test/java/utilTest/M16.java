package utilTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sx.mmt.internal.util.CSVUtil;
import com.sx.mmt.internal.util.SimpleBytes;

public class M16 {
	public static void main(String[] args) throws Exception {
		//InputStream inp = new FileInputStream("C:\\Users\\peter\\Desktop\\a.xls");
		InputStream inp = new FileInputStream("C:\\Users\\peter\\Desktop\\a.xlsx");
		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet = wb.getSheetAt(0);
		int rowb=sheet.getFirstRowNum();
		int rowl=sheet.getLastRowNum();
		for(int i=rowb;i<=rowl;i++){
			Row row = sheet.getRow(i);
			for(int j=row.getFirstCellNum();j<row.getLastCellNum();j++){
				Cell cell=row.getCell(j);
				if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
					System.out.println(cell.getNumericCellValue());
				}else if(cell.getCellType()==Cell.CELL_TYPE_STRING){
					System.out.println(cell.getStringCellValue());
				}
			}
		}
		
		System.out.println("ddd".lastIndexOf(".bin"));
		System.out.println(new SimpleBytes((short)Integer.parseInt("0001",16)));
		
	}
}
