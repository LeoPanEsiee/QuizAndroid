package com.example.courstest1.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.courstest1.R;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderViewHolder> {
    private ArrayList<PlayerScoreItem> mExampleList;

    public static class LeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;

        public LeaderViewHolder(View itemView) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView);
            mTextView2 = itemView.findViewById(R.id.textView2);
        }
    }

    public LeaderboardAdapter(ArrayList<PlayerScoreItem> exampleList) {
        mExampleList = exampleList;
    }

    @Override
    public LeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_score_item, parent, false);
        LeaderViewHolder evh = new LeaderViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(LeaderViewHolder holder, int position) {
        PlayerScoreItem currentItem = mExampleList.get(position);

        holder.mTextView1.setText(currentItem.getText1());
        holder.mTextView2.setText(currentItem.getText2());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}