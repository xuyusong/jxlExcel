package emmcToExcel.entity;

import java.util.ArrayList;
import java.util.List;

public class EmmcLog {
	
	public int id;
	public int totalData;
	public int totalTime;
	
	public List<LogProcess> Over1M = new ArrayList<LogProcess>();
	
	public  List<LogProcess> totalMax = new ArrayList<LogProcess>();
	public int number;
	public int getTotalData() {
		return totalData;
	}
	public void setTotalData(int totalData) {
		this.totalData = totalData;
	}
	public int getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<LogProcess> getOver1M() {
		return Over1M;
	}
	public void setOver1M(List<LogProcess> over1m) {
		Over1M = over1m;
	}
	public List<LogProcess> getTotalMax() {
		return totalMax;
	}
	public void setTotalMax(List<LogProcess> totalMax) {
		this.totalMax = totalMax;
	}
	@Override
	public String toString() {
		return "EmmcLog [id=" + id + ", totalData=" + totalData + ", totalTime=" + totalTime + ", Over1M=" + Over1M
				+ ", totalMax=" + totalMax + ", number=" + number + "]";
	}
}
