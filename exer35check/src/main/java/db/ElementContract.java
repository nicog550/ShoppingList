package db;

import android.provider.BaseColumns;

public class ElementContract {
    public static final String DB_NAME = "ltim.master.practica";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "elements";

    public class Columns {
        public static final String ITEM = "element";
        public static final String CATEGORY = "category";
        public static final String IMAGE = "image";
        public static final String _ID = BaseColumns._ID;
    }
}
