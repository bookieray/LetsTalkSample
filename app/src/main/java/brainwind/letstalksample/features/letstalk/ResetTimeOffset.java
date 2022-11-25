package brainwind.letstalksample.features.letstalk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.exoplayer2.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import brainwind.letstalksample.data.database.OrgFields;
import brainwind.letstalksample.data.memory.Memory;

public class ResetTimeOffset extends Worker
{


    public ResetTimeOffset(@NonNull Context context,
                           @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork()
    {

        Memory memory=new Memory(getApplicationContext());
        memory.Save(OrgFields.SERVER_TIME_OFFSET,"-1");

        Log.i("ResetTimeOffset","reseted to -1");

        DatabaseReference offsetRef =
                FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                android.util.Log.w("ResetTimeOffset", "offset="+offset
                        +" estimatedServerTimeMs="+estimatedServerTimeMs);
                org.joda.time.LocalDateTime localDateTime
                        =new org.joda.time.LocalDateTime(estimatedServerTimeMs);
                android.util.Log.w("ResetTimeOffset","day="+localDateTime.getDayOfMonth()
                        +" month="+localDateTime.getMonthOfYear()
                        +" year="+localDateTime.getYear()
                        +" hour="+localDateTime.getHourOfDay());

                Memory memory=new Memory(getApplicationContext());
                String jnm=memory.getString(OrgFields.SERVER_TIME_OFFSET);
                long localtimeoffset=Long.parseLong(jnm);
                if(localtimeoffset==-1)
                {
                    memory.Save(OrgFields.SERVER_TIME_OFFSET,String.valueOf(localtimeoffset));
                }
                else
                {
                    memory.Save(OrgFields.SERVER_TIME_OFFSET,"-1");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                android.util.Log.i("ResetTimeOffset", "Listener was cancelled");
            }



        });

        return Result.success();

    }


}
