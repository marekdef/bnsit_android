package pl.bnsit.aa.part1.concurrency;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import pl.bnsit.aa.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: marekdef
 * Date: 08.07.13
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
public class ExecutorExample extends Activity implements View.OnClickListener {

    private static final int NUMBER_OF_THREADS = 2;
	public static final int DELAY_MILLIS = 5000;
    private Button button;
    private LinearLayout container;
    private long referenceTime;
    private ScheduledExecutorService scheduledExecutorService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduledExecutorService = Executors.newScheduledThreadPool(NUMBER_OF_THREADS);
        setContentView(R.layout.handler_layout);

        container = (LinearLayout) findViewById(R.id.container);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        scheduledExecutorService.shutdownNow();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(button)) {
            if (referenceTime == 0)
                referenceTime = System.currentTimeMillis();
            final TimeAwareView runnable = new TimeAwareView(this, referenceTime, 1000, 2000);
            container.addView(runnable);
            scheduledExecutorService.schedule(runnable, DELAY_MILLIS , TimeUnit.MILLISECONDS );
        }
    }

}