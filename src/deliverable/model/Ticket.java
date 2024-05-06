package deliverable.model;

import java.util.Date;
import java.util.List;

public class Ticket {
	private String key;
	private Version iv;
	private Version fv;
	private Version ov;
	private List<Version> av;
	private int index;
	private Date resolutionDate;
	
	public Ticket(String key, Version iv, Version fv, Version ov, List<Version> av, Date resolutionDate) {
		this.key = key;
		this.iv = iv;
		this.fv = fv;
		this.ov = ov;
		this.av = av;
		this.resolutionDate = resolutionDate;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Version getIv() {
		return iv;
	}
	public void setIv(Version iv) {
		this.iv = iv;
	}
	public Version getFv() {
		return fv;
	}
	public void setFv(Version fv) {
		this.fv = fv;
	}
	public Version getOv() {
		return ov;
	}
	public void setOv(Version ov) {
		this.ov = ov;
	}
	public List<Version> getAv() {
		return av;
	}
	public void setAv(List<Version> av) {
		this.av = av;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public Date getResolutionDate() {
		return resolutionDate;
	}

	public void setResolutionDate(Date resolutionDate) {
		this.resolutionDate = resolutionDate;
	}
	
}
