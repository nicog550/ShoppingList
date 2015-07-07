package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ElementDBHelper extends SQLiteOpenHelper {

    public ElementDBHelper(Context context) {
        super(context, ElementContract.DB_NAME, null, ElementContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sqlQuery =
                String.format("CREATE TABLE IF NOT EXISTS %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT," +
                                "%s TEXT," +
                                "%s TEXT)", ElementContract.TABLE, ElementContract.Columns.ITEM,
                        ElementContract.Columns.CATEGORY, ElementContract.Columns.IMAGE);

        sqlDB.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int i, int i2) {
        sqlDB.execSQL("DROP TABLE IF EXISTS "+ElementContract.TABLE);
        onCreate(sqlDB);
    }
}
