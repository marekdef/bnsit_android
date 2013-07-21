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
    private TextView textViewNotPrimes;
    private TextView textViewPrimes;
    private TextView textViewSummary;
    private ProgressBar primes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.primes);

        textViewPrimes = (TextView) findViewById(R.id.textViewPrimes);
         textViewNotPrimes = (TextView) findViewById(R.id.textViewNotPrimes);
         textViewSummary = (TextView) findViewById(R.id.textViewSummary);
         primes = (ProgressBar) findViewById(R.id.progressBar);

        Handler handler = new Handler() {
            //TODO 1C-2 make handler receive 3 messages that are devined in pl.bnsit.aa.part1.concurrency.primes.PrimeSearcher
        };

        new PrimeSearcher(handler, Byte.MAX_VALUE);
    }

    private void updateWhenNotPrimesShifted(int processed, int notPrimes) {
        textViewNotPrimes.setText(String.format("Count of numbers that are not primes is %d.\nRemoved multiples of %d.", notPrimes, processed));
    }

    private void showToastWhenFinished(int messageCounter) {
        Toast.makeText(this, "Finished", Toast.LENGTH_LONG).show();
        textViewSummary.setText("Received " + messageCounter + " messages.");
        primes.setVisibility(View.GONE);
    }

    private void updateAboutFindPrimes(int primeFound, int totalPrimes) {
        textViewPrimes.setText(String.format("Already found %d primes.\nLast found %d.", totalPrimes, primeFound));
    }


}