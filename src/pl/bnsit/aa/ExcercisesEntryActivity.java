package pl.bnsit.aa;

import android.*;
import android.R;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import pl.bnsit.aa.part1.concurrency.*;
import pl.bnsit.aa.part1.concurrency.national.NationalGeographicExample;
import pl.bnsit.aa.part1.concurrency.primes.PrimeSearchActivity;
import pl.bnsit.aa.part2.http.PolishPostHttpTracking;
import pl.bnsit.aa.part5.gson.RandomQuoteGsonActivity;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/20/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExcercisesEntryActivity extends ListActivity implements AdapterView.OnItemClickListener {

    public static final Class[] CLASSES = new Class[]{
            ThreadExample.class,
            HandlerExample.class,
            HandlerThreadExample.class,
            ExecutorExample.class,
            AsyncTaskExample.class,
            NationalGeographicExample.class,
            PrimeSearchActivity.class,

            CameraActivity.class,
            PolishPostHttpTracking.class,
            RandomQuoteGsonActivity.class
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new ArrayAdapter<Class>(this, R.layout.simple_list_item_1, CLASSES));

        getListView().setOnItemClickListener(
                this
        );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this, CLASSES[position]);
        startActivity(intent);
    }
}