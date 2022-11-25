package brainwind.letstalksample.data.database.user.activity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserActivity
{

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "activity_type")
    public int actvity_type;


}
