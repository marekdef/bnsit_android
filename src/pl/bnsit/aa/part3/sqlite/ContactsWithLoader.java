package pl.bnsit.aa.part3.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pl.bnsit.aa.R;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/13/13
 * Time: 7:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactsWithLoader extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final String TAG = ContactsWithLoader.class.getSimpleName();
    private ListAdapter listAdapter;
    private ListView list;
    private Cursor cursor;
    private ProgressBar progressBar;
    private TextView textViewCounter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);


        list = (ListView)findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewCounter = (TextView) findViewById(R.id.textView);

        list.setOnItemClickListener(this);

        setNewCursor(null);


        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void setNewCursor(Cursor cursor) {
        if(cursor != null) {
            textViewCounter.setText(String.format("Loaded %s contact(s)", cursor.getCount()));
        }
        final Cursor oldCursor = this.cursor;
        progressBar.setVisibility(cursor != null ? View.GONE : View.VISIBLE);

        this.cursor = cursor;
        listAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME  },
                new int[] { android.R.id.text1 }) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = super.newView(context, cursor, parent);
                int id = cursor.getInt(0);
                View viewById = view.findViewById(android.R.id.text2);

                Cursor query = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=? AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL",
                        new String[]{String.valueOf(id)}, null);

                query.moveToFirst();
                ((TextView)viewById).setText(query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                query.close();
                return view;
            }
        };

        list.setAdapter(listAdapter);

        if(oldCursor != null)
        list.post(new Runnable() {
            @Override
            public void run() {
                oldCursor.close();
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, String.format("onCreateLoader(%s)", Thread.currentThread().getName()));
        CursorLoader cursorLoader = new CursorLoader(this,
                ContactsContract.Contacts.CONTENT_URI,
                new String[] {
                        ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                                    + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                                    + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))",
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        );
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, String.format("onLoadFinished %s loaded %d", Thread.currentThread().getName(), cursor.getCount()));

        setNewCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.d(TAG, "onLoaderReset");
        setNewCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

    }
}