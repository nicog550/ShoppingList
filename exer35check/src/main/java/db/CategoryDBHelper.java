package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CategoryDBHelper extends SQLiteOpenHelper {

    public CategoryDBHelper(Context context) {
        super(context, CategoriesContract.DB_NAME, null, CategoriesContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sqlQuery =
                String.format("CREATE TABLE IF NOT EXISTS %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT)", CategoriesContract.TABLE,
                        CategoriesContract.Columns.CATEGORY);

        Log.d("CategoryDBHelper","Query to form table: "+sqlQuery);
        sqlDB.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
        sqlDB.execSQL("DROP TABLE IF EXISTS "+CategoriesContract.TABLE);
        onCreate(sqlDB);
    }
}
