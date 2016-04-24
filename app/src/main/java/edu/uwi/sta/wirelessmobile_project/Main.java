package edu.uwi.sta.wirelessmobile_project;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
    Firebase firebaseRef;

    public class ItemAdapter extends ArrayAdapter<String> {
        public ItemAdapter(Context context, String[] items){
            super(context, 0, items);
        }
        public View getView(int position, View v, ViewGroup p){
            v = LayoutInflater.from(getContext()).inflate(R.layout.menu_adapter, p, false);
            String item = getItem(position);
            String [] itemName = getContext().getResources().getStringArray(R.array.menu_array);
            TypedArray itemImages = getContext().getResources().obtainTypedArray(R.array.menu_images);
            ((TextView)v.findViewById(R.id.item_txt)).setText(item);
            ((ImageView)v.findViewById(R.id.img_icon)).setImageResource(itemImages.getResourceId(position, 0));
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        course=(EditText)findViewById(R.id.txt_courseCode);
        ListView lv = (ListView)findViewById(R.id.items_list);
        String[] items = getResources().getStringArray(R.array.menu_array);
        ArrayAdapter adapter = new ItemAdapter(this, items);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    cal = Calendar.getInstance();
                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(Main.this);
                    final View time1 = new View(Main.this);
                    builder1.setTitle("Set Class Details");
//                    builder1.setMessage();
//                    builder1.setView(time1);
                    builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(Main.this,"Details not set", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });
                    builder1.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
                            alertDialog.setTitle("Course");
                            alertDialog.setMessage("Enter Course Code");
                            final EditText input = new EditText(Main.this);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            alertDialog.setView(input);
                            alertDialog.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    courseCode = input.getText().toString();
                                }
                            });
                            alertDialog.setNegativeButton("NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            endTime(time1);
                            startTime(time1);
                            alertDialog.show();

                        }
                    });
                    builder1.show();


                }
                else if(position == 1){
                    takeAttendance(view);
                }
                else if(position == 2){
                    searchRecords(view);
                }
            }
        });
    }

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

//            courseCode = course.getText().toString();
            Intent intent = new Intent(Main.this, Scanner.class);
            intent.putExtra("course_code", courseCode);
            intent.putExtra("start_time", stime);
            intent.putExtra("end_time", etime);
            Toast.makeText(getApplicationContext(), courseCode + "  " + stime + " - " + etime, Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    }

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
       start.setMessage("Set the start time of the class");
       start.show();
   }

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
        end.setMessage("Set the end time of the class");
        end.show();
    }

    public void searchRecords(View view){
        Intent intent = new Intent(Main.this,Attendance.class);
        startActivity(intent);
    }

}
