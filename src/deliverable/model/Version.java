package deliverable.model;

import java.util.Date;

public class Version {

	private String name;
	private Date date;
	private int index;
	
	public Version(String name, Date date, int index) {
		this.name = name;
		this.date = date;
		this.index = index;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	
}
