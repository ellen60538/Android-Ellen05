package tw.org.iii.ellen.ellen05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private boolean isRunning ;
    private Button leftBtn,rightBtn ;
    private Timer timer = new Timer() ;
    private int hs ;
    private Counter counter ;
    private UIHandler uiHandler = new UIHandler() ;
    TextView clock ;
    //-----
    private ListView listView ;
    private SimpleAdapter adapter ;
    private LinkedList<HashMap<String,String>> data = new LinkedList<>() ;
    private String[] from = {"lap","time1","time2"} ;
    private int[] to = {R.id.lap_rank, R.id.lap_time1, R.id.lap_time2} ;
    private int lapCounter ;
    private int lastHS ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftBtn = findViewById(R.id.leftBtn) ;
        rightBtn = findViewById(R.id.rightBtn) ;
        clock = findViewById(R.id.clock) ;
        listView = findViewById(R.id.listView) ;

        changeDisplay() ;
        clock.setText(parseHS(hs));

        initLap() ;
    }

    private void initLap(){
        adapter = new SimpleAdapter(this, data, R.layout.layout_lap, from, to) ;
        listView.setAdapter(adapter) ;
    }

    private void changeDisplay(){
        leftBtn.setText(isRunning ? "LAP" : "RESET") ;
        rightBtn.setText(isRunning ? "STOP" : "START") ;
    }


    public void doLeft(View view) {
        if (isRunning){
            //LAP
            doLap() ;
        }else {
            //RESET
            doReset() ;
        }
    }

    private void doLap(){
        int dHS = hs - lastHS ;
        lastHS = hs ;
        HashMap<String,String> row = new HashMap<>() ;
        row.put(from[0], "lap" + ++lapCounter) ;
        row.put(from[1], parseHS(dHS)) ;
        row.put(from[2], parseHS(hs)) ;
        data.add(0,row) ;
        adapter.notifyDataSetChanged() ;
        Log.v("ellen",parseHS(hs)) ;
    }

    private void doReset(){
        hs = 0 ;
        lastHS = 0 ;
        lapCounter = 0 ;
        data.clear() ;
        adapter.notifyDataSetChanged() ;
        clock.setText(parseHS(hs));
    }

    public void doRight(View view) {
        isRunning = !isRunning ;
        changeDisplay() ;

        if (isRunning){
            counter = new Counter() ;
            timer.schedule(counter,10,10) ;
        }else {
            counter.cancel() ;
            counter = null ;
        }
    }

    private class Counter extends TimerTask{
        @Override
        public void run() {
            hs++ ;
            uiHandler.sendEmptyMessage(0) ;
        }
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg) ;
            clock.setText(parseHS(hs));
        }
    }

    private static String parseHS(int hs){
        int hr, min, sec, phs;
        phs = hs % 100 ;
        sec = hs % (100*60) / 100 ;
        min = hs % (100*60*60) / (100*60) ;
        hr = hs / (100*60*60) ;

        return  (hr<10 ? "0"+hr : hr) +":"+
                (min<10 ? "0"+min : min) +":"+
                (sec<10 ? "0"+sec : sec) +"."+
                (phs<10 ? "0"+phs : phs);
    }

    @Override
    public void finish() {
        if (timer != null) {
            timer.cancel() ;
            timer.purge() ;
            timer = null ;
        }
        super.finish();
    }
}
