package edu.uwi.sta.wirelessmobile_project;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private final int CAMERA_REQUEST_CODE=0;
    Firebase firebaseRef;
    String start_time;
    String end_time;
    String course_code;
    final int ID_LENGTH=9;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view

        Firebase.setAndroidContext(this);
        firebaseRef = new Firebase("https://comp-3275project.firebaseio.com/");

        Bundle bundle= getIntent().getExtras();
        course_code=(String)bundle.get("course_code");
        start_time=(String)bundle.get("start_time");
        end_time=(String)bundle.get("end_time");

        Toast.makeText(getApplicationContext(),course_code+"  "+start_time + " - "+end_time ,Toast.LENGTH_LONG).show();
        scan();
    }

    //START SCANNING CHECKS FOR CAMERA PERMISSIONS AND REQUEST IF THEY DONT EXIST
    public void scan(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        else {
            setContentView(mScannerView);
        }
    }

    //OCCURS ON RESUMPTION OF ACTIVITY
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    //OCCURS WHEN LEAVE SCANNING ACTIVITY
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    //RESTART SCANNING
    public void restartCamera(){
        mScannerView.stopCamera();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    //RESULT FROM REQUEST CAMERA PERMISSION
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    scan();
                }
                else {
                    Toast.makeText(getApplicationContext(),"CAMERA ACCESS PERMISSIONS MUST BE GRANTED FOR THIS TO WORK",Toast.LENGTH_LONG).show();
                    scan();
                }
                return;
            }
        }
    }

    //HANDLES THE RESULT AFTER SCANNING THE BARCODE
    @Override
    public void handleResult(Result rawResult) {
        Toast.makeText(this,rawResult.getText(),Toast.LENGTH_SHORT).show(); // Prints scan results

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        SimpleDateFormat tdf=new SimpleDateFormat("HH:mm aa");
        SimpleDateFormat ddf=new SimpleDateFormat("dd-MM-yyyy");

        final String id=rawResult.getText();
        final String date=ddf.format(Calendar.getInstance().getTime());
        final String curr_time = sdf.format(Calendar.getInstance().getTime());

        final String time = tdf.format(Calendar.getInstance().getTime());

        Date startTime=null,endTime=null,currTime=null;
        try{
            startTime=sdf.parse(start_time);
            endTime=sdf.parse(end_time);
            currTime=sdf.parse(curr_time);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //TRYING TO FIGURE OUT WHY THE DATE WITH THE TIME IS WRONG BUT IT'S ONLY TIME BEING COMPARED SO IT WORKS SOMETHING ABOUT AST1970
        System.out.println(currTime +"  "+startTime+"\n"+currTime+"  "+endTime+"\n"+ time);

        if(id.length()!=ID_LENGTH)
            Toast.makeText(getApplicationContext(),"Invalid ID Entered",Toast.LENGTH_SHORT).show();

        else if(currTime.compareTo(startTime)<0)
            Toast.makeText(getApplicationContext(),"It's Too Early to take Attendance",Toast.LENGTH_SHORT).show();

        else if(currTime.compareTo(endTime)>0)
            Toast.makeText(getApplicationContext(),"It's Too Late to take Attendance",Toast.LENGTH_SHORT).show();

        else{
            firebaseRef.child(course_code).child(date+" "+startTime+" - "+endTime).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(!snapshot.exists()){
                        addRecord(id,date,time);
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
        restartCamera();
        //mScannerView.resumeCameraPreview(this);
    }

    //ADDS RECORD TO FIREBASE FOR USER BARCODE
    public void addRecord(String id,String date,String time){
        Map map = new HashMap();
        map.put("id",id);
        map.put("time",time);

        firebaseRef.child(course_code).child(date+" "+start_time+" - "+end_time).child(id).setValue(map);
    }
}
