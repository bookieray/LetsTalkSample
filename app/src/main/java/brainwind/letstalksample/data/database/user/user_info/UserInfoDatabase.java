package brainwind.letstalksample.data.database.user.user_info;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {UserInfo.class}, version = 1)
public abstract class UserInfoDatabase extends RoomDatabase
{

    public static String DB_NAME="user_info_db5";
    public abstract UserInfoDao userInfoDao();
    public static UserInfoDatabase instance;

    private static volatile UserInfoDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static UserInfoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserInfoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserInfoDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
