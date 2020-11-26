package inesc_id.pt.motivandroid.deprecated;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;

import inesc_id.pt.motivandroid.data.tripData.FullTrip;

/**
 * Created by admin on 1/4/18.
 */

@Deprecated
public class TripPersistenceManager {

    private Context mContext;
    private static TripPersistenceManager MANAGER = null;

    private TripPersistenceManager(Context context){
        mContext = context;
    }

    /**
     * Fetches an instance of the TripPersistenceManager singleton.
     * @param context
     * @return An instance of the TripPersistenceManager singleton.
     */
    public static TripPersistenceManager getInstance(Context context){

        synchronized (TripPersistenceManager.class){
            if(MANAGER == null)
                MANAGER = new TripPersistenceManager(context);
        }

        return MANAGER;
    }

    private void saveFullTripData(FullTrip fullTrip){

        String tripID = fullTrip.getInitTimestamp()+""+fullTrip.getEndTimestamp();

        Gson gson = new Gson();
        String json = gson.toJson(fullTrip);



        //prefsEditor.putString("SerializableObject", json);

        //SharedPreferences.Editor editor =
        //        mContext.getSharedPreferences(Constants.SETTINGS_PREFERENCES, Context.MODE_PRIVATE).edit();



    }

    private interface Constants {

    }

    //see https://www.androidauthority.com/use-sqlite-store-data-app-599743/

    private class TripDBHelper extends SQLiteOpenHelper{

        public static final String DATABASE_NAME = "TripLocalDB.db";
        private static final int DATABASE_VERSION = 1;
        public static final String TRIP_TABLE_NAME = "TripsTable";
        public static final String TRIP_COLUMN_ID = "_id";
        public static final String TRIP_COLUMN_DATA = "data";

        public TripDBHelper(Context context) {
            super(context, DATABASE_NAME , null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TRIP_TABLE_NAME + "(" +
                    TRIP_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    TRIP_COLUMN_DATA + " TEXT)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE_NAME);
            onCreate(db);
        }

        public boolean insertFullTrip(String data) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TRIP_COLUMN_DATA, data);
            db.insert(TRIP_TABLE_NAME, null, contentValues);
            return true;
        }

        public Cursor getFullTrip(int id) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery( "SELECT * FROM " + TRIP_TABLE_NAME + " WHERE " +
                    TRIP_COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
            return res;
        }

        public Cursor getAllFullTrips() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery( "SELECT * FROM " + TRIP_TABLE_NAME, null );
            return res;
        }

    }

}
