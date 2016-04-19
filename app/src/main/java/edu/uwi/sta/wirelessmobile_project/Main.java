package edu.uwi.sta.wirelessmobile_project;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main extends AppCompatActivity {
    EditText course;
    String stime=new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    String etime=new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    String courseCode;
    Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        course=(EditText)findViewById(R.id.txt_courseCode);
    }

    //START SCANNING ACTIVITY
    public void takeAttendance(View view){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTime=null,endTime=null,currTime=null;
        try{
            startTime=sdf.parse(stime);
            endTime=sdf.parse(etime);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(endTime.compareTo(startTime)<0) {
            Toast.makeText(getApplicationContext(), "INVALID START AND END TIMES FOR COURSE", Toast.LENGTH_SHORT).show();
        }
        else {
            courseCode = course.getText().toString();
            Intent intent = new Intent(Main.this, Scanner.class);
            intent.putExtra("course_code", courseCode);
            intent.putExtra("start_time", stime);
            intent.putExtra("end_time", etime);
            Toast.makeText(getApplicationContext(), courseCode + "  " + stime + " - " + etime, Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }
    //GET START TIME OF CLASS FROM TIME PICKER
    public void startTime(View view){
        cal=Calendar.getInstance();
        TimePickerDialog start = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
           @Override
           public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
               SimpleDateFormat format;
               Calendar calendar = Calendar.getInstance();
               calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
               calendar.set(Calendar.MINUTE, minute);
               format = new SimpleDateFormat("HH:mm");
               stime = format.format(calendar.getTime());
           }
       }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
       start.show();
    }

    //GET END TIME OF CLASS FROM TIME PICKER
    public void endTime(View view){
        cal=Calendar.getInstance();
        TimePickerDialog end = new TimePickerDialog(this,
            new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                SimpleDateFormat format;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                format = new SimpleDateFormat("HH:mm");
                etime = format.format(calendar.getTime());
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        end.show();
    }

    //START VIEW RECORDS ACTIVITY
    public void searchRecords(View view){
        Intent intent = new Intent(Main.this,Attendance.class);
        startActivity(intent);
    }
}
