package com.angcyo.recyclerlayoutmanager;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

import static android.R.attr.x;
import static com.angcyo.recyclerlayoutmanager.OverLayCardLayoutManager.MAX_SHOW_COUNT;
import static com.angcyo.recyclerlayoutmanager.OverLayCardLayoutManager.SCALE_GAP;
import static com.angcyo.recyclerlayoutmanager.OverLayCardLayoutManager.TRANS_Y_GAP;

/**
 * 介绍：人人影视效果的Callback
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 16/12/18.
 */

public class RenRenCallback extends ItemTouchHelper.SimpleCallback {

    private static final String TAG = "RenRen";
    private static final int MAX_ROTATION = 15;
    OnSwipeListener mSwipeListener;
    boolean isSwipeAnim = false;

    public RenRenCallback() {
        super(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    public void setSwipeListener(OnSwipeListener swipeListener) {
        mSwipeListener = swipeListener;
    }

    //水平方向是否可以被回收掉的阈值
    public float getThreshold(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //2016 12 26 考虑 探探垂直上下方向滑动，不删除卡片，这里参照源码写死0.5f
        return recyclerView.getWidth() * /*getSwipeThreshold(viewHolder)*/ 0.5f;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        viewHolder.itemView.setRotation(0);//恢复最后一次的旋转状态
        if (mSwipeListener != null) {
            mSwipeListener.onSwipeTo(viewHolder, 0);
        }
        notifyListener(viewHolder.getAdapterPosition(), direction);
    }

    private void notifyListener(int position, int direction) {
        Log.w(TAG, "onSwiped: " + position + " " + direction);
        if (mSwipeListener != null) {
            mSwipeListener.onSwiped(position, direction);
        }
    }

    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        //滑动的比例达到多少之后, 视为滑动
        return 0.3f;
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        Log.i(TAG, "onChildDraw: dx:" + dX + " dy:" + dY);
        //人人影视的效果
        //if (isCurrentlyActive) {
        //先根据滑动的dxdy 算出现在动画的比例系数fraction
        float swipeValue = (float) Math.sqrt(dX * dX + dY * dY);
        final float threshold = getThreshold(recyclerView, viewHolder);
        float fraction = swipeValue / threshold;
        //边界修正 最大为1
        if (fraction > 1) {
            fraction = 1;
        } else if (fraction < -1) {
            fraction = -1;
        }
        //对每个ChildView进行缩放 位移
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            //第几层,举例子，count =7， 最后一个TopView（6）是第0层，
            int level = childCount - i - 1;
            if (level > 0) {
                child.setScaleX(1 - SCALE_GAP * level + fraction * SCALE_GAP);

                if (level < MAX_SHOW_COUNT - 1) {
                    child.setScaleY(1 - SCALE_GAP * level + fraction * SCALE_GAP);
                    child.setTranslationY(TRANS_Y_GAP * level - fraction * TRANS_Y_GAP);
                } else {
                    //child.setTranslationY((float) (mTranslationYGap * (level - 1) - fraction * mTranslationYGap));
                }
            } else {
                //最上层
                //rotate
                if (dX < -50) {
                    child.setRotation(-fraction * MAX_ROTATION);
                } else if (dX > 50) {
                    child.setRotation(fraction * MAX_ROTATION);
                } else {
                    child.setRotation(0);
                }

                if (mSwipeListener != null) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                    final int adapterPosition = params.getViewAdapterPosition();
                    mSwipeListener.onSwipeTo(recyclerView.findViewHolderForAdapterPosition(adapterPosition), dX);
                }
            }
        }
    }

    public void toLeft(RecyclerView recyclerView) {
        if (check(recyclerView)) {
            animTo(recyclerView, false);
        }
    }

    public void toRight(RecyclerView recyclerView) {
        if (check(recyclerView)) {
            animTo(recyclerView, true);
        }
    }

    private void animTo(final RecyclerView recyclerView, boolean right) {
        final int position = recyclerView.getAdapter().getItemCount() - 1;
        final View view = recyclerView.findViewHolderForAdapterPosition(position).itemView;

        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, right ? 1f : -1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1.3f);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(300);
        translateAnimation.setInterpolator(new DecelerateInterpolator());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isSwipeAnim = false;
                recyclerView.removeView(view);
                notifyListener(position,
                        x > view.getMeasuredWidth() / 2
                                ?
                                ItemTouchHelper.RIGHT : ItemTouchHelper.LEFT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(translateAnimation);
    }

    private boolean check(RecyclerView recyclerView) {
        if (isSwipeAnim) {
            return false;
        }
        if (recyclerView == null || recyclerView.getAdapter() == null) {
            return false;
        }
        if (recyclerView.getAdapter().getItemCount() == 0) {
            return false;
        }
        isSwipeAnim = true;
        return true;
    }

    public interface OnSwipeListener {

        /**
         * @param direction {@link ItemTouchHelper#LEFT} / {@link ItemTouchHelper#RIGHT}
         *                  {@link ItemTouchHelper#UP} or {@link ItemTouchHelper#DOWN}).
         */
        void onSwiped(int adapterPosition, int direction);

        /**
         * 最上层View滑动时回调.
         *
         * @param viewHolder 最上层的ViewHolder
         * @param offset     距离原始位置的偏移量
         */
        void onSwipeTo(RecyclerView.ViewHolder viewHolder, float offset);
    }

    public static class SimpleSwipeCallback implements OnSwipeListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSwiped(int adapterPosition, int direction) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSwipeTo(RecyclerView.ViewHolder viewHolder, float offset) {

        }
    }
}
