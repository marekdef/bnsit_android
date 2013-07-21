package pl.bnsit.aa.part4.sqlite;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import pl.bnsit.aa.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/20/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddTodo extends Activity implements View.OnClickListener {

    private static final int NO_ID = -1;
    public static final String TODO_ID = "TODO_ID";
    private View button;
    private long todoId;
    private SQLiteDatabase sqlDB;
    private DatePicker datePicker;
    private CheckBox checkBox;
    private EditText editTextDescription;
    private EditText editTextLabel;
    private Spinner spinnerPriority;
    private boolean stopTracking;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_todo);

        editTextLabel = (EditText) findViewById(R.id.editTextLabel);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        checkBox = (CheckBox) findViewById(R.id.checkBoxDone);
        datePicker = (DatePicker) findViewById(R.id.datePickerDue);
        spinnerPriority = (Spinner) findViewById(R.id.spinner);

        editTextLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (stopTracking)
                    return;
                editTextDescription.setText(editTextLabel.getText());
            }
        });

        editTextDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                stopTracking = true;
            }
        });

        button = findViewById(R.id.button);
        button.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        todoId = intent.getLongExtra(TODO_ID, NO_ID);

        sqlDB = new SQToDoLite.TodoDatabaseHelper(this).getWritableDatabase();

        populateToDoFromDB();

    }

    private void populateToDoFromDB() {
        if (todoId != NO_ID) {
            stopTracking = true;

            Cursor cursor = sqlDB.query(SQToDoLite.TodoDatabaseHelper.TABLE_NAME,
                    SQToDoLite.TodoDatabaseHelper.COLUMNS,
                    SQToDoLite.TodoDatabaseHelper.ID + "= ?",
                    new String[]{String.valueOf(todoId)},
                    null,
                    null,
                    null);

            if (cursor.getCount() == 0)
                finish();

            cursor.moveToFirst();

            editTextDescription.setText(cursor.getString(cursor.getColumnIndex(SQToDoLite.TodoDatabaseHelper.CONTENT)));
            editTextLabel.setText(cursor.getString(cursor.getColumnIndex(SQToDoLite.TodoDatabaseHelper.LABEL)));
            checkBox.setChecked(cursor.getInt(cursor.getColumnIndex(SQToDoLite.TodoDatabaseHelper.DONE)) == 1);
            setDateToPicker(datePicker, cursor.getLong(cursor.getColumnIndex(SQToDoLite.TodoDatabaseHelper.DUE)));
            spinnerPriority.setSelection(cursor.getInt(cursor.getColumnIndex(SQToDoLite.TodoDatabaseHelper.PRIORITY)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        updateOrInsertToDo();

        sqlDB.close();

    }

    private void updateOrInsertToDo() {
        ContentValues values = new ContentValues();
        values.put(SQToDoLite.TodoDatabaseHelper.CONTENT, editTextDescription.getText().toString());

        values.put(SQToDoLite.TodoDatabaseHelper.LABEL, editTextLabel.getText().toString());
        values.put(SQToDoLite.TodoDatabaseHelper.DUE, getDateFromPicker(datePicker));
        values.put(SQToDoLite.TodoDatabaseHelper.DONE, checkBox.isChecked());
        values.put(SQToDoLite.TodoDatabaseHelper.PRIORITY, spinnerPriority.getSelectedItemPosition());

        if (todoId != NO_ID) {
            sqlDB.update(SQToDoLite.TodoDatabaseHelper.TABLE_NAME, values, SQToDoLite.TodoDatabaseHelper.ID + "= ?", new String[]{String.valueOf(todoId)});
        } else {
            values.put(SQToDoLite.TodoDatabaseHelper.CREATED, new Date().getTime());
            long newId = sqlDB.insert(SQToDoLite.TodoDatabaseHelper.TABLE_NAME, null, values);
        }
    }

    private long getDateFromPicker(DatePicker datePicker) {
        Calendar instance = Calendar.getInstance();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        int dayOfMonth = datePicker.getDayOfMonth();

        instance.set(Calendar.MONTH, month);
        instance.set(Calendar.YEAR, year);
        instance.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        long date = instance.getTime().getTime();
        return date;
    }

    private void setDateToPicker(DatePicker datePicker, long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date(time));
        int year = instance.get(Calendar.YEAR);
        int monthOfYear = instance.get(Calendar.MONTH);
        int dayOfMonth = instance.get(Calendar.DAY_OF_MONTH);

        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

}