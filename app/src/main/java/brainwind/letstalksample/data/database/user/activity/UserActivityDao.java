package brainwind.letstalksample.data.database.user.activity;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


@Dao
public interface UserActivityDao
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(UserActivity... userActivities);

    @Delete
    void delete(UserActivity userActivity);

    @Query("SELECT * FROM UserActivity order by id desc limit 1")
    UserActivity getLastUserActivity();

    @Query("SELECT * FROM UserActivity where activity_type=:type order by id desc limit 1")
    UserActivity getLastUserActivityOfType(int type);

    @Query("DELETE FROM UserActivity")
    void deleteAll();

}
