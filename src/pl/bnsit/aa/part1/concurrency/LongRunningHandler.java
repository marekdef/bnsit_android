package pl.bnsit.aa.part1.concurrency;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/12/13
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class LongRunningHandler extends Activity {

    private Handler handler = new Handler();
    private TextView textView;
    private int count = 0;
    private String TAG = LongRunningHandler.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = new TextView(this);
        setContentView(textView);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                textView.setText(String.valueOf(count));
                Log.d(TAG, String.format("Counted %d on thread %s", count, Thread.currentThread().getName()));
                handler.postDelayed(this, 2000);

            }
        }, 2000);
    }

}