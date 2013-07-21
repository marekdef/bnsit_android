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

        writableDatabase = new TodoDatabaseHelper(getApplicationContext()).getWritableDatabase();

        Cursor query = writableDatabase.query(TodoDatabaseHelper.TABLE_NAME, TodoDatabaseHelper.COLUMNS_SMALL, TodoDatabaseHelper.DONE + " != 1", null, null, null, "due ASC");

        list.setAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_checked, query, new String[]{TodoDatabaseHelper.LABEL, TodoDatabaseHelper.DONE}, new int[] {android.R.id.text1, android.R.id.checkbox} ));
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
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
            sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +
                    "(" +
                    ID + " integer primary key, " +
                    LABEL + " text not null, " +
                    CONTENT + " text not null, " +
                    CREATED + " date not null, " +
                    DUE + " date not null, " +
                    PRIORITY + " integer not null, " +
                    DONE + " boolean not null" +
                    ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS todos");
            onCreate(sqLiteDatabase);
        }
    }
}