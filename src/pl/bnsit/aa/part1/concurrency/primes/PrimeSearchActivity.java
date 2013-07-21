package pl.bnsit.aa.part1.concurrency.primes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import pl.bnsit.aa.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/20/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimeSearchActivity extends Activity {

    private int messageCounter = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primes);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView textViewPrimes = (TextView) findViewById(R.id.textViewPrimes);
                TextView textViewNotPrimes = (TextView) findViewById(R.id.textViewNotPrimes);
                TextView textViewSummary = (TextView) findViewById(R.id.textViewSummary);
                ProgressBar primes = (ProgressBar) findViewById(R.id.progressBar);
                switch (msg.what) {
                    case PrimeSearcher.MESSAGE_FOUND_PRIME:
                        messageCounter++;
                        textViewPrimes.setText(String.format("Already found %d primes.\nLast found %d.", msg.arg2, msg.arg1));
                        break;
                    case PrimeSearcher.MESSAGE_FINISHED:
                        messageCounter++;
                        Toast.makeText(PrimeSearchActivity.this, "Finished", Toast.LENGTH_LONG).show();
                        textViewSummary.setText("Received " + messageCounter + " messages.");
                        primes.setVisibility(View.GONE);
                        break;
                    case PrimeSearcher.MESSAGE_UPDATE_NOT_PRIMES:
                        messageCounter++;
                        textViewNotPrimes.setText(String.format("Count of numbers that are not primes is %d.\nRemoved multiples of %d.", msg.arg2, msg.arg1));
                        break;
                }
            }
        };

        new PrimeSearcher(handler, Byte.MAX_VALUE);
    }



}