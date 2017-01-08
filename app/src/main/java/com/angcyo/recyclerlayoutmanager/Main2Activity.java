package com.angcyo.recyclerlayoutmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private List<SwipeCardBean> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatas = SwipeCardBean.initDatas();
                recyclerView.getAdapter().notifyDataSetChanged();

            }
        });

        mDatas = SwipeCardBean.initDatas();

        recyclerView.setLayoutManager(new OverLayCardLayoutManager(this));
        recyclerView.setAdapter(new MyAdapter());

        final RenRenCallback callback = new RenRenCallback();
        callback.setSwipeListener(new RenRenCallback.OnSwipeListener() {
            @Override
            public void onSwiped(int adapterPosition, int direction) {
                if (direction == ItemTouchHelper.DOWN || direction == ItemTouchHelper.UP) {
                    mDatas.add(0, mDatas.remove(adapterPosition));
//                    Collections.swap(mDatas, 0, adapterPosition);
                    recyclerView.getAdapter().notifyDataSetChanged();
                } else {
                    mDatas.remove(adapterPosition);
//                    recyclerView.getAdapter().notifyItemRemoved(adapterPosition);
                }
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);

        findViewById(R.id.to_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.toLeft(recyclerView);
            }
        });
        findViewById(R.id.to_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.toRight(recyclerView);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;
        ImageView mImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        public int dp(int dp) {
            final float density = getResources().getDisplayMetrics().density;
            return (int) (dp * density);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            CardView cardView = new CardView(Main2Activity.this);
            cardView.setLayoutParams(new ViewGroup.LayoutParams(dp(270), dp(300)));
            cardView.setCardBackgroundColor(Color.parseColor("#393F4E"));
//            cardView.setBackgroundColor(Color.BLUE);
            TextView textView = new TextView(Main2Activity.this);
//            textView.setBackgroundColor(Color.YELLOW);

            ImageView imageView = new ImageView(Main2Activity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            textView.setPadding(0, dp(260), 0, 0);

            cardView.addView(textView, new ViewGroup.LayoutParams(dp(270), dp(300)));
            cardView.addView(imageView, new ViewGroup.LayoutParams(dp(270), dp(260)));

            final MyViewHolder myViewHolder = new MyViewHolder(cardView);
            myViewHolder.mTextView = textView;
            myViewHolder.mImageView = imageView;
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            Glide.with(Main2Activity.this).load(mDatas.get(position).getUrl()).fitCenter().into(holder.mImageView);
            holder.mTextView.setText(mDatas.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

}
