package pl.bnsit.aa.part1.concurrency;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class TimeAwareView extends View implements Runnable {
	private static final String TAG = TimeAwareView.class.getSimpleName();
	
    
    
    public static final int HEIGHT = 10;
    public static final int NOT_MEASURED = 0;
    public static final int STROKE_WIDTH = 9;
    
    private final static Random RANDOM_GENERATOR = new Random();
	
    public final int randomSleepTime;
    public final int minimalSleepTime;
    
    public final long createTime;
    public final long referenceTime;
    public long startTime = 0;
    public long finishTime = 0;


    public TimeAwareView(Context context, long reference, int minimalSleepTime, int randomSleepTime) {
        super(context);
        createTime = System.currentTimeMillis();
        this.referenceTime = reference;
        this.minimalSleepTime = minimalSleepTime;
        this.randomSleepTime = randomSleepTime;
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        try {
            int sleepTime = minimalSleepTime + RANDOM_GENERATOR.nextInt(1 + randomSleepTime);
            Thread.sleep(sleepTime);
        } catch (InterruptedException ignore) {

        }
        finishTime = System.currentTimeMillis();
        Log.d(TAG, String.format("Started at %d. Finished at for %d. Running for %d", startTime, finishTime, finishTime - startTime));
        requestLayoutInUiThread();
    }

    final Runnable action = new Runnable() {
        @Override
        public void run() {
        	Log.d(TAG, "requestLayout");
            requestLayout();
        }
    };

    public void requestLayoutInUiThread() {
        if(Looper.getMainLooper() == Looper.myLooper()) {
            action.run();
        } else {
            post(action);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (finishTime == NOT_MEASURED) {
            setMeasuredDimension(0, HEIGHT);
            Log.d(TAG, "not measured");
            return;
        }
        final long width = (finishTime - referenceTime) / 100;

        Log.d(TAG, String.format("measured %d", width));
        setMeasuredDimension((int) width, HEIGHT);
    }
    
    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        if(finishTime == NOT_MEASURED)
            return;

        paint.setStrokeWidth(STROKE_WIDTH);
        final long inQueueLine = createTime - referenceTime;
        final long waitingTime = startTime - referenceTime;
        final long workingTime = finishTime - referenceTime;

        paint.setColor(Color.GRAY);
        canvas.drawLine(inQueueLine / 100, 5, waitingTime / 100, 5, paint);
        paint.setColor(Color.RED);
        canvas.drawLine(waitingTime / 100, 5, workingTime / 100, 5, paint);

    }
}