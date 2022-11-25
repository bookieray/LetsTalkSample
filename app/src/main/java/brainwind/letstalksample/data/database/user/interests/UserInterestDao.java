package brainwind.letstalksample.data.database.user.interests;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserInterestDao
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(UserInterest... userInterests);

    @Delete
    void delete(UserInterest userInterest);

    @Query("DELETE FROM UserInterest")
    void deleteAll();

    @Update
    public void updateOrgs(UserInterest... userInterests);

    @Query("SELECT * FROM UserInterest order by uid asc")
    List<UserInterest> getAll();

    @Query("SELECT count(*) FROM UserInterest where uid=:uid order by uid asc")
    int countUserInterests(String uid);

    @Query("SELECT * FROM UserInterest where uid=:uid order by num_searches desc limit :limit")
    List<UserInterest> getUserInterests(String uid,int limit);

    @Query("SELECT interest_title FROM UserInterest where uid=:uid order by num_searches desc limit :limit")
    List<String> getUserInterestsStr(String uid,int limit);

    @Query("SELECT * FROM UserInterest where id=:interest_id limit 1")
    UserInterest getUserInterest(String interest_id);

    @Query("SELECT * FROM UserInterest order by num_searches asc limit 1")
    UserInterest getLowestUserInterests();

    @Query("SELECT * FROM UserInterest where interest_title!=:except_this_one order by num_searches asc limit 1")
    UserInterest getLowestUserInterests(String except_this_one);

    @Query("SELECT * FROM UserInterest where uid=:uid and interest_title like '%' || :searchterm || '%' order by num_searches desc limit 10")
    List<UserInterest> checkConflicts(String uid,String searchterm);

    @Query("SELECT * FROM UserInterest GROUP BY interest_title limit :limit")
    List<UserInterest> getDuplicates(int limit);



}
