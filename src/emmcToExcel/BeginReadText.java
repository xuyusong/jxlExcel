package emmcToExcel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emmcToExcel.Text.ReadTextToData;
import emmcToExcel.entity.EmmcLog;
import emmcToExcel.entity.LogProcess;
import emmcToExcel.entity.TotalStatistics;
import emmcToExcel.excel.BuildExcel;

public class BeginReadText {

	private static String path = "/Users/xuyusong/emmc/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReadTextToData read =new  ReadTextToData();
		File file = new File(path);
		String [] fileName = file.list();
		BuildExcel buildExcel = new BuildExcel(path+"emmc.xls");
		buildExcel.createSheet("总量统计");
		String date = "";
		String id ="";
		for (String name : fileName) {
			if(name.trim().matches("^[0-9]+[\u4e00-\u9fa5].*$")){
				Pattern pattern = Pattern.compile("^([0-9]*).([0-9]*-[0-9]*)*.*$");
				Matcher matcher = pattern.matcher(name);
				while(matcher.find()){
					List<EmmcLog> emmcLogs = new ArrayList<EmmcLog>();
					id = matcher.group(1);
					date = matcher.group(2);
					String myDate = date;
					if(buildExcel.createSheet(date.trim())){
						for (String name1 : fileName) {
							if(name1.trim().matches("^[0-9]+[\u4e00-\u9fa5].*$")){
								matcher = pattern.matcher(name1);
								while(matcher.find()){
									 if(matcher.group(2).equals(date)){
										 EmmcLog emmcLog = read.readText(path+name1);
										 buildExcel.writeExcel(date, emmcLog);
										 myDate = date;
										 emmcLogs.add(emmcLog);
									 }
								}
							}
						}
					}
					if(emmcLogs.size()!= 0){
						updateTotal(emmcLogs);
					}
				}
			}
		}
//		List<EmmcLog> emmcLogs = new ArrayList<EmmcLog>();
//		updateTotal(emmcLogs);
	}
	
	public static void updateTotal(List<EmmcLog> emmcLogs){
		BuildExcel buildExcel = new BuildExcel(path+"emmc.xls");
		List<EmmcLog> oldLogs = buildExcel.readExcel("总量统计");
		List<EmmcLog> common = null;
//		buildExcel.clearSheet("总量统计");
		if(oldLogs == null||oldLogs.size()==0){
			oldLogs = emmcLogs;
		}else{
		buildExcel.clearSheet("总量统计");
		common = new ArrayList<EmmcLog>();
		for (EmmcLog emmcLog : oldLogs) {
			for (EmmcLog newEmmcLog : emmcLogs) {
				if(newEmmcLog.getId()==emmcLog.getId()){
					emmcLog.setTotalTime(newEmmcLog.getTotalTime()+emmcLog.getTotalTime());
					emmcLog.setTotalData(newEmmcLog.getTotalData()+emmcLog.getTotalData());
					if(newEmmcLog.getTotalMax().size() >0 &&emmcLog.getTotalMax().size()>0){
						List<LogProcess> oldLog = emmcLog.getTotalMax();
						List<LogProcess> newLog = newEmmcLog.getTotalMax();
						List<LogProcess> maxCommon = newEmmcLog.getTotalMax();
						if(oldLog!=null&&oldLog.size()>0&&newLog!=null&&newLog.size()>0){
							for(int i =0;i<5;i++){
								for(int j = 0;j<5;j++){
									if(oldLog.get(i) != null&&newLog.get(j)!=null){
										if(oldLog.get(i).getName().equals(newLog.get(j).getName())){
											oldLog.get(i).setEmmc(oldLog.get(i).getEmmc()+newLog.get(j).getEmmc());
											maxCommon.add(newLog.get(j));
										}
									}
								}
							}
						}else if(oldLog==null&&oldLog.size()<=0&&newLog!=null&&newLog.size()>0){
							oldLog = newLog;
						}
						if(maxCommon!= null && maxCommon.size()!=0){
							for (LogProcess logProcess : newLog) {
								if(!maxCommon.contains(logProcess)){
									oldLog.add(logProcess);
								}
							}
						}
						Comparator<LogProcess> c1 = new Comparator<LogProcess>(){

							@Override
							public int compare(LogProcess o1, LogProcess o2) {
								// TODO Auto-generated method stub
								if(o1.getEmmc()<o2.getEmmc()){
									return 1;
								}else{
									return -1;
								}
							}
						};
						if(oldLog != null&& oldLog.size()!= 0){
							oldLog.sort(c1);
							emmcLog.setTotalMax(oldLog);
						}
					}else if(newEmmcLog.getTotalMax().size() >0 &&emmcLog.getTotalMax().size()<=0){
						emmcLog.setTotalMax(newEmmcLog.getTotalMax());
					}
					Comparator<LogProcess> c = new Comparator<LogProcess>(){
						@Override
						public int compare(LogProcess o1, LogProcess o2) {
							// TODO Auto-generated method stub
							if(o1.getEmmc()<o2.getEmmc()){
								return 1;
							}else{
								return -1;
							}
						}
					};
					List<LogProcess> tmp = new ArrayList<LogProcess>();
					if(newEmmcLog.getOver1M()!=null&&newEmmcLog.getOver1M().size() != 0&&emmcLog.getOver1M().size()!=0){
						int oldSize = emmcLog.getOver1M().size();
						int newSize = newEmmcLog.getOver1M().size();
						for(int i=0 ; i<oldSize ; i++){
							tmp.add(emmcLog.getOver1M().get(i));
							
						}
						for(int i=0 ; i<newSize ; i++){
							tmp.add(newEmmcLog.getOver1M().get(i));
						}
					}else if(newEmmcLog.getOver1M().size() != 0&&emmcLog.getOver1M().size() ==0){
						tmp = newEmmcLog.getOver1M();
					}else{
						tmp = emmcLog.getOver1M();
					}
					if(tmp !=null&&tmp.size()!=0){
						tmp.sort(c);
					}
					emmcLog.setOver1M(tmp);
					common.add(newEmmcLog);
				}
			  }
			}
		}
		if(common !=null&&common.size()!=0){
			for (EmmcLog emmcLog2 : emmcLogs) {
				if(!common.contains(emmcLog2)){
						oldLogs.add(emmcLog2);
				}
			}
		}
		Comparator<EmmcLog> c2 = new Comparator<EmmcLog>(){

			@Override
			public int compare(EmmcLog o1, EmmcLog o2) {
				// TODO Auto-generated method stub
				if(o1.getTotalData()/o1.getTotalTime()<o2.getTotalData()/o2.getTotalTime()){
					return 1;
				}else{
					return -1;
				}
			}
		};
		oldLogs.sort(c2);
		Iterator<EmmcLog> it = oldLogs.iterator();
		while(it.hasNext()){
			EmmcLog tEmmcLog = it.next();
			buildExcel.writeExcel("总量统计", tEmmcLog);
		}
	}

}
