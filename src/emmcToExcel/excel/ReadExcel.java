package emmcToExcel.excel;

import java.io.File;
import java.util.Arrays;

import jxl.Sheet;
import jxl.Workbook;

public class ReadExcel {
	
	Workbook read = null;
	public ReadExcel(String filePath){
		try {
			read = Workbook.getWorkbook(new File(filePath));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean isHaveSheet(String name){
		String SheetNames[] =  read.getSheetNames();
		if(Arrays.asList(SheetNames).contains(name)){
			return true;
		}
		return false;
	}
	
	public int getSheetNumber(){
		return read.getNumberOfSheets();
	}
	
	public void close(){
		read.close();
	}

}
