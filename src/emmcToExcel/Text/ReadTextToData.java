package emmcToExcel.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emmcToExcel.entity.EmmcLog;
import emmcToExcel.entity.LogProcess;

public class ReadTextToData {
	
	
	public EmmcLog readText(String filePath){
		EmmcLog emmcLog = new EmmcLog();
		try {
            String encoding="Unicode";
			//String encoding="utf-8";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                Pattern pattern = null;
                Matcher matcher = null;
                int lastMaxTime =0;
                int lastMaxData =0;
                List<LogProcess> over1m = null;
                Map<String,LogProcess> allProcess = new HashMap<String,LogProcess>();
                while((lineTxt =bufferedReader.readLine()) != null){
                	//System.out.println(lineTxt.trim());
                	if(lineTxt.trim() != null && !lineTxt.trim().equals("") ){
                		if(isFirstChinese(lineTxt.trim())){
                				pattern = Pattern.compile("..[\u4e00-\u9fa5]*([0-9]*)[\u4e00-\u9fa5]*([0-9]*).*$");
                				matcher = pattern.matcher(lineTxt);
                				while(matcher.find()){
                					if(!matcher.group(1).equals("")){
                						emmcLog.setId(Integer.parseInt(matcher.group(1)));
    	        					}
                					if(!matcher.group(2).equals(""))
                						emmcLog.setNumber(Integer.parseInt(matcher.group(2)));
    	        				}
							}
                		}
                		if(isFirstNumber(lineTxt.trim())){
                			if(isContainTotal(lineTxt.trim())){
                				pattern = Pattern.compile(".*is.([0-9]*).min.*data:([0-9]*)$");
                				matcher = pattern.matcher(lineTxt);
                				while(matcher.find()){
                					if(!matcher.group(1).equals("")&&!matcher.group(2).equals("")){
	                					if(Integer.parseInt(matcher.group(1))<emmcLog.getTotalTime()){
	                						lastMaxTime = emmcLog.getTotalTime()+lastMaxTime;
	                						lastMaxData = emmcLog.getTotalData()+lastMaxData;
	                					}
	            						emmcLog.setTotalTime(Integer.parseInt(matcher.group(1)));
	            						emmcLog.setTotalData(Integer.parseInt(matcher.group(2)));
            						}
                				}
                			}else{
                				pattern = Pattern.compile("^.*\\s([0-9]*:[0-9]*:[0-9]*\\.[0-9]*)\\s*.*iopp\\s*:\\s*([0-9]*)\\s*([0-9]*)\\s*([^0-9]*[0-9]*[^0-9]*).*$");
                				matcher = pattern.matcher(lineTxt);
                				LogProcess logProcessOver1M = new LogProcess();
                				over1m = emmcLog.getOver1M();
                				LogProcess logProcess = new LogProcess();
                				while(matcher.find()){
                					if(!matcher.group(1).equals("")&&!matcher.group(2).equals("")&&!matcher.group(3).equals("")&&!matcher.group(4).equals("")){
	                					String time = matcher.group(1);
	                					int pid = Integer.parseInt(matcher.group(2));
	                					int emmc = Integer.parseInt(matcher.group(3));
	                					String name = matcher.group(4);
	                					if(Integer.parseInt(matcher.group(3))>1000){
	                						logProcessOver1M.setTime(time);
	                						logProcessOver1M.setPid(pid);
	                						logProcessOver1M.setEmmc(emmc);
	                						logProcessOver1M.setName(name);
	                						over1m.add(logProcessOver1M);
	                					}
	                					if(allProcess.get(name) != null){
	                						logProcess = allProcess.get(name);
	                						allProcess.get(name).setEmmc(logProcess.getEmmc()+emmc);;
	                					}else{
	                						logProcess.setTime(matcher.group(1));
		                					logProcess.setPid(Integer.parseInt(matcher.group(2)));
		                					logProcess.setEmmc(Integer.parseInt(matcher.group(3)));
		                					logProcess.setName(matcher.group(4));
		                					allProcess.put(name, logProcess);
	                					}
                					}
                				}
                				emmcLog.setOver1M(over1m);
                				
                			}
                		}
                	}
                read.close();
                emmcLog.setTotalData(lastMaxData+emmcLog.getTotalData());
                emmcLog.setTotalTime(lastMaxTime+emmcLog.getTotalTime());
                Iterator<?> it = allProcess.entrySet().iterator();
                List<LogProcess> listLogProcess = new ArrayList<LogProcess>();
                Comparator<LogProcess> c = new Comparator<LogProcess>() {  
                    @Override  
                    public int compare(LogProcess o1, LogProcess o2) {  
                        // TODO Auto-generated method stub  
                        if(o1.getEmmc()<o2.getEmmc())  
                            return 1;  
                        //注意！！返回值必须是一对相反数，否则无效。jdk1.7以后就是这样。  
                //      else return 0; //无效  
                        else return -1;  
                    }  
                };    
                while(it.hasNext()){
                	@SuppressWarnings("unchecked")
					Map.Entry<String, LogProcess> entry = (Entry<String, LogProcess>) it.next();
                	listLogProcess.add(entry.getValue());
                }
                listLogProcess.sort(c);
                emmcLog.setTotalMax(listLogProcess);
                //System.out.println(emmcLog);
		    }else{
		        System.out.println("找不到指定的文件");
		    }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错");
	        e.printStackTrace();
	    }
		return emmcLog;
	}
	
	public boolean isFirstNumber(String line){
		return line.matches("^[0-9].*$");
	}
	public boolean isFirstChinese(String line){
		return !line.matches("^-.*$")&&!line.matches("^[0-9].*$");
	}
	public boolean isContainTotal(String line){
		return line.matches("^[0-9].*total.*$");
	}
}
