package cs446Server.Model;

public class CourseModel {
	String subject;
	String catalog_number;
	String title;
	
	public String getSubject() {
		return this.subject;
	}
	
	public String getCatalog_number() {
		return this.catalog_number;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setCatalog_number(String catalog_number) {
		this.catalog_number = catalog_number;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}
