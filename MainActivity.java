package com.example.safedriveapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Formatter;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;


public class MainActivity extends AppCompatActivity implements LocationListener{
    private static final int PERMISSION_REQUEST_READ_PHONE_STATE =1000 ;





    //Variables for Animation
    Animation topAnim,bottomAnim,blinkAnim ;
    Button launchImageView;
    TextView logo,slogan;
    SwitchCompat sw_metric;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    TextView textViewButton2;
    TextView textViewButton3;
    RelativeLayout continueRelativeLayout;
    private CountDownTimer timer;










   /* public void showMap(View view){
        Intent intent = new Intent(this,SafeDriveMap.class);
        startActivity(intent);
    }*/


    public void startApp(View view){


        launchImageView.setVisibility(view.INVISIBLE);
        continueRelativeLayout.setVisibility(View.VISIBLE);

        timer = new CountDownTimer(10200,8000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewButton2.setText("BUMP CHECKER");

                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  textViewButton2.setText("BUMP AHEAD");
                                                  textViewButton2.setTextColor(0xFFFB2032);
                                                  textViewButton2.startAnimation(blinkAnim);




                MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(),R.raw.tick);
                mplayer.start();
                                              }
                },4000);

            }

            @Override
            public void onFinish() {

                try{
                    textViewButton2.setText("BUMP CHECKER");
                    textViewButton2.setTextColor(0xFF0B0B0B);
                    textViewButton2.clearAnimation();
                    timerRepeat();
                }catch(Exception e){
                    Log.e("Error", "Error: " + e.toString());
                }
                //startApp(findViewById(R.id.continueRelativeLayout));

            }
        }.start();

    }

    private void timerRepeat() {
        timer.start();
        //To cancel the timer, you can call timer.cancel();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {




        //check permission phone call

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                    requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
                }
            }
        }catch(Exception e){
            Log.e("Error", "Error: " + e.toString());
        }







        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        blinkAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_effect);





        /*Button button1 = (Button) findViewById(R.id.button1);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"Crashed Scoreboard.ttf");
        button1.setTypeface(typeface);*/



       //Hooks
        slogan = findViewById(R.id.slogan);
        logo = findViewById(R.id.logo);
        sw_metric = findViewById(R.id.sw_metric);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        textViewButton2 = findViewById(R.id.textViewButton2);
        textViewButton3 = findViewById(R.id.textViewButton3);
        continueRelativeLayout =(RelativeLayout) findViewById(R.id.continueRelativeLayout);
        launchImageView = (Button) findViewById(R.id.launchImageView);



        launchImageView.setAnimation(topAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);


       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);*/



        //Check for gps permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }else {
            //start program if permission granted
            doStuff();
        }
        this.updateSpeed(null);
        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainActivity.this.updateSpeed(null);
            }
        });
    }




    @Override
    public void onLocationChanged(Location location) {

        if(location != null){
            CLocation myLocation = new CLocation(location,this.useMetricUnits());
            this.updateSpeed(myLocation);


        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    @SuppressLint("MissingPermission")
    public void doStuff(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        }
          new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Tap at the center to Start App!\nWaiting for GPS connection!",Toast.LENGTH_LONG).show();

            }
        },0);




    }


    public void updateSpeed (CLocation location){


        float nCurrentSpeed=0;

        try {
            nCurrentSpeed = location.getSpeed();

        }catch (Exception e) {
            e.printStackTrace();
        }








        if(location != null){
            location.setUseMetricUnits(this.useMetricUnits());

            nCurrentSpeed = location.getSpeed();
            Intent intent = new Intent(getApplicationContext(), IncomingCallReceiver.class);
            intent.putExtra("nCurrentSpeed", location.getSpeed());




        }if(nCurrentSpeed>=10){



            textViewButton3.setText("WARNING\nOVERSPEEDING");
            textViewButton3.setTextColor(0xFFFB2032);
            button1.setTextColor(0xFFFB2032);
            textViewButton3.startAnimation(blinkAnim);
            button1.startAnimation(blinkAnim);



            MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(),R.raw.speeding);
            mplayer.start();




        }else{
            textViewButton3.setText("5KM/HR\nSPEED LIMIT");
            textViewButton3.setTextColor(0xFF0B0B0B);
            button1.setTextColor(0xFF319C35);
            textViewButton3.clearAnimation();
            button1.clearAnimation();


        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.getDefault(),"%5.1f",nCurrentSpeed);

        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace((char) nCurrentSpeed,'0');

        if (this.useMetricUnits()){

            button1.setText("\t\t\t\t\t"+strCurrentSpeed + "\n\n\t\t\t\t\t km/hr");
        }else {
            button1.setText("\t\t\t\t\t"+strCurrentSpeed + "\n\n\t\t\t\t\t mi/hr");

        }
       /*for(nCurrentSpeed=0;;) {
        if(nCurrentSpeed >= 1) {
            button3.setText("Warning");

        } else {
            button3.setText("80KM/HR\nSPEED LIMIT");

       */

    }
    public boolean useMetricUnits(){
        return sw_metric.isChecked();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {if (requestCode == 1000){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                doStuff();
            }else{
                finish();
            }
        }

            if (requestCode == PERMISSION_REQUEST_READ_PHONE_STATE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                }
                return;

            }

        }catch(Exception e){
            Log.e("Error", "Error: " + e.toString());
        }


}

}
   /* @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION_REQUEST_READ_PHONE_STATE, Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }
}*/
//Intent intent = new Intent(getApplicationContext(), IncomingCallReceiver.class);
//                intent.putExtra("nCurrentSpeed", location.getSpeed());
   class IncomingCallReceiver extends BroadcastReceiver implements LocationListener {



       @SuppressLint("Assert")
       public void onReceive(Context context, Intent intent){

           CLocation location = null;
           assert false;
           float nCurrentSpeed = location.getSpeed();



           ITelephony telephonyService;

           try {
               String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
               String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
               /* float nCurrentSpeed= intent.getFloatExtra("nCurrentSpeed",0); */


               if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                   TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                   try {
                       @SuppressLint("SoonBlockedPrivateApi") Method m = tm.getClass().getDeclaredMethod("getITelephony");

                       m.setAccessible(true);
                       telephonyService = (ITelephony) m.invoke(tm);

                       try{
                           if (number != null && nCurrentSpeed >=10){
                               telephonyService.endCall();

                           }


                       }catch(Exception e){
                           e.printStackTrace();
                       }




                   } catch (Exception e) {
                       e.printStackTrace();
                   }

                   makeText(context, "Ring " + number, LENGTH_SHORT).show();

               }
               if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                   makeText(context, "Answered " + number, LENGTH_SHORT).show();
               }
               if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
                   makeText(context, "Idle " + number, LENGTH_SHORT).show();
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

       @Override
       public void onLocationChanged(Location location) {

       }

       @Override
       public void onStatusChanged(String provider, int status, Bundle extras) {

       }

       @Override
       public void onProviderEnabled(String provider) {

       }

       @Override
       public void onProviderDisabled(String provider) {

       }
   }
