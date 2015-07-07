/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ltim.master;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

import db.ElementContract;
import db.ElementDBHelper;


public class Element extends ListActivity {

    private String category;
    private ElementDBHelper helper;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        setTitle(category);
        updateUI();
    }

    private void updateUI() {
        helper = new ElementDBHelper(Element.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        helper.onCreate(sqlDB);
        Cursor cursor = sqlDB.query(
                ElementContract.TABLE,
                new String[]{ElementContract.Columns._ID, ElementContract.Columns.ITEM,
                        ElementContract.Columns.IMAGE},
                ElementContract.Columns.CATEGORY + " = ?", new String[] {category},null,null,null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.element,
                cursor,
                new String[] {ElementContract.Columns.ITEM, ElementContract.Columns.IMAGE},
                new int[] {R.id.elementTextView},
                0
        );
        this.setListAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem btn = menu.findItem(R.id.switch_activity);
        btn.setVisible(false); //Ocultam el botó del menú
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
                        String elem = inputField.getText().toString();
                        Log.d("MainActivity",elem);

                        ElementDBHelper helper = new ElementDBHelper(Element.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(ElementContract.Columns.ITEM, elem);
                        values.put(ElementContract.Columns.CATEGORY, category);
                        values.put(ElementContract.Columns.IMAGE, "");

                        db.insertWithOnConflict(ElementContract.TABLE, null, values,
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
                ElementContract.TABLE,
                ElementContract.Columns.ITEM,
                task);

        helper = new ElementDBHelper(Element.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        updateUI();
    }

}
