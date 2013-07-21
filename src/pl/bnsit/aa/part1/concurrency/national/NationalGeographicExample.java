package pl.bnsit.aa.part1.concurrency.national;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import pl.bnsit.aa.R;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: marekdef
 * Date: 08.07.13
 * Time: 21:49
 * To change this template use File | Settings | File Templates.
 */
public class NationalGeographicExample extends Activity implements View.OnClickListener {

    public static final int DELAY_MILLIS = 5000;
    public static final int MESSAGE_INFO = 0;
    public static final int MESSAGE_READY = 1;
    public static final int MESSAGE_ERROR = 2;
    public static final int MESSAGE_DONE = 4;
    private Button button;
    private LinearLayout container;
    private long referenceTime;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            TextView textView = (TextView) findViewById(R.id.textView);
            LinearLayout horizontalScrollView = (LinearLayout)findViewById(R.id.contents);
            switch(msg.what) {
                case MESSAGE_INFO:
                    textView.setText((String)msg.obj);
                    break;
                case MESSAGE_READY:
                    textView.setText(String.format("Done loading of %s", msg.obj));
                    ImageView imageView = new ImageView(NationalGeographicExample.this);
                    Bitmap bitmap = BitmapFactory.decodeFile(new File(ngDirectory, (String) msg.obj).getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    horizontalScrollView.addView(imageView);
                    break;
                case MESSAGE_ERROR:
                    textView.setText("Cannot load %s");
                    break;
                case MESSAGE_DONE:
                    textView.setText("Done loading files");
                    break;
            }
        }
    };
    private File ngDirectory;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.national_geographic);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        ngDirectory = new File(getFilesDir(), "ng");
        ngDirectory.mkdirs();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(button)) {
            new Thread(new NationalGeographicMainParser(handler, ngDirectory)).start();
        }
    }

}