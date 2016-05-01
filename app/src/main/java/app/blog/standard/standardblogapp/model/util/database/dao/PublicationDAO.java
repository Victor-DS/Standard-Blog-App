package app.blog.standard.standardblogapp.model.util.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import app.blog.standard.standardblogapp.model.Publication;
import app.blog.standard.standardblogapp.model.util.Util;
import app.blog.standard.standardblogapp.model.util.database.MyDatabaseHelper;

/**
 * @author victor
 */
public class PublicationDAO implements DAO<Publication> {

    //region variables
    private SQLiteDatabase database;
    private MyDatabaseHelper dbHelper;
    private String[] allColumns = { MyDatabaseHelper.COLUMN_ID,
            MyDatabaseHelper.COLUMN_TITLE, MyDatabaseHelper.COLUMN_URL,
            MyDatabaseHelper.COLUMN_AUTHOR, MyDatabaseHelper.COLUMN_DESCRIPTION,
            MyDatabaseHelper.COLUMN_CONTENT, MyDatabaseHelper.COLUMN_DATE };
    private Context mContext;
    private CategoriesDAO categoriesDAO;
    //endregion

    //region Singleton
    private static PublicationDAO ourInstance;

    public static PublicationDAO getInstance(Context mContext) {
        if(ourInstance == null)
            ourInstance = new PublicationDAO(mContext);

        return ourInstance;
    }

    private PublicationDAO(Context mContext) {
        dbHelper = new MyDatabaseHelper(mContext);
        categoriesDAO = CategoriesDAO.getInstance(mContext);
        this.mContext = mContext;
    }
    //endregion

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        categoriesDAO.open();
    }

    public void close() {
        categoriesDAO.close();
        dbHelper.close();
    }

    @Override
    public boolean create(Publication publication) {
        ContentValues values = new ContentValues();
        values.put(MyDatabaseHelper.COLUMN_TITLE, publication.getTitle());
        values.put(MyDatabaseHelper.COLUMN_AUTHOR, publication.getCreator());
        values.put(MyDatabaseHelper.COLUMN_CONTENT, publication.getContent());
        values.put(MyDatabaseHelper.COLUMN_DATE, publication.getTimestamp());
        values.put(MyDatabaseHelper.COLUMN_DESCRIPTION, publication.getDescription());
        values.put(MyDatabaseHelper.COLUMN_URL, publication.getUrl());

        long insertId = database.insert(MyDatabaseHelper.TABLE_PUBLICATIONS, null, values);

        categoriesDAO.insertAllCategories(insertId, publication.getCategory());

        return insertId > -1;
    }

    public boolean createAll(ArrayList<Publication> publications) {
        while (!publications.isEmpty())
            create(publications.remove(0));

        return true;
    }

    @Override
    public Publication read(Publication publication) {
        return null;
    }

    @Override
    public ArrayList<Publication> read(int skip, int take) {
        ArrayList<Publication> publications = new ArrayList<>();

        Cursor cursor = database.query(MyDatabaseHelper.TABLE_PUBLICATIONS,
                allColumns, null, null, null, null, MyDatabaseHelper.COLUMN_ID + " DESC",
                skip + ", " + take);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            publications.add(cursorToPublication(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return publications;
    }

    @Override
    public ArrayList<Publication> readAll() {
        return readAll(false);
    }

    public ArrayList<Publication> readAll(boolean includeAds) {
        ArrayList<Publication> publications = new ArrayList<>();

        Cursor cursor = database.query(MyDatabaseHelper.TABLE_PUBLICATIONS,
                allColumns, null, null, null, null, MyDatabaseHelper.COLUMN_ID + " DESC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Publication publication = cursorToPublication(cursor);
            if(includeAds || !publication.isPatrocinated())
                publications.add(publication);
            cursor.moveToNext();
        }

        cursor.close();

        return publications;
    }

    public Publication readLatest() {
        Publication publication = null;
        Cursor cursor = database.query(MyDatabaseHelper.TABLE_PUBLICATIONS,
                allColumns, null, null, null, null, MyDatabaseHelper.COLUMN_ID + " DESC", "1");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            publication = cursorToPublication(cursor);
            cursor.moveToNext();
        }

        cursor.close();

        return publication;
    }

    public Publication readById(int i) {
        Publication publication = null;
        Cursor cursor = database.query(MyDatabaseHelper.TABLE_PUBLICATIONS,
                allColumns, MyDatabaseHelper.COLUMN_ID+"=?", new String[] {i+""}, null, null,
                MyDatabaseHelper.COLUMN_ID + " DESC", "1");

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            publication = cursorToPublication(cursor);
            cursor.moveToNext();
        }

        cursor.close();

        return publication;
    }

    public ArrayList<Publication> readAllById(ArrayList<Integer> ids) {
        ArrayList<Publication> publications = new ArrayList<>();

        while(!ids.isEmpty())
            publications.add(readById(ids.remove(0)));

        return publications;
    }

    public ArrayList<Publication> readAllByCategory(String category) { //FIXME Tudo errado.
        ArrayList<Publication> publications = new ArrayList<>();
        final String whereClause = MyDatabaseHelper.TABLE_PUBLICATIONS + "." +
                MyDatabaseHelper.COLUMN_ID + "=";
        final String whereEqualTo = MyDatabaseHelper.TABLE_CATEGORIES_PER_POST
                + "." + MyDatabaseHelper.COLUMN_POST_ID;

        String[] columns = allColumns;
        columns = Util.append(columns, MyDatabaseHelper.TABLE_CATEGORIES_PER_POST + "." +
                MyDatabaseHelper.COLUMN_POST_ID);

        //SELECT columns FROM the two tables WHERE

        Cursor cursor = database.query(MyDatabaseHelper.TABLE_PUBLICATIONS+ ", " +
                MyDatabaseHelper.TABLE_CATEGORIES_PER_POST,
                columns, whereClause + whereEqualTo, null, null, null,
                MyDatabaseHelper.COLUMN_ID + " DESC", "1"); //FIXME Gambiarra!

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            publications.add(cursorToPublication(cursor));
            cursor.moveToNext();
        }

        cursor.close();


        return publications;
    }

    @Override
    public boolean update(Publication publication) {
        return false;
    }

    @Override
    public boolean delete(Publication publication) {
        database.delete(MyDatabaseHelper.TABLE_PUBLICATIONS, MyDatabaseHelper.COLUMN_TITLE
                + " = " + publication.getTitle(), null);
        return true;
    }

    private Publication cursorToPublication(Cursor cursor) {
        Publication publication = new Publication();
        publication.setCategory(categoriesDAO.readAllCategoriesForPublication(cursor.getInt(0)));
        publication.setTitle(cursor.getString(1));
        publication.setUrl(cursor.getString(2));
        publication.setCreator(cursor.getString(3));
        publication.setDescription(cursor.getString(4));
        publication.setContent(cursor.getString(5));
        publication.setDateFromTimestamp(cursor.getString(6));
        return publication;
    }
}
