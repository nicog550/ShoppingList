package db;

import android.provider.BaseColumns;

public class CategoriesContract {
    public static final String DB_NAME = "ltim.master.practica";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "categories";

    public class Columns {
        public static final String CATEGORY = "CATEGORY";
        public static final String _ID = BaseColumns._ID;
    }
}
