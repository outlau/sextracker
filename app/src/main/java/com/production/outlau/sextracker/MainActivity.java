package com.production.outlau.sextracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends FragmentActivity {
    public final static int PAGES = 5;
    // You can choose a bigger number for LOOPS, but you know, nobody will fling
    // more than 1000 times just in order to test your "infinite" ViewPager :D
    public final static int LOOPS = 1000;
    public final static int FIRST_PAGE = PAGES * LOOPS / 2;

    public MyPagerAdapter adapter;
    public ViewPager pager;
    public TextView monthTextView;
    public TextView yearTextView;

    Button setTimeButton;

    private PendingIntent pendingIntent;

    private final static String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.viewpager);
        monthTextView = (TextView) findViewById(R.id.month);
        yearTextView = (TextView) findViewById(R.id.year);

        setTimeButton = (Button) findViewById(R.id.set_time_button);

        monthTextView.setText(months[MyFragment.getWeek(0).get(0).get(Calendar.MONTH)]);
        yearTextView.setText(Integer.toString(MyFragment.getWeek(0).get(0).get(Calendar.YEAR)));

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/lato_hairline.ttf");
        monthTextView.setTypeface(font);


        adapter = new MyPagerAdapter(this, this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setPageTransformer(false, adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        pager.setCurrentItem(FIRST_PAGE);

        // Necessary or the pager will only have one extra page to show
        // make this at least however many pages you can see
        pager.setOffscreenPageLimit(3);

        // Set margin for pages as a negative number, so a part of next and
        // previous pages will be showed
        pager.setPageMargin(-20);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                monthTextView.setText(months[MyFragment.getWeek(position-FIRST_PAGE).get(0).get(Calendar.MONTH)]);
                yearTextView.setText(Integer.toString(MyFragment.getWeek(position-FIRST_PAGE).get(0).get(Calendar.YEAR)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);

                openClock();
                start();
            }
        });


    }


    public void openClock(){
        final Dialog customDialog = new Dialog(MainActivity.this);
        // the setContentView() method is used to set the custom layout for the dialog
        customDialog.setContentView(R.layout.clock_inflater);
        // using window set the hight and width of custom dialog
        Window window = customDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,      WindowManager.LayoutParams.WRAP_CONTENT);    window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final SeekArc arc = (SeekArc) customDialog.findViewById(R.id.seekArc);
        final TextView timeText = (TextView) customDialog.findViewById(R.id.time_text);
        final TextView acceptText = (TextView) customDialog.findViewById(R.id.ok_button);
        acceptText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        arc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            //String time = timeFormat.format(Globals.calendar.getTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            //String date = dateFormat.format(Globals.calendar.getTime());

            Calendar calendar = Calendar.getInstance();
            int isPM = 0;
            int hours;
            int minutes;
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                int curMins = seekArc.getProgress();

                hours = (int)Math.floor(12*curMins/360);
                minutes = 2*curMins % 60;

                isPM += arc.getChangeSignState() + 2;
                isPM %= 2;

                if (isPM == 1){
                    hours += 12;
                }

                calendar.set(Calendar.HOUR_OF_DAY,hours);
                calendar.set(Calendar.MINUTE,minutes);
                SimpleDateFormat timeFormatConst = new SimpleDateFormat("HH:mm");
                String timeStr = timeFormatConst.format(calendar.getTime());

                timeText.setText(timeStr);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                arc.setPositive(arc.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                int stopHour = calendar.get(Calendar.HOUR_OF_DAY);
                int stopMinute = calendar.get(Calendar.MINUTE);
                if(stopMinute<15)
                    stopMinute = 0;
                else if(stopMinute<45)
                    stopMinute=30;
                else{
                    stopHour++;
                    calendar.add(Calendar.HOUR_OF_DAY,1);
                    stopMinute = 0;
                }
                calendar.set(Calendar.MINUTE, stopMinute);
                SimpleDateFormat timeFormatConst = new SimpleDateFormat("HH:mm");
                String timeStr = timeFormatConst.format(calendar.getTime());

                timeText.setText(timeStr);

                arc.setProgress((stopHour % 12 * 30) + stopMinute/2);
            }
        });
        customDialog.show();
    }


    public void openTimer1(final View view){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //set the top text of alert box
        TextView title = new TextView(this);
        title.setText("HOW MUCH MINS?");
        title.setPadding(40,40,0,0);

        //set a frame layout to add views to
        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);
        layout.setPadding(2, 2, 2, 2);

        //TODO set time to server time

        /*
         * HOURS AND MINUTES
         */

        //Get hours from system
        String hourTimePattern = "HH";
        SimpleDateFormat hourTimeFormat = new SimpleDateFormat(hourTimePattern);
        int hour = Integer.parseInt(hourTimeFormat.format(new Date()));
        String hourTime = hourTimeFormat.format(new Date());

        //Get minutes from system
        String minuteTimePattern = "mm";
        SimpleDateFormat minuteTimeFormat = new SimpleDateFormat(minuteTimePattern);
        int minute = Integer.parseInt(minuteTimeFormat.format(new Date()));
        String minuteTime = minuteTimeFormat.format(new Date());

        //Set time to time textview
        final TextView timeText = new TextView(this);
        timeText.setText(hourTime+":"+minuteTime);
        timeText.setGravity(Gravity.CENTER);
        timeText.setPadding(0,0,0,50);

        /*
         * DATE
         */

        //Get date from system
        String datePattern = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        String date = dateFormat.format(new Date());

        //Set date to date textview
        final TextView dateText = new TextView(this);
        dateText.setText(date);
        dateText.setGravity(Gravity.CENTER);
        dateText.setPadding(0,50,0,0);

        //Add time views to layout
        layout.addView(timeText);
        layout.addView(dateText);

        View tv = (View) getLayoutInflater().inflate(R.layout.clock_inflater, layout, true);
        final SeekArc arc = (SeekArc) tv.findViewById(R.id.seekArc);

        //ON CREATE SET TO TIME
        arc.setProgress((hour%12)*30 + minute/2);

        alert.setCustomTitle(title);
        alert.setView(layout);
        alert.setCancelable(true);


