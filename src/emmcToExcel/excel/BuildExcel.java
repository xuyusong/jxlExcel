package emmcToExcel.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emmcToExcel.entity.EmmcLog;
import emmcToExcel.entity.LogProcess;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class BuildExcel {
	//private WritableSheet workSheet = null;
	String filePath ="";
	public BuildExcel(String filePath){
		this.filePath = filePath;
	}
	public boolean createSheet(String name){
		File emmc = new File(filePath);
		try {
			if(!emmc.exists()){
				WritableWorkbook book = Workbook.createWorkbook(emmc);
				WritableSheet sheet = book.createSheet(name, 0);
				book.write();
				book.close();
				return true;
			}else{
				ReadExcel rx = null;
				rx = new ReadExcel(filePath);
				int pos = rx.getSheetNumber();
				if(rx.isHaveSheet(name)){
					rx.close();
					//System.out.println("已经有这个sheet");
//					Workbook wb = Workbook.getWorkbook(new File(filePath));
//					 // 打开一个文件的副本，并且指定数据写回到原文件
//					WritableWorkbook book = Workbook.createWorkbook(new File(filePath),
//			                wb);
//					WritableSheet sheet = book.getSheet(name);
//					book.write();
//					book.close();
					return false;
				}else{
					rx.close();
					Workbook wb = Workbook.getWorkbook(new File(filePath));
					 // 打开一个文件的副本，并且指定数据写回到原文件
					WritableWorkbook book = Workbook.createWorkbook(new File(filePath),
			                wb);
					if(book != null){
						WritableSheet sheet = book.createSheet(name, pos);
					}
					book.write();
					book.close();
					return true;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void writeExcel(String name,EmmcLog emmcLog){
		WritableFont title= new WritableFont(WritableFont.TIMES,16,WritableFont.BOLD);
		WritableCellFormat firtLine=new WritableCellFormat(title);
		WritableFont content= new WritableFont(WritableFont.TIMES,16,WritableFont.NO_BOLD);
		WritableCellFormat idContent=new WritableCellFormat(content);
		String total5FirstString = "";
		String over1mString ="";
		Iterator<LogProcess> it = emmcLog.getTotalMax().iterator();
		Iterator<LogProcess> it1 = emmcLog.getOver1M().iterator();
		int avg =0;
		if(emmcLog.getTotalTime()>0)
		avg = emmcLog.getTotalData()/emmcLog.getTotalTime();
		int num = 1;
		while(it.hasNext()){
			if(num ==6){
				break;
			}
			LogProcess tmp = it.next();
			total5FirstString += "进程:"+tmp.getName()+" 进程号:"+tmp.getPid()+" 写emmc:"+tmp.getEmmc()/1024+"M"+"\n";
			num ++;
		}
		while(it1.hasNext()){
			LogProcess tmp = it1.next();
			over1mString += "时间:"+tmp.getTime() +"进程:"+tmp.getName()+" 进程号:"+tmp.getPid()+" 写emmc:"+tmp.getEmmc()+"K"+"\n";
		}
		try {
			idContent.setWrap(true);
			firtLine.setWrap(true);
			firtLine.setAlignment(jxl.format.Alignment.CENTRE);
			firtLine.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			firtLine.setBackground(Colour.LIGHT_ORANGE);
			idContent.setAlignment(jxl.format.Alignment.LEFT);
			idContent.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
			if(avg>2000 && emmcLog.getTotalTime()>100){
				idContent.setBackground(Colour.RED);
			}
			Workbook wb = Workbook.getWorkbook(new File(filePath));
			 // 打开一个文件的副本，并且指定数据写回到原文件
			WritableWorkbook book = Workbook.createWorkbook(new File(filePath),
	                wb);
			WritableSheet writeSheet = book.getSheet(name);
			if(writeSheet.getCell(0, 0).getContents().equals("")){
				
				Label id=new Label(0,0,"id",firtLine);
				Label time=new Label(1,0,"监控时间",firtLine);
				Label total=new Label(2,0,"总量",firtLine);
				Label totalAvg=new Label(3,0,"平均每分钟写入",firtLine);
				Label total5First=new Label(4,0,"总量排名前五",firtLine);
				Label over1m=new Label(5,0,"单次打印超过1M",firtLine);
				writeSheet.addCell(id);
				writeSheet.addCell(time);
				writeSheet.addCell(total);
				writeSheet.addCell(totalAvg);
				writeSheet.addCell(total5First);
				writeSheet.addCell(over1m);
				writeSheet.setRowView(0,500);
				writeSheet.setColumnView(0,30);
				writeSheet.setColumnView(1,30);
				writeSheet.setColumnView(2,30);
				writeSheet.setColumnView(3,30);
				writeSheet.setColumnView(4,70);
				writeSheet.setColumnView(5,120);
			}
			int j = 1;
			while(true){
				if(writeSheet.getCell(0, j).getContents().trim().equals("")){
					System.out.println("第"+(j)+"个id写入");
					Label id=new Label(0,j,emmcLog.getId()+"",idContent);
					Label time = null;
					if(emmcLog.getTotalTime()<60){
						time=new Label(1,j,emmcLog.getTotalTime()+"分钟",idContent);
					}else{
						time=new Label(1,j,emmcLog.getTotalTime()/60+"小时 "+emmcLog.getTotalTime()%60+"分钟",idContent);
					}
//					time=new Label(1,j,emmcLog.getTotalTime()/1024+"分钟",idContent);
					Label total = null;
					if(emmcLog.getTotalData()/1024>=1024){
						 int k = emmcLog.getTotalData();
						 int m = k/1024;
						 int g = m/1024;
						 m = m%1024;
						 total=new Label(2,j,g+"G "+m+"M",idContent);
					}else{
						 total=new Label(2,j,emmcLog.getTotalData()/1024+"M",idContent);
					}
//					total=new Label(2,j,emmcLog.getTotalData()/1024+"M",idContent);
					Label totalAvg=new Label(3,j,avg+" K/min",idContent);
					Label total5First=new Label(4,j,total5FirstString,idContent);
					Label over1m=new Label(5,j,over1mString,idContent);
					writeSheet.addCell(id);
					writeSheet.addCell(time);
					writeSheet.addCell(total);
					writeSheet.addCell(totalAvg);
					writeSheet.addCell(total5First);
					writeSheet.addCell(over1m);
					break;
				}
				j= j+1;
			}
			book.write();
			book.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("write error");
			e.printStackTrace();
		}
	}
	
	public List<EmmcLog> readExcel(String name){
		List<EmmcLog> emmcLogs = new ArrayList<EmmcLog>();
		try{
			Workbook book = Workbook.getWorkbook(new File(filePath));
	        Sheet sheet = book.getSheet(name);
	        int rows = sheet.getRows();
	        for(int j= 1;j<rows;j++){
	        	if(!sheet.getCell(0,j).getContents().trim().equals("")){
	        		EmmcLog emmcLog = new EmmcLog();
	        		emmcLog.setId(Integer.parseInt(sheet.getCell(0, j).getContents().trim()));
	        		if(!sheet.getCell(1, j).getContents().trim().equals("")){
	        			emmcLog.setTotalTime(totalTimeToInt(sheet.getCell(1, j).getContents().trim()));
	        		}
	        		if(!sheet.getCell(2, j).getContents().trim().equals("")){
	        			emmcLog.setTotalData(totalDataToInt(sheet.getCell(2, j).getContents().trim()));
	        		}
	        		if(!sheet.getCell(4, j).getContents().trim().equals("")){
	        			String[] max5Data = sheet.getCell(4, j).getContents().trim().split("\n");
	        			List<LogProcess> list = new ArrayList<LogProcess>();
	        			for (String logProcess : max5Data) {
	        				list.add(stringToLogProcess(logProcess.trim()));
						}
	        			emmcLog.setTotalMax(list);
	        		}
	        		if(!sheet.getCell(5, j).getContents().trim().equals("")){
	        			String[] over1M = sheet.getCell(5, j).getContents().trim().split("\n");
	        			List<LogProcess> list = new ArrayList<LogProcess>();
	        			for (String logProcess : over1M) {
	        				list.add(stringToLogProcess2(logProcess));
						}
	        			emmcLog.setOver1M(list);
	        		}
	        		emmcLogs.add(emmcLog);
	        	}
	        }
	        book.close();
		} catch (Exception e) {
		// TODO Auto-generated catch block
			System.out.println("read error");
			e.printStackTrace();
		}
		return emmcLogs;
	}
	
	public int getIntString(String regex){
		Pattern pattern = Pattern.compile("([0-9]*)");
		Matcher matcher = pattern.matcher(regex);
		while(matcher.find()){
			if(!matcher.group(1).trim().equals("")){
				return Integer.parseInt(matcher.group(1).trim())*1024;
			}
		}
		return 0;
	}
	public int totalDataToInt(String data){
		int res =0;
		if(data.matches("([0-9]*).*[A-Z]+\\s([0-9]*).*[A-Z]+.*")){
			Pattern pattern = Pattern.compile("([0-9]*).*[A-Z]+\\s([0-9]*).*[A-Z]+.*");
			Matcher matcher = pattern.matcher(data);
			while(matcher.find()){
					if(!matcher.group(1).trim().equals("")){
						res = res+Integer.parseInt(matcher.group(1).trim())*1024*1024;
					}
					if(!matcher.group(2).trim().equals("")){
						res =res + Integer.parseInt(matcher.group(1).trim())*1024;
					}
				}
		}else{
			Pattern pattern = Pattern.compile("([0-9]*).*");
			Matcher matcher = pattern.matcher(data);
			while(matcher.find()){
				if(!matcher.group(1).trim().equals("")){
					res = res+Integer.parseInt(matcher.group(1).trim())*1024;
				}
			}
		}
		return res;
	}
	public int totalTimeToInt(String time){
		int res = 0;
		if(time.matches("([0-9]*)[\u4e00-\u9fa5]* ([0-9]*)[\u4e00-\u9fa5]*")){
			Pattern pattern = Pattern.compile("([0-9]*)[\u4e00-\u9fa5]* ([0-9]*)[\u4e00-\u9fa5]*");
			Matcher matcher = pattern.matcher(time);
			while(matcher.find()){
				if(!matcher.group(1).trim().equals("")){
					res = res+Integer.parseInt(matcher.group(1).trim())*60;
				}
				if(!matcher.group(2).trim().equals("")){
					res =res + Integer.parseInt(matcher.group(1).trim());
				}
			}
		}else{
			Pattern pattern = Pattern.compile("([0-9]*).*");
			Matcher matcher = pattern.matcher(time);
			while(matcher.find()){
				if(!matcher.group(1).trim().equals("")){
					res = res+Integer.parseInt(matcher.group(1).trim());
				}
			}
		}
		return res;
	}
	
	public LogProcess stringToLogProcess(String log){
		LogProcess logProcess = new LogProcess();
		Pattern patter = Pattern.compile("[\u4e00-\u9fa5]*:(.*) [\u4e00-\u9fa5]*:([0-9]*) [\u4e00-\u9fa5]*emmc:([0-9]*)");
		Matcher matcher = patter.matcher(log);
		while(matcher.find()){
			if(!matcher.group(1).equals("")){
				logProcess.setName(matcher.group(1));
			}
			if(!matcher.group(2).equals("")){
				logProcess.setPid(Integer.parseInt(matcher.group(2)));
			}
			if(!matcher.group(3).equals("")){
				logProcess.setEmmc(Integer.parseInt(matcher.group(3))*1024);
			}
		}
		logProcess.setTime("default");
		return logProcess;
	}
	public LogProcess stringToLogProcess2(String log){
		LogProcess logProcess = new LogProcess();
		Pattern patter = Pattern.compile("[\u4e00-\u9fa5]*:([0-9]*:[0-9]*:[0-9]*.[0-9]*)[\u4e00-\u9fa5]*:(.*) [\u4e00-\u9fa5]*:([0-9]*) [\u4e00-\u9fa5]*emmc:([0-9]*)");
		Matcher matcher = patter.matcher(log);
		while(matcher.find()){
			if(!matcher.group(1).equals("")){
				logProcess.setTime(matcher.group(1));
			}
			if(!matcher.group(2).equals("")){
				logProcess.setName(matcher.group(2));
				
			}
			if(!matcher.group(3).equals("")){
				logProcess.setPid(Integer.parseInt(matcher.group(3)));
			}
			if(!matcher.group(4).equals("")){
				logProcess.setEmmc(Integer.parseInt(matcher.group(4)));
			}
		}
		return logProcess;
	}
	
	public void clearSheet(String name){
		Workbook wb;
		try {
			wb = Workbook.getWorkbook(new File(filePath));
		
			// 打开一个文件的副本，并且指定数据写回到原文件
			WritableWorkbook book = Workbook.createWorkbook(new File(filePath),
               wb);
			WritableSheet sheet = book.getSheet(name);
			int total = sheet.getRows();
			for(int i= total-1;i>=0;i--){
				sheet.removeRow(i);
			}
			book.write();
			book.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("clear faile");
			e.printStackTrace();
		}
	}
}
