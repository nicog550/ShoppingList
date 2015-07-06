/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ltim.master;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import db.Contract;
import db.TaskDBHelper;

/**
 *
 * @author mascport
 */
public class Practica extends ListActivity
        /*implements CompoundButton.OnCheckedChangeListener*/ {

    private CheckBox cb;
    private TaskDBHelper helper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        //cb = (CheckBox) findViewById(R.id.check);
        //cb.setOnCheckedChangeListener(this);
        updateUI();
    }

    private void updateUI() {
        helper = new TaskDBHelper(Practica.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(Contract.TABLE,
                new String[]{Contract.Columns._ID, Contract.Columns.TASK},
                null,null,null,null,null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.item,
                cursor,
                new String[] {Contract.Columns.TASK},
                new int[] { R.id.taskTextView},
                0
        );
        this.setListAdapter(listAdapter);
    }

    /*public void onCheckedChanged(CompoundButton but, boolean bol) {
        if (bol) {
            cb.setText("Picat");
        } else {
            cb.setText("No picat");
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Afegir un element");
                builder.setMessage("Introdueix el nom del nou element");
                final EditText inputField = new EditText(this);
                builder.setView(inputField);
                builder.setPositiveButton("Afegir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String task = inputField.getText().toString();
                        Log.d("MainActivity",task);

                        TaskDBHelper helper = new TaskDBHelper(Practica.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(Contract.Columns.TASK,task);

                        db.insertWithOnConflict(Contract.TABLE, null, values,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        updateUI();
                    }
                });

                builder.setNegativeButton("Cancelar",null);

                builder.create().show();
                return true;

            default:
                return false;
        }
    }

    /**
     * Es crida quan es pitja el botó "fet"
     * @param view
     */
    public void onDoneButtonClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        String task = taskTextView.getText().toString();

        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                Contract.TABLE,
                Contract.Columns.TASK,
                task);

        helper = new TaskDBHelper(Practica.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();
    }

}
