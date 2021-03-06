package pl.bnsit.aa.part1.concurrency;

import android.app.Activity;
import android.os.*;
import android.os.Process;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import pl.bnsit.aa.R;

/**
 * Created with IntelliJ IDEA.
 * User: marekdef
 * Date: 08.07.13
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
public class HandlerThreadExample extends Activity implements View.OnClickListener {

    public static final int DELAY_MILLIS = 5000;
    private Button button;

    private LinearLayout container;
    private long referenceTime;
    private Handler handler;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HandlerThread handlerThread = new HandlerThread("HandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());

        setContentView(R.layout.handler_layout);

        container = (LinearLayout) findViewById(R.id.container);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(button)) {
            if (referenceTime == 0)
                referenceTime = System.currentTimeMillis();
            final TimeAwareView runnable = new TimeAwareView(this, referenceTime, 500, 2000);
            container.addView(runnable);
            handler.postDelayed(runnable, DELAY_MILLIS);
        }
    }

}