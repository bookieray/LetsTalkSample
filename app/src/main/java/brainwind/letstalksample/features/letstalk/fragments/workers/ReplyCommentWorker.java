package brainwind.letstalksample.features.letstalk.fragments.workers;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import brainwind.letstalksample.CommentListener;
import brainwind.letstalksample.R;
import brainwind.letstalksample.SwipeControllerActions;
import brainwind.letstalksample.features.letstalk.fragments.item.Comment;
import brainwind.letstalksample.utils.AndroidUtils;

public class ReplyCommentWorker
{


    Fragment currentFragment;
    CommentWorker commentWorker;
    ItemTouchHelper itemTouchHelper;

    public ReplyCommentWorker(CommentWorker commentWorker) {
        this.commentWorker = commentWorker;
        this.currentFragment = commentWorker.currentTopicForConvo;
        if(this.currentFragment!=null)
        {
            messageSwipeController=new MessageSwipeController(currentFragment.getActivity(),
                    new SwipeControllerActions() {
                        @Override
                        public void showReplyUI(int position) {
                            quotedMessagePos = position;
                            Comment comment=commentWorker.commentAdapter
                                    .commentListUnderHeadComment.get(position);
                            showQuotedMessage(comment);
                        }
                    });
        }

    }

    private int quotedMessagePos;
    private void showQuotedMessage(Comment comment)
    {

        CommentListener commentListener=(CommentListener) currentFragment.getActivity();
        if(commentListener!=null)
        {
            commentListener.onReply(comment);
        }

    }

    class MessageSwipeController extends ItemTouchHelper.Callback {

        private Drawable imageDrawable;
        private Drawable shareRound;
        private RecyclerView.ViewHolder currentItemViewHolder=null;
        private View mView;
        private float dX=0f;
        private float replyButtonProgress=0;
        private long lastReplyButtonAnimationTime=0;
        private boolean swipeBack=false;
        private boolean isVibrate=false;
        private boolean startTracking=false;
        private SwipeControllerActions swipeControllerActions;
        private Context context;

        MessageSwipeController(Context context, SwipeControllerActions swipeControllerActions)
        {
            this.context=context;
            this.swipeControllerActions=swipeControllerActions;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            //mView = viewHolder.itemView
            mView=viewHolder.itemView;
            imageDrawable=currentFragment.getActivity().getDrawable(R.drawable.ic_reply_black_24dp);
            imageDrawable.setBounds(0, 0, 100, 100);
            shareRound=currentFragment.getActivity().getDrawable(R.drawable.ic_round_shape);



            return ItemTouchHelper.Callback.makeMovementFlags(ACTION_STATE_IDLE, RIGHT);
        }

