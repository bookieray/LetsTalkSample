package brainwind.letstalksample.data.database.user.interests;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {UserInterest.class}, version = 1)
public abstract class UserInterestDatabase extends RoomDatabase
{

    public static String DB_NAME="user_interests_db2";
    public abstract UserInterestDao userInterestDao();
    public static UserInterestDatabase instance;

    private static volatile UserInterestDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserInterestDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserInterestDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserInterestDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
