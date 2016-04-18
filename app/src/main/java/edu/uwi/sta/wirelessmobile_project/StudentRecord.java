package edu.uwi.sta.wirelessmobile_project;


public class StudentRecord {
    private String id;
    private String time;
    private String course;

    StudentRecord(){

    };
    StudentRecord(String Id,String time, String course){
        this.id =Id;
        this.time =time;
        this.course =course;
    };

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }
}
