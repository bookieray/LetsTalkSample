package brainwind.letstalksample.data.database.user.activity;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserActivity.class}, version = 1)
public abstract class UserActivityDatabase extends RoomDatabase
{

    public static String DB_NAME="useractivity_db";
    public abstract UserActivityDao userActivityDao();
    public static UserActivityDatabase instance;

    private static volatile UserActivityDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static UserActivityDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserActivityDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserActivityDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
