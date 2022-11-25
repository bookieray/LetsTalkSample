package brainwind.letstalksample.data.database.org;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Org.class}, version = 1)
public abstract class OrgDatabase extends RoomDatabase
{

    public static String DB_NAME="org_db1";
    public abstract OrgDao orgDao();
    public static OrgDatabase instance;

    private static volatile OrgDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static OrgDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (OrgDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            OrgDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
