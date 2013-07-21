package pl.bnsit.aa.part2.http;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import pl.bnsit.aa.R;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/12/13
 * Time: 8:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomQuoteApacheClientActivity extends Activity {

    public static final int START_BACKGROUND_OPERATION = 1;
    public static final int DONE_BACKGROUND_OPERATION = 2;
    public static final int ERROR_BACKGROUND_OPERATION = 4;
    private static final String QUOTE = "Quote";

    private View button;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case START_BACKGROUND_OPERATION:
                    toggleActionButtons(false);
                    break;
                case ERROR_BACKGROUND_OPERATION:
                    toggleActionButtons(true);
                    Toast.makeText(RandomQuoteApacheClientActivity.this, String.valueOf(msg.obj), Toast.LENGTH_LONG).show();
                    break;
                case DONE_BACKGROUND_OPERATION:
                    toggleActionButtons(true);
                    TextView viewById = (TextView) findViewById(R.id.quote);
                    String quote = String.valueOf(msg.obj);
                    viewById.setText(quote);
                    getSharedPreferences(QUOTE, MODE_PRIVATE).edit().putString(QUOTE, quote).commit();
                    break;
            }
        }

        private void toggleActionButtons(boolean enable) {
            findViewById(R.id.button).setEnabled(enable);
            findViewById(R.id.progressBar).setVisibility(enable ? View.INVISIBLE : View.VISIBLE);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.random_quote);

        button = findViewById(R.id.button);

        button.setOnClickListener(new RandomQuoteClickListener(handler));
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.

        TextView quoteView = (TextView)findViewById(R.id.quote);
        String quote = getSharedPreferences(QUOTE, MODE_PRIVATE).getString(QUOTE, null);
        if(quote != null)
            quoteView.setText(quote);

    }

    @Override
    protected void onPause() {
//       handler.removeCallbacksAndMessages(national_geographic);
        super.onPause();
    }
}