package com.example.burekos1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class SheetAdapter extends RecyclerView.Adapter<SheetAdapter.SheetAdapterViewHolder> {

    private SheetAdapterClickListener sheetAdapterClickListener;
    private List<Bakery> bakeryList;

    public SheetAdapter() {

        this.bakeryList = new ArrayList<>();
    }

    public SheetAdapter(SheetAdapterClickListener sheetAdapterClickListener,
                        List<Bakery> bakeryList) {

        this.sheetAdapterClickListener = sheetAdapterClickListener;
        this.bakeryList = bakeryList;
    }

    @NonNull
    @Override
    public SheetAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sheet_item, parent, false);
        return new SheetAdapterViewHolder(view, sheetAdapterClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SheetAdapterViewHolder holder, int position) {

        Bakery bakery = bakeryList.get(position);
        holder.bind(bakery);

    }

    @Override
    public int getItemCount() {
        return bakeryList.size();
    }


    public class SheetAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.textView_bakeryName)
        TextView textView_bakeryName;
        @BindView(R.id.textView_ratingMain)
        TextView textView_ratingMain;

        public SheetAdapterViewHolder(@NonNull View itemView, SheetAdapterClickListener sheetAdapterClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Bakery bakery) {
            textView_bakeryName.setText(bakery.getName());
            double votesSum = Double.parseDouble(bakery.getVotesSum());
            double noVotes = Double.parseDouble(bakery.getNoVotes());
            double rating = votesSum / noVotes;
            NumberFormat numberFormat = new DecimalFormat("#0.00");
            String stringRating = numberFormat.format(rating);
            textView_ratingMain.setText(stringRating);
        }

        @OnClick
        public void onItemClick() {
            sheetAdapterClickListener.onClick(bakeryList.get(getAdapterPosition()));
        }
    }
}
