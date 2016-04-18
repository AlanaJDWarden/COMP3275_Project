package edu.uwi.sta.wirelessmobile_project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Attendance extends AppCompatActivity {
    EditText course;
    String start_time=new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    String end_time=new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
    String date=new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    String courseCode;
    Firebase firebaseRef;
    Calendar cal;
    ArrayList<Map> records;
    ArrayAdapter adapter;
    ListView studentRecords;
    TextView numRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        course=(EditText)findViewById(R.id.txt_course);
        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase("https://comp-3275project.firebaseio.com/");

        numRecords=(TextView)findViewById(R.id.txt_num_recs);
        records=new ArrayList<>();
        adapter= new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,records);
        studentRecords=(ListView)findViewById(R.id.listView);
        studentRecords.setAdapter(adapter);
    }

    public void getDate(View view){
        cal=Calendar.getInstance();

        DatePickerDialog datePicker=new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat format;
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                format = new SimpleDateFormat("dd-MM-yyyy");
                date = format.format(cal.getTime());
            }
        },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    public void startTime(View view){
        cal=Calendar.getInstance();
        TimePickerDialog start = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat format;
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
                format = new SimpleDateFormat("HH:mm");
                start_time = format.format(cal.getTime());
            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        start.show();
    }

    public void endTime(View view){
        cal=Calendar.getInstance();
        TimePickerDialog end = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
                        SimpleDateFormat format;
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        format = new SimpleDateFormat("HH:mm");
                        end_time = format.format(cal.getTime());
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
        end.show();
    }

    public void search(View view){
        courseCode=course.getText().toString();

        firebaseRef.child(courseCode).child(date+" "+start_time+" - "+end_time).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                adapter.clear();
                adapter.notifyDataSetChanged();
                if (snapshot.exists()) {
                    numRecords.setText("There are " + snapshot.getChildrenCount() + " records");
                    for (DataSnapshot record : snapshot.getChildren()) {
                        System.out.println(record);
                        StudentRecord srec = record.getValue(StudentRecord.class);
                        System.out.println(srec.getId() + " - " + srec.getTime());

                        HashMap map = new HashMap();
                        map.put("id",srec.getId());
                        map.put("time",srec.getTime());
                        records.add(map);
                    }
                    studentRecords.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                else{
                    numRecords.setText("There are 0 records");
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
