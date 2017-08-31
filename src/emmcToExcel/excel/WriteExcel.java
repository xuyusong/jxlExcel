package emmcToExcel.excel;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class WriteExcel {
    WritableWorkbook write = null;
	public WriteExcel(String filePath){
		try {
			write = Workbook.createWorkbook(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Sheet createSheet(String name,int pos){
		Sheet sheet = null;
		sheet = write.createSheet(name, pos);
		try {
			write.write();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sheet;
	}
	public void close(){
		try {
			write.close();
		} catch (WriteException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