        /*
         override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
         */
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        //override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        /*
            override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
                if (swipeBack) {
                    swipeBack = false
                    return 0
                }
                return super.convertToAbsoluteDirection(flags, layoutDirection)
            }
         */
        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            if (swipeBack)
            {
                swipeBack = false;
                return 0;
            }
            return super.convertToAbsoluteDirection(flags, layoutDirection);

        }

        /*
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {

                if (actionState == ACTION_STATE_SWIPE) {
                    setTouchListener(recyclerView, viewHolder)
                }

                if (mView.translationX < convertTodp(130) || dX < this.dX) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    this.dX = dX
                    startTracking = true
                }
                currentItemViewHolder = viewHolder
                drawReplyButton(c)
            }
         */

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (actionState == ACTION_STATE_SWIPE)
            {
                setTouchListener(recyclerView, viewHolder);
            }
            if (mView.getTranslationX() < convertTodp(130) || dX < this.dX)
            {
                super.onChildDraw(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive);
                this.dX = dX;
                startTracking = true;
            }
            currentItemViewHolder = viewHolder;
            drawReplyButton(c);

        }

        @SuppressLint("ClickableViewAccessibility")
        private void setTouchListener(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder)
        {

            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    swipeBack = motionEvent.getAction() == MotionEvent.ACTION_CANCEL
                            || motionEvent.getAction() == MotionEvent.ACTION_UP;
                    if (swipeBack)
                    {
                        if (Math.abs(mView.getTranslationX()) >= convertTodp(100))
                        {
                            swipeControllerActions.showReplyUI(viewHolder.getBindingAdapterPosition());
                        }
                    }
                    return false;
                }
            });

        }

        private void drawReplyButton(Canvas canvas)
        {

            if (currentItemViewHolder == null) {
                return;
            }

            //val translationX = mView.translationX
            float translationX=mView.getTranslationX();
            //val newTime = System.currentTimeMillis()
            long newTime = System.currentTimeMillis();
            //val dt = Math.min(17, newTime - lastReplyButtonAnimationTime)
            long dt=Math.min(17,newTime-lastReplyButtonAnimationTime);
            //lastReplyButtonAnimationTime = newTime
            lastReplyButtonAnimationTime = newTime;
            //val showing = translationX >= convertTodp(30)
            boolean showing = translationX >= convertTodp(30);
            if (showing)
            {
                if (replyButtonProgress < 1.0f)
                {
                    replyButtonProgress += dt / 180.0f;
                    if (replyButtonProgress > 1.0f)
                    {
                        replyButtonProgress = 1.0f;
                    }
                    else
                    {
                        mView.invalidate();
                    }
                }
            }
            else if (translationX <= 0.0f)
            {

                replyButtonProgress = 0f;
                startTracking = false;
                isVibrate = false;

            }
            else
            {

                if (replyButtonProgress > 0.0f)
                {
                    replyButtonProgress -= dt / 180.0f;
                    if (replyButtonProgress < 0.1f)
                    {
                        replyButtonProgress = 0f;

                    }
                    else
                    {
                        mView.invalidate();
                    }
                }

            }

            //val alpha: Int
            int alpha;
            //val scale: Float
            float scale;
            if (showing)
            {
                if (replyButtonProgress <= 0.8f)
                {
                    scale=1.2f * (replyButtonProgress / 0.8f);
                }
                else
                {
                    scale=1.2f - 0.2f * ((replyButtonProgress - 0.8f) / 0.2f);
                }
                alpha = (int)Math.min(255f, 255 * (replyButtonProgress / 0.8f));

            }
            else
            {
                scale = replyButtonProgress;
                alpha = (int)Math.min(255f, 255 * replyButtonProgress);
            }

            shareRound.setAlpha(alpha);
            imageDrawable.setAlpha(alpha);
            if (startTracking)
            {
                if (!isVibrate && mView.getTranslationX() >= convertTodp(100))
                {
                    mView.performHapticFeedback(
                            HapticFeedbackConstants.KEYBOARD_TAP,
                            HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    isVibrate = true;
                }
            }

            /*
            val x: Int = if (mView.translationX > convertTodp(130)) {
                convertTodp(130) / 2
            } else {
                (mView.translationX / 2).toInt()
            }
             */
            int x;
            if (mView.getTranslationX() > convertTodp(130))
            {
                x=convertTodp(130) / 2;
            }
            else
            {
                x=(int)(mView.getTranslationX() / 2);
            }
            //val y = (mView.top + mView.measuredHeight / 2).toFloat()
            float y=Float.valueOf(mView.getTop() + mView.getMeasuredHeight() / 2);
            //shareRound.colorFilter =
            //            PorterDuffColorFilter(ContextCompat.getColor(context,
            //            R.color.colorE),
            //            PorterDuff.Mode.MULTIPLY)
            shareRound.setColorFilter(new PorterDuffColorFilter(currentFragment.getActivity().getResources().getColor(R.color.colorE)
                    , PorterDuff.Mode.MULTIPLY));
            shareRound.setBounds(
                    (int)(x - convertTodp(18) * scale),
                    (int)(y - convertTodp(18) * scale),
                    (int)(x + convertTodp(50) * scale),
                    (int)(y + convertTodp(50) * scale)
            );
            shareRound.draw(canvas);
            imageDrawable.setBounds(
                    (int)(x - convertTodp(12) * scale),
                    (int)(y - convertTodp(11) * scale),
                    (int)(x + convertTodp(40) * scale),
                    (int)(y + convertTodp(38) * scale)
            );
            imageDrawable.draw(canvas);
            shareRound.setAlpha(255);
            imageDrawable.setAlpha(255);

        }
        /*
            private fun convertTodp(pixel: Int): Int {
                return AndroidUtils.dp(pixel.toFloat(), context)
            }
         */
        private int convertTodp(int pixel)
        {
            return AndroidUtils.dp(Float.valueOf(pixel), context);
        }


    }

    MessageSwipeController messageSwipeController=null;
    public void attachReplySwitch(RecyclerView comment_list)
    {

        if(comment_list!=null)
        {
            itemTouchHelper=new ItemTouchHelper(messageSwipeController);
            itemTouchHelper.attachToRecyclerView(comment_list);
        }

    }


}
