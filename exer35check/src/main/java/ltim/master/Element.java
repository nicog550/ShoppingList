package ltim.master;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import db.ElementContract;
import db.ElementDBHelper;
import db.ItemContract;
import db.ItemDBHelper;


public class Element extends ListActivity {

    private String category;
    private ElementDBHelper helper;
    private Cursor cursor;
    private ImageButton elementImage;
    private final int REQUEST_CODE_PICK_FILE = 1;
    private String imageTag;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        Intent intent = getIntent();
        category = intent.getStringExtra("category");
        setTitle(category);
        updateElementsUI();
    }

    private void updateElementsUI() {
        helper = new ElementDBHelper(Element.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        helper.onCreate(sqlDB);
        cursor = sqlDB.query(
                ElementContract.TABLE,
                new String[]{ElementContract.Columns._ID, ElementContract.Columns.ITEM,
                        ElementContract.Columns.IMAGE},
                ElementContract.Columns.CATEGORY + " = ?", new String[] {category},null,null,null);

        SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.element,
                cursor,
                new String[] {
                        ElementContract.Columns.ITEM,
                        ElementContract.Columns.IMAGE},
                new int[] {R.id.elementTextView, R.id.imageView},
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

                        ElementDBHelper helper = new ElementDBHelper(Element.this);
                        SQLiteDatabase db = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.clear();
                        values.put(ElementContract.Columns.ITEM, elem);
                        values.put(ElementContract.Columns.CATEGORY, category);
                        values.put(ElementContract.Columns.IMAGE, "");

                        db.insertWithOnConflict(ElementContract.TABLE, null, values,
                                SQLiteDatabase.CONFLICT_IGNORE);
                        updateElementsUI();
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
     * Es crida quan es pitja el botó d'afegir a la llista
     * @param view
     */
    public void onAddToCartClick(View view) {
        View v = (View) view.getParent().getParent();
        TextView tv = (TextView)v.findViewById(R.id.elementTextView);
        final String name = tv.getText().toString();
        final String img = getImage(name);
        final Activity currentActivity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Afegir \"" + name + "\" a la llista");
        builder.setMessage("Introdueix la quantitat");
        final EditText inputField = new EditText(this);
        builder.setView(inputField);
        builder.setPositiveButton("Afegir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String num = inputField.getText().toString();
                String toBeInserted = num + " x " + name;
                String finalText = "S'ha afegit \"" + toBeInserted + "\" a la llista";
                Toast.makeText(currentActivity, finalText, Toast.LENGTH_SHORT).show();

                ItemDBHelper helper = new ItemDBHelper(Element.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.clear();
                values.put(ItemContract.Columns.ITEM, toBeInserted);
                values.put(ItemContract.Columns.IMAGE, img);

                db.insertWithOnConflict(ItemContract.TABLE, null, values,
                        SQLiteDatabase.CONFLICT_IGNORE);
            }
        });

        builder.setNegativeButton("Cancelar",null);

        builder.create().show();
    }

    private String getImage(String name) {
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        cursor = sqlDB.query(ElementContract.TABLE,
                new String[]{ElementContract.Columns.IMAGE},
                ElementContract.Columns.ITEM + " = ?", new String[] {name},null,null,null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    /**
     * Es crida quan es pitja sobre el botó d'esborrar
     * @param view
     */
    public void confirmRemovalClick(View view) {
        View v = (View) view.getParent().getParent();
        TextView taskTextView = (TextView) v.findViewById(R.id.elementTextView);
        final String elem = taskTextView.getText().toString();
        final Activity currentActivity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar un element");
        builder.setMessage("Segur que voleu eliminar \"" + elem + "\"?");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ElementDBHelper helper = new ElementDBHelper(Element.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                // Eliminam els elements de la categoria
                db.delete(ElementContract.TABLE,
                        ElementContract.Columns.ITEM + "= ?",
                        new String[]{elem});

                String msg = "S'ha eliminat l'element \"" + elem + "\"";
                Toast.makeText(currentActivity, msg, Toast.LENGTH_SHORT).show();
                updateElementsUI();
            }
        });

        builder.setNegativeButton("Cancelar",null);

        builder.create().show();
    }

    /**
     * Es crida quan es pitja la imatge
     * @param view
     */
    public void onSetImageClick(View view) {
        View v = (View) view.getParent();
        elementImage = (ImageButton)v.findViewById(R.id.imageButton);
        TextView taskTextView = (TextView) v.findViewById(R.id.elementTextView);
        imageTag = taskTextView.getText().toString();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageButton imageView = elementImage;
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            storeImg(picturePath);
        }
    }

    private void storeImg(String path) {
        ElementDBHelper helper = new ElementDBHelper(Element.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        // Actualitzam el producte amb la nova imatge
        ContentValues cv = new ContentValues();
        cv.put(ElementContract.Columns.IMAGE, path);
        db.update(ElementContract.TABLE,
                cv,
                ElementContract.Columns.ITEM + "= ?",
                new String[]{imageTag});
    }
    /*protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case REQUEST_CODE_PICK_FILE:
                Log.d("Log", "here");
                if(resultCode == RESULT_OK){
                    Log.d("Log", "here ok: " + imageReturnedIntent.toString());
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] path = selectedImage.toString().split("content");
                    String realPath = path[path.length - 1];
                    realPath = realPath.replace("%3A", "");
                    realPath = realPath.replace("%2F", "/");
                    realPath = realPath.replace("/ACTUAL", "");
                    realPath = "/" + realPath;
                    Log.d("Uri", realPath);
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                        storeImg(imageStream, realPath);

                        Resources res = getResources();
                        Bitmap bitmap = BitmapFactory.decodeFile("//media/external/images/media/22441");
                        BitmapDrawable bd = new BitmapDrawable(res, bitmap);
                        elementImage.setBackgroundDrawable(bd);
                    } catch (FileNotFoundException fe) {
                            Log.w("Log", "Not found");
                    }
                }
        }
    }*/
}
