package ltim.master;

import android.app.Activity;
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
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import db.CategoriesContract;
import db.CategoryDBHelper;
import db.ElementContract;
import db.ElementDBHelper;
import db.ItemContract;
import db.ItemDBHelper;

public class Categories extends ListActivity {

    private CategoryDBHelper helper;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        updateCategoriesUI();
    }

    private void updateCategoriesUI() {
        helper = new CategoryDBHelper(Categories.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        helper.onCreate(sqlDB);
        Cursor cursor = sqlDB.query(CategoriesContract.TABLE,
                new String[]{CategoriesContract.Columns._ID, CategoriesContract.Columns.CATEGORY},
                null,null,null,null,null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.category,
                cursor,
                new String[] {CategoriesContract.Columns.CATEGORY},
                new int[] { R.id.categoryView},
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
                        String cat = inputField.getText().toString();

                        CategoryDBHelper helper = new CategoryDBHelper(Categories.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(CategoriesContract.Columns.CATEGORY, cat);

                        db.insertWithOnConflict(CategoriesContract.TABLE, null, values,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        updateCategoriesUI();
                    }
                });

                builder.setNegativeButton("Cancelar",null);

                builder.create().show();
                return true;
            case R.id.switch_activity:
                Intent i = new Intent(this, Categories.class);
                startActivity(i);
                return true;

            default:
                return false;
        }
    }

    /**
     * Es crida quan es pitja sobre una categoria
     * @param view
     */
    public void onViewCategoryClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.categoryView);
        String cat = taskTextView.getText().toString();
        Intent intent = new Intent(this, Element.class);
        intent.putExtra("category", cat);
        startActivity(intent);
    }

    /**
     * Es crida quan es pitja sobre el botó d'esborrar
     * @param view
     */
    public void confirmRemovalClick(View view) {
        View v = (View) view.getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.categoryView);
        final String cat = taskTextView.getText().toString();
        final Activity currentActivity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar una categoria");
        builder.setMessage("Segur que voleu eliminar " + cat + "?");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ElementDBHelper helper = new ElementDBHelper(Categories.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                // Eliminam els elements de la categoria
                db.delete(ElementContract.TABLE,
                        ElementContract.Columns.CATEGORY + "= ?",
                        new String[] {cat});

                // Eliminam la categoria
                db.delete(CategoriesContract.TABLE,
                        CategoriesContract.Columns.CATEGORY + "= ?",
                        new String[]{cat});

                String msg = "S'ha eliminat la categoria " + cat;
                Toast.makeText(currentActivity, msg, Toast.LENGTH_SHORT).show();
                updateCategoriesUI();
            }
        });

        builder.setNegativeButton("Cancelar",null);

        builder.create().show();
    }
}
