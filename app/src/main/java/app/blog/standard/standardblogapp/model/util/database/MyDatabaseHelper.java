package app.blog.standard.standardblogapp.model.util.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author victor
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext; //Usar para strings localizadas (caso precise de nomes diferentes para a .db

    //Publications table
    public static final String TABLE_PUBLICATIONS = "publications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_DATE = "date";

    //Categories Table
    public static final String TABLE_CATEGORIES_PER_POST = "categoriespost";
    public static final String COLUMN_POST_ID = "postId";
    public static final String COLUMN_CATEGORY = "category";

    private static final String DB_NAME = "publications.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE `"+
            TABLE_CATEGORIES_PER_POST+"` (" +
            "  `"+COLUMN_POST_ID+"` INTEGER REFERENCES " + TABLE_PUBLICATIONS + "("+ COLUMN_ID + ")," +
            "  `"+COLUMN_CATEGORY+"` TEXT" +
            ");";

    private static final String CREATE_TABLE_PUBLICATIONS = "CREATE TABLE `"
            +TABLE_PUBLICATIONS+"` (" +
            "  `"+COLUMN_ID+"` INTEGER PRIMARY KEY," +
            "  `"+COLUMN_TITLE+"` TEXT," +
            "  `"+COLUMN_URL+"` TEXT," +
            "  `"+COLUMN_AUTHOR+"` TEXT," +
            "  `"+COLUMN_DESCRIPTION+"` TEXT," +
            "  `"+COLUMN_CONTENT+"` TEXT," +
            "  `"+COLUMN_DATE+"` DATETIME" +
            ");";;


    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORIES);
        sqLiteDatabase.execSQL(CREATE_TABLE_PUBLICATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES_PER_POST);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PUBLICATIONS);

        // create new tables
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //    @Override
//    public void onOpen(SQLiteDatabase db) {
//        super.onOpen(db);
//
//        if(!db.isReadOnly()) {
//            db.execSQL("PRAGMA foreign_keys=ON;");
//        }
//    }
}
