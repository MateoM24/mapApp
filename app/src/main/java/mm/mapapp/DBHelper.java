package mm.mapapp;
/*** Created by Mateusz on 2016-12-30.*/
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME="DataBase";
    private static final int DB_VERSION=1;
    private static final String TablePlaces ="VisitedPlaces";

    DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override  //gdy BD po raz pierwszy jest tworzona
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE "+ TablePlaces +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "NAME TEXT UNIQUE, "+
                "DESCRIPTION TEXT," +
                "LATITUDE NUMBER," +
                "LONGITUDE NUMBER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }


    public static boolean insert(SQLiteDatabase db, String name, String desc, double latitude, double longitude)throws SQLiteConstraintException {
        ContentValues values=new ContentValues();
        values.put("NAME",name);
        values.put("DESCRIPTION",desc);
        values.put("LATITUDE",latitude);
        values.put("LONGITUDE",longitude);
        long success=db.insert(TablePlaces,null,values);
        if(success==-1) throw new SQLiteConstraintException("Product already exists");
        return success!=-1;
    }

    public static int deleteWholeList(SQLiteDatabase db){
        int success=db.delete(TablePlaces,null,null);
        return success;
    }
    public static Cursor getAllPlaces(SQLiteDatabase db){
        Cursor cursor=db.query(TablePlaces,null,null,null,null
                ,null,null);
        return cursor;
    }
}
