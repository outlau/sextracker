package com.production.outlau.sextracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.View.inflate;

public class MyFragment extends Fragment {

    private final static String[] weekdays = new String[]{"M","T","W","T","F","S","S"};
    AppDatabase db;

    public static Fragment newInstance(MainActivity context, int pos, float scale) {
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putFloat("scale", scale);
        return Fragment.instantiate(context, MyFragment.class.getName(), b);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        db = new AppDatabase(getContext());

        LinearLayout l = (LinearLayout) inflater.inflate(R.layout.week_inflater, container, false);

        LinearLayout weeklist = (LinearLayout)l.findViewById(R.id.week_list);

        int pos = this.getArguments().getInt("pos");
        List<Calendar> week = getWeek(pos - MainActivity.FIRST_PAGE);

        for(int i = 0; i < 7; i++) {
            TableRow inflateRow = (TableRow) inflate(getContext(), R.layout.day_inflater, null);
            TextView tvDay = (TextView)inflateRow.findViewById(R.id.day);
            tvDay.setText(weekdays[i]+"\n"+Integer.toString(week.get(i).get(Calendar.DAY_OF_MONTH)));

            ImageView rating = (ImageView)inflateRow.findViewById(R.id.rating);
            TableRow parent = (TableRow)inflateRow.findViewById(R.id.rating_parent);
            onTouchMove(parent,rating,parent,week.get(i));
            if(i == 0){
                ImageView imageView = (ImageView)inflateRow.findViewById(R.id.border);
                imageView.setImageDrawable(null);
            }
            try{
                SimpleDateFormat dateFormatConst = new SimpleDateFormat("dd/MM/yyyy");

                String curDateStr = dateFormatConst.format(Calendar.getInstance().getTime());
                String dateConstStr = dateFormatConst.format(week.get(i).getTime());

                Date curDate = dateFormatConst.parse(curDateStr);
                Date dateConst = dateFormatConst.parse(dateConstStr);

                if (curDate.compareTo(dateConst)==0) {
                    tvDay.setTextColor(ResourcesCompat.getColor(getResources(), R.color.currentDate, null));
                }
                else{

                    int colorCode = week.get(i).get(Calendar.MONTH)%2 == 1 ? R.color.oddMonths : R.color.evenMonths;
                    tvDay.setTextColor(ResourcesCompat.getColor(getResources(), colorCode, null));
                }

            }catch (ParseException e1){
                e1.printStackTrace();
            }

            inflateRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT,
                    1.0f));
            weeklist.addView(inflateRow);
        }

        MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root);
        float scale = this.getArguments().getFloat(
                "scale");
        root.setScaleBoth(scale);
        return l;
    }

    public static List<Calendar> getWeek(int weekPos) {
        List<Calendar> week = new ArrayList<>();
        Calendar startCalendar = Calendar.getInstance();
        int startDay = ((startCalendar.get(Calendar.DAY_OF_WEEK) + 5)%7)+1;
        while (week.size() < 7) {
            Calendar tempCalendar = Calendar.getInstance();
            week.add(tempCalendar);
            tempCalendar.set(Calendar.DAY_OF_YEAR,
                    startCalendar.get(Calendar.DAY_OF_YEAR) +(weekPos*7)- startDay + week.size());
        }
        return week;
    }

    private void onTouchMove(final View ViewToTouch, final View viewToMove, final View parent, final Calendar calendar) {
        ViewToTouch.setOnTouchListener(new View.OnTouchListener() {
            ImageView imageView = (ImageView) viewToMove;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.setPressed(true);
                        //viewToMove.animate()
                          //      .y(clamp(event.getY(), 0, parent.getHeight() - viewToMove.getHeight()))
                            //    .setDuration(0)
                              //  .start();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getY() > 0 && event.getY() < parent.getHeight()-viewToMove.getHeight())
                            imageView.setColorFilter(getGradientRGB(event.getY() / (parent.getHeight() - viewToMove.getHeight())));

                        viewToMove.animate()
                                .y(clamp(event.getY(),0,parent.getHeight()-viewToMove.getHeight()))
                                .setDuration(0)
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                        v.setPressed(false);
                        imageView.setColorFilter(null);
                        int id;
                        int sex;
                        if (event.getY()>parent.getHeight()*0.666){
                            id = R.drawable.no_sex;
                            sex = 0;
                            viewToMove.animate()
                                    .y(parent.getHeight() - viewToMove.getHeight())
                                    .setDuration(100)
                                    .start();
                        }else if(event.getY()>parent.getHeight()*0.2){
                            id = R.drawable.unprotected_sex;
                            sex = 1;
                            viewToMove.animate()
                                    .y((parent.getHeight() - viewToMove.getHeight())/2)
                                    .setDuration(100)
                                    .start();
                        }else{
                            id = R.drawable.protected_sex;
                            sex = 2;
                            viewToMove.animate()
                                    .y(0)
                                    .setDuration(100)
                                    .start();
                        }
                        SimpleDateFormat dateFormatConst = new SimpleDateFormat("dd-MM-yyyy");
                        String dateStr = dateFormatConst.format(calendar.getTime());
                        //db.insertValueToTable(dateStr,sex);
                        db.insertValueToTable(dateStr,sex);
                        db.getValue(dateStr);
                        //System.out.println("GET VAL: " +db.getValue(dateStr));
                        //System.out.println(db.getTableAsString(""));

                        imageView.setImageDrawable(getResources().getDrawable(id,null));
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    private int getGradientRGB(float input){
        //AT 0 RGB -> (255,0,0)
        //AT 0.5 RGB -> (255,255,0)
        //AT 1 RGB -> (0,255,0)
        int a = 160;
        int r;
        int g;
        if(input<0.5){
            r = (int)(a*input*2);
            g = a;

        }else{
            g = (int)(-a*2*input + 2*a);
            r = a;
        }
        //System.out.println("input "+input + " rgb ("+r+","+g+",0)");
        return Color.rgb(r,g,0);
    }

}