/*
        if (hour >= 12){
            Globals.isPM = 1;
        }
        else{
            Globals.isPM = 0;
        }
        final int startingIsPm = Globals.isPM;

        Globals.calendar = Calendar.getInstance();
        Globals.startupCalendar = Calendar.getInstance();
        Globals.hour = hour;
        Globals.minute = minute;
        Globals.startingProgress = arc.getProgress();

*/

        arc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            //String time = timeFormat.format(Globals.calendar.getTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            //String date = dateFormat.format(Globals.calendar.getTime());

            int isPM = 0;
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                int curMins = seekArc.getProgress();

                double hours = Math.floor(12*curMins/360);
                double minutes = 2*curMins % 60;

                isPM += arc.getChangeSignState() + 2;
                isPM %= 2;

                if (isPM == 1){
                    hours += 12;
                }
                /*
                if(hours > 13 || hours <11 ){
                    Globals.calendar.add(Calendar.DAY_OF_YEAR, arc.getChangeSignState());
                }

                Globals.calendar.set(Calendar.HOUR_OF_DAY,(int)hours);
                Globals.calendar.set(Calendar.MINUTE,(int)minutes);

                try{

                    SimpleDateFormat dateFormatConst = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                    String curDateStr = dateFormatConst.format(Globals.calendar.getTime());
                    String dateConstStr = dateFormatConst.format(Globals.startupCalendar.getTime());

                    Date curDate = dateFormatConst.parse(curDateStr);
                    Date dateConst = dateFormatConst.parse(dateConstStr);

                    if (curDate.compareTo(dateConst)<0){
                        //TODO eliminate preceeding seekarc - right now the arc before startHour is visible - dont want that
                        Globals.isBelow = true;
                    }
                    else{
                        Globals.isBelow = false;
                    }

                }catch (ParseException e1){
                    e1.printStackTrace();
                }
                */

                //time = timeFormat.format(Globals.calendar.getTime());
                //date = dateFormat.format(Globals.calendar.getTime());

                timeText.setText((Double.toString(hours) + Double.toString(minutes)));//time.toString());
                dateText.setText("TEST");  //date.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                arc.setPositive(arc.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                /*if(Globals.isBelow) {

                    arc.setProgress(Globals.startingProgress);
                    time = timeFormat.format(Globals.startupCalendar.getTime());
                    date = dateFormat.format(Globals.startupCalendar.getTime());

                    Globals.calendar = Calendar.getInstance();
                    Globals.isPM = startingIsPm;

                    timeText.setText(time.toString());
                    dateText.setText(date.toString());
                    Globals.isBelow = false;
                }
                */
            }
        });

        // Setting Negative "Cancel" Button
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        // Setting Positive "OK" Button
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                /*
                Switch thisSwitch = (Switch) findViewById(view.getId() - 3);

                try{
                    SimpleDateFormat dateFormatConst = new SimpleDateFormat("HH:mm dd/MM/yyyy");

                    String curDateStr = dateFormatConst.format(Globals.calendar.getTime());
                    String dateConstStr = dateFormatConst.format(Globals.startupCalendar.getTime());

                    Date curDate = dateFormatConst.parse(curDateStr);
                    Date dateConst = dateFormatConst.parse(dateConstStr);

                    TextView changeStateTime = (TextView) findViewById(view.getId());

                    changeStateTime.setGravity(Gravity.CENTER);
                    changeStateTime.setPadding(0,5,5,5);

                    if (curDate.compareTo(dateConst)<=0) {
                        changeStateTime.setText("Silly rabbit! That's right now.");
                    }

                    else{
                        changeStateTime.setText("This appliance will turn " + (thisSwitch.isChecked() ? "off" : "on") + " at " + curDateStr.toString());

                        long timeSinceEpoch = Globals.calendar.getTime().getTime()/1000;

                        final String buttonID = getResources().getResourceEntryName(thisSwitch.getId());

                        requestQueue = Volley.newRequestQueue(MainActivity.this);

                        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                "http://outlau.ddns.net/lights/exec_at_time.php?time=" + timeSinceEpoch + "&state=" + (thisSwitch.isChecked() ? 0 : 1) + "&ID=" + buttonID,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        requestQueue.stop();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("error");
                                requestQueue.stop();
                            }

                        });
                        requestQueue.add(stringRequest);
                    }
                }catch (ParseException e1){
                    e1.printStackTrace();
                }
                */
            }
        });

        AlertDialog alertDialog = alert.create();

        try {
            alertDialog.show();
        } catch (Exception e) {
            // WindowManager$BadTokenException will be caught and the app would
            // not display the 'Force Close' message
            e.printStackTrace();
        }
    }



    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void startAt10() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE,1);
//        calendar.set(Calendar.HOUR_OF_DAY, 21);
  //      calendar.set(Calendar.MINUTE, 43);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, pendingIntent);
    }

}