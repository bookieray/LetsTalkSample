package brainwind.letstalksample.data.database.user.interests;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserInterest
{

    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "uid")
    public String uid;

    @ColumnInfo(name = "interest_title")
    public String interest_title;

    @ColumnInfo(name = "interest_description")
    public String interest_description;

    @ColumnInfo(name = "country")
    public String country;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "num_searches",defaultValue = "0")
    public int num_searches;


}
