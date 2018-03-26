package com.production.outlau.sextracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
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






        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, buildNotification().build());




        //createNotification("HII", "bodyu","url", getApplicationContext(), 0, "id");



/*

        Context context = getApplicationContext();

        System.out.println("RECEIVED");
        Intent newIntent = new Intent(context, MainActivity.class);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, newIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.background)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(new NotificationCompat.Action(
                        R.drawable.hamburger_menu, "TEST", pendingIntent2));


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, mBuilder.build());*/


















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
                //start();
                openClock();
                //startAt10();
            }
        });


    }










    protected NotificationCompat.Builder buildNotification() {

        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                // Set Icon
                .setSmallIcon(R.drawable.hamburger_menu)//TODO
                .setContentTitle("PENIS")
                // Set Ticker Message
                .setTicker(getApplicationContext().getString(R.string.customnotificationticker))
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // build a complex notification, with buttons and such
            //
            builder = builder.setContent(getComplexNotificationView());
        } else {
            // Build a simpler notification, without buttons
            //
            builder = builder.setContentTitle(getTitle())
                    .setContentText(getText(0))
                    .setSmallIcon(android.R.drawable.ic_menu_gallery);
        }
        return builder;
    }








    private RemoteViews getComplexNotificationView() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews notificationView = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.notification_builder
        );

        // Locate and set the Image into customnotificationtext.xml ImageViews
       // notificationView.setImageViewResource(
       //         R.id.imagenotileft,
       //         R.drawable.hamburger_menu);

        // Locate and set the Text into customnotificationtext.xml TextViews
        //notificationView.setTextViewText(R.id.title, getTitle());
        //notificationView.setTextViewText(R.id.text, getText(0));

        return notificationView;
    }







    public void openClock(){
        final Dialog customDialog = new Dialog(MainActivity.this);
        customDialog.setContentView(R.layout.clock_inflater);
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











   /* public static void createNotification(String title, String body,String image_url, Context context, int notificationsId, String single_id) {
        Intent notificationIntent;

        long when = System.currentTimeMillis();
        int id = (int) System.currentTimeMillis();

        NotificationCompat.BigPictureStyle notifystyle = new NotificationCompat.BigPictureStyle();
        //notifystyle.bigPicture(bitmap);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_builder);
        //contentView.setImageViewBitmap(R.id.image, bitmap);
        contentView.setTextViewText(R.id.text_view, body);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.hamburger_menu)
                .setStyle(notifystyle)
                .setCustomBigContentView(contentView)
                .setContentText(body);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("single_id",single_id);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, id, notificationIntent, 0);

        Notification notification = mBuilder.build();
        notification.contentIntent = contentIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;


        mNotificationManager.notify(notificationsId, notification);

    }

*/





















    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1;

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
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 32);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 20, pendingIntent);
    }

}