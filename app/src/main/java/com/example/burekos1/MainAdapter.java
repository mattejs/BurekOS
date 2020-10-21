package com.example.burekos1;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainAdapterViewHolder> {

    private MainAdapterClickListener mainAdapterClickListener;
    private List<CityBlock> cityBlockList;


    public MainAdapter() {

        cityBlockList = new ArrayList<>();
    }


    public MainAdapter(MainAdapterClickListener mainAdapterClickListener, List<CityBlock> cityBlockList) {

        this.mainAdapterClickListener = mainAdapterClickListener;
        this.cityBlockList = cityBlockList;

    }

    @NonNull
    @Override
    public MainAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MainAdapterViewHolder(view, mainAdapterClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapterViewHolder holder, int position) {

        CityBlock cityBlock = cityBlockList.get(position);
        holder.bind(cityBlock);

    }

    @Override
    public int getItemCount() {
        return cityBlockList.size();
    }


    public class MainAdapterViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.textView_cityBlockName)
        TextView textView_cityBlockName;


        public MainAdapterViewHolder(@NonNull View itemView, MainAdapterClickListener mainAdapterClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(CityBlock cityBlock) {

            textView_cityBlockName.setText(cityBlock.getCityBlockName());
        }

        @OnClick
        public void onItemClick() {
            mainAdapterClickListener.onClick(cityBlockList.get(getAdapterPosition()));
        }

    }
}
