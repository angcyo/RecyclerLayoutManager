package com.angcyo.recyclerlayoutmanager;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "layout_manager";
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                mItemTouchHelper.startSwipe(recyclerView.findViewHolderForAdapterPosition(2));
                recyclerView.findViewHolderForAdapterPosition(2).itemView.dispatchTouchEvent(MotionEvent.obtain(System.currentTimeMillis() - 1000,
                        System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                recyclerView.findViewHolderForAdapterPosition(2).itemView.dispatchTouchEvent(MotionEvent.obtain(System.currentTimeMillis() - 1000,
                        System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                recyclerView.findViewHolderForAdapterPosition(2).itemView.onTouchEvent(MotionEvent.obtain(System.currentTimeMillis() - 1000,
                        System.currentTimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));

//                recyclerView.findViewHolderForAdapterPosition(2).itemView.onTouchEvent(MotionEvent.obtain(System.currentTimeMillis() - 1000,
//                        System.currentTimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        });


        recyclerView.setAdapter(new MyAdapter());
        recyclerView.setLayoutManager(new MyLayoutManager());
        mItemTouchHelper = new ItemTouchHelper(new MyCallback2(ItemTouchHelper.DOWN | ItemTouchHelper.UP,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT));
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public class MyCallback2 extends ItemTouchHelper.SimpleCallback {

        public MyCallback2(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            Log.e(TAG, "onMove: " + viewHolder.getAdapterPosition() + "->" + target.getAdapterPosition()
                    + " : " + viewHolder.getLayoutPosition() + "->" + target.getLayoutPosition());
            recyclerView.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.e(TAG, "onSwiped: " + viewHolder.getAdapterPosition() + " " + viewHolder.getLayoutPosition() + ":" + direction);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            Log.e(TAG, "onChildDraw: " + dX + " " + dY);
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            Log.e(TAG, "onChildDrawOver: " + dX + " " + dY);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            Log.e(TAG, "onSelectedChanged: " + actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            Log.e(TAG, "clearView: " + viewHolder.getAdapterPosition());
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
            relativeLayout.setBackgroundColor(Color.BLUE);
            TextView textView = new TextView(MainActivity.this);
            textView.setBackgroundColor(Color.YELLOW);
            relativeLayout.addView(textView, new ViewGroup.LayoutParams(-2, 200));
            final MyViewHolder myViewHolder = new MyViewHolder(relativeLayout);
            myViewHolder.mTextView = textView;
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Log.e(TAG, "onBindViewHolder: " + position);
            holder.mTextView.setText("................--> " + position);
//            holder.mTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e(TAG, "onClick: " + position + " " + holder.getAdapterPosition() + " " + holder.getLayoutPosition());
//                    Toast.makeText(MainActivity.this, "Hello..." + position, Toast.LENGTH_SHORT).show();
//                    mItemTouchHelper.startDrag(holder);
//                }
//            });
            holder.mTextView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
//                    mItemTouchHelper.startSwipe(holder);
//                    mItemTouchHelper.startDrag(holder);
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    public class MyLayoutManager extends RecyclerView.LayoutManager {

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
            Log.e(TAG, "generateDefaultLayoutParams: ");
            final RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-2, -2);
//            layoutParams.setMargins(10, 10, 10, 10);
            return layoutParams;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            Log.e(TAG, "onLayoutChildren: " + recycler.toString() + "\n" + state.toString());

            int top = 0;
            for (int i = 0; i < 60; i++) {
                final View view = recycler.getViewForPosition(i);
                addView(view);
//                measureChild(view, 100, 100);
                measureChildWithMargins(view, 100, 200);
//                view.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST),
//                        View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.AT_MOST));
                Log.e(TAG, "onLayoutChildren: view:" + view.getMeasuredWidth() + "  " + view.getMeasuredHeight());
                Log.e(TAG, "onLayoutChildren: " + getWidth() + " " + getHeight() + " :: " + getDecoratedMeasuredWidth(view) + " " + getDecoratedMeasuredHeight(view));
                layoutDecoratedWithMargins(view, 0, top, view.getMeasuredWidth(), top + view.getMeasuredHeight());
                top += view.getMeasuredHeight();
            }

        }

        @Override
        public boolean canScrollHorizontally() {
            return true;
        }

        @Override
        public boolean canScrollVertically() {
            return true;
        }

        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            offsetChildrenVertical(-dy);
            return dy;
        }

        @Override
        public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
            offsetChildrenHorizontal(-dx);
            return dx;
        }

        @Override
        public void onAttachedToWindow(RecyclerView view) {
            super.onAttachedToWindow(view);
            Log.e(TAG, "onAttachedToWindow: " + view.getMeasuredWidth() + " " + view.getMeasuredHeight());
        }

        @Override
        public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
            Log.e(TAG, "onRequestChildFocus: ");
            return super.onRequestChildFocus(parent, state, child, focused);
        }

        @Override
        public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
            super.onAdapterChanged(oldAdapter, newAdapter);
            Log.e(TAG, "onAdapterChanged: ");
        }

        @Override
        public void onDetachedFromWindow(RecyclerView view, RecyclerView.Recycler recycler) {
            super.onDetachedFromWindow(view, recycler);
            Log.e(TAG, "onDetachedFromWindow: ");
        }

        @Override
        public void onItemsAdded(RecyclerView recyclerView, int positionStart, int itemCount) {
            super.onItemsAdded(recyclerView, positionStart, itemCount);
            Log.e(TAG, "onItemsAdded: ");
        }

        @Override
        public void onItemsChanged(RecyclerView recyclerView) {
            super.onItemsChanged(recyclerView);
            Log.e(TAG, "onItemsChanged: ");
        }

        @Override
        public void onItemsMoved(RecyclerView recyclerView, int from, int to, int itemCount) {
            super.onItemsMoved(recyclerView, from, to, itemCount);
            Log.e(TAG, "onItemsMoved: ");
        }

        @Override
        public void onLayoutCompleted(RecyclerView.State state) {
            super.onLayoutCompleted(state);
            Log.e(TAG, "onLayoutCompleted: ");
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            Log.e(TAG, "onMeasure: ");
        }

        @Override
        public void onScrollStateChanged(int state) {
            super.onScrollStateChanged(state);
            Log.e(TAG, "onScrollStateChanged: " + state + " " + computeHorizontalScrollOffset(null));
        }
    }

}
