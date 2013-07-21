package pl.bnsit.aa.part4.sqlite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import pl.bnsit.aa.R;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/20/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SQToDoLite extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private View button;
    private ListView list;
    private SQLiteDatabase writableDatabase;
    private Cursor query;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.todo);

        list = (ListView)findViewById(R.id.listView);

        list.setOnItemClickListener(this);


        button = findViewById(R.id.button);

        button.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO 3A-6 Obtain the database
        writableDatabase = null;

        //TODO 3A-3 please sort by due date
        String sortCriteria = null;
        //TODO 3A-4 seems that we need full condition
        String whereCriteria = " != 1";
        //TODO 3A-5 null projects means all columns we need the more refined search looked what columns are used in the Cursor below
        String[] projection = null;
        query = writableDatabase.query(TodoDatabaseHelper.TABLE_NAME, projection, whereCriteria, null, null, null, sortCriteria);

        //a bit depracated use. We should use loaders really
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_checked, query, new String[]{TodoDatabaseHelper.LABEL, TodoDatabaseHelper.DONE}, new int[]{android.R.id.text1, android.R.id.checkbox});
        list.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        query.close();
        writableDatabase.close();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(button)) {
            startActivity(new Intent(this, AddTodo.class));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, AddTodo.class);
        intent.putExtra(AddTodo.TODO_ID, id);
        startActivity(intent);
    }

    public static class TodoDatabaseHelper extends SQLiteOpenHelper {

        private static final int VERSION = 2;
        private static final String NAME = "todo.db";
        public static final String TABLE_NAME = "todos";

        public static final String ID = "_id ";
        public static final String LABEL = "label";
        public static final String CONTENT = "content";
        public static final String CREATED = "created";
        public static final String DUE = "due";
        public static final String PRIORITY = "priority";
        public static final String DONE = "done";

        public static final String[] COLUMNS = {ID, LABEL, CONTENT, CREATED, DUE, PRIORITY, DONE};
        public static final String[] COLUMNS_SMALL = {ID, LABEL, DONE};

        public TodoDatabaseHelper(Context context) {
            super(context, NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //TODO 3A-1 Create the database using the columns defined above. You can guess
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS todos");
            ///TODO 3A-2 Some code missing we don't want to drop database on upgrade!
        }
    }
}