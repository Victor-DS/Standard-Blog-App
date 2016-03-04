package app.blog.standard.standardblogapp.model.util.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.database.MyDatabaseHelper;

/**
 * @author victor
 */
public class CategoriesDAO {
    //region variables
    private SQLiteDatabase database;
    private MyDatabaseHelper dbHelper;
    private String[] allColumns = { MyDatabaseHelper.COLUMN_POST_ID,
            MyDatabaseHelper.COLUMN_CATEGORY };

    private static CategoriesDAO ourInstance;
    //endregion variables

    //region Singleton
    public static CategoriesDAO getInstance(Context mContext) {
        if(ourInstance == null)
            ourInstance = new CategoriesDAO(mContext);

        return ourInstance;
    }

    private CategoriesDAO(Context mContext) {
        dbHelper = new MyDatabaseHelper(mContext);
    }
    //endregion

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insert(long publicationId, String category) {
        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_POST_ID, publicationId);
        values.put(MyDatabaseHelper.COLUMN_CATEGORY, category);

        long insertId = database.insert(MyDatabaseHelper.TABLE_CATEGORIES_PER_POST, null, values);

        return insertId > -1;
    }

    public boolean insertAllCategories(long publicationId, String[] categories) {
        for(int index = 0; index < categories.length; index++)
            insert(publicationId, categories[index]);

        return true;
    }

    public String[] readAllCategories() {
        ArrayList<String> categories = new ArrayList<>();

        Cursor cursor = database.query(true, MyDatabaseHelper.TABLE_CATEGORIES_PER_POST,
                new String[] {MyDatabaseHelper.COLUMN_CATEGORY},
                null, null, null, null, MyDatabaseHelper.COLUMN_POST_ID + " DESC", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            categories.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();

        String[] aCategories = new String[categories.size()];
        aCategories = categories.toArray(aCategories);

        return aCategories;
    }

    public ArrayList<Integer> readAllPublicationsByCategory(String category) {
        ArrayList<Integer> publicationsId = new ArrayList<>();

        Cursor cursor = database.query(MyDatabaseHelper.TABLE_CATEGORIES_PER_POST,
                allColumns, null, null, category, null, MyDatabaseHelper.COLUMN_POST_ID + " DESC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            publicationsId.add(cursor.getInt(0));
            cursor.moveToNext();
        }

        cursor.close();

        return publicationsId;
    }

    public ArrayList<Integer> readAllPublicationsByCategory(String category,
                                                                int skip, int take) {
        ArrayList<Integer> publicationsId = new ArrayList<>();

        Cursor cursor = database.query(MyDatabaseHelper.TABLE_CATEGORIES_PER_POST,
                allColumns, MyDatabaseHelper.COLUMN_CATEGORY+"=?", new String[] {category}, null,
                null, MyDatabaseHelper.COLUMN_POST_ID + " DESC",
                skip + ", " + take);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            publicationsId.add(cursor.getInt(0));
            cursor.moveToNext();
        }

        cursor.close();

        return publicationsId;
    }

}
