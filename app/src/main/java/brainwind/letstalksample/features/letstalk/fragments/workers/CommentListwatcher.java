package brainwind.letstalksample.features.letstalk.fragments.workers;

import android.graphics.Rect;
import android.util.Log;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import brainwind.letstalksample.features.letstalk.fragments.adapters.CommentAdapter;

public class CommentListwatcher
{

    public CommentListwatcher()
    {

    }

    boolean isKeyboardShowing=false;
    public void watch(RecyclerView comment_list, CommentAdapter commentAdapter)
    {

        if(comment_list!=null&commentAdapter!=null)
        {
            try {

                comment_list.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {

                                Rect r = new Rect();
                                comment_list.getWindowVisibleDisplayFrame(r);
                                int screenHeight = comment_list.getRootView().getHeight();

                                // r.bottom is the position above soft keypad or device button.
                                // if keypad is shown, the r.bottom is smaller than that before.
                                int keypadHeight = screenHeight - r.bottom;

                                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                                    // keyboard is opened
                                    if (!isKeyboardShowing) {
                                        isKeyboardShowing = true;
                                        LinearLayoutManager ln=(LinearLayoutManager)comment_list.getLayoutManager();


                                    }
                                }
                                else {
                                    // keyboard is closed
                                    if (isKeyboardShowing) {
                                        isKeyboardShowing = false;
                                        commentAdapter.notifyDataSetChanged();
                                        if(commentAdapter.last_reading_pos>0)
                                        {

                                            //comment_list.scrollToPosition(commentAdapter.last_reading_pos);
                                            comment_list.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                    //comment_list.smoothScrollToPosition(commentAdapter.last_reading_pos);
                                                    Log.i("keyposa","last_reading_pos="+commentAdapter.last_reading_pos);

                                                }
                                            },1000);
                                        }
                                    }
                                }

                            }
                        });


            }
            catch(Exception exception)
            {

            }
        }

    }



}
