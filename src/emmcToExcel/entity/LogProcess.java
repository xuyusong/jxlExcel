package emmcToExcel.entity;

public class LogProcess {
	public String time;
	public int pid;
	public int emmc;
	public String name;
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getEmmc() {
		return emmc;
	}
	public void setEmmc(int emmc) {
		this.emmc = emmc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "LogProcess [time=" + time + ", pid=" + pid + ", emmc=" + emmc + ", name=" + name + "]";
	}
	
}
