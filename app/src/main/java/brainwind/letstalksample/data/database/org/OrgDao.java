package brainwind.letstalksample.data.database.org;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OrgDao
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Org... orgs);

    @Delete
    void delete(Org org);

    @Update
    public void updateOrgs(Org... orgs);

    @Query("SELECT * FROM Org order by orgid asc")
    List<Org> getAll();

    @Query("SELECT * FROM Org where owner=:uid order by orgid asc")
    List<Org> getAllForUser(String uid);

    @Query("SELECT count(*) FROM Org where owner=:uid")
    int countOrgsForUser(String uid);

    @Query("SELECT * FROM Org where orgid=:orgid order by orgid asc limit 1")
    Org getOrg(String orgid);

    @Query("DELETE FROM Org")
    void deleteAll();


}
