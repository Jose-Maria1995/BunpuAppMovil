package com.example.bunpuoficial.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.activities.FiltersActivity;

public class FilterFragment extends Fragment {

    View mView;
    CardView mCardViewPescaderia;
    CardView mCardViewFruteria;
    CardView mCardViewPanaderia;
    CardView mCardViewCarniceria;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_filter, container, false);

        mCardViewCarniceria=mView.findViewById(R.id.cardViewCarniceria);
        mCardViewPescaderia=mView.findViewById(R.id.cardViewPescaderia);
        mCardViewPanaderia=mView.findViewById(R.id.cardViewPanaderia);
        mCardViewFruteria=mView.findViewById(R.id.cardViewFruteria);

        mCardViewPanaderia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Panaderia","#ffff00");
            }
        });

        mCardViewPescaderia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Pescaderia","#00AAEE");
            }
        });

        mCardViewCarniceria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Carniceria","#EFB810");
            }
        });

        mCardViewFruteria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Fruteria","#FF0000");
            }
        });

        return mView;
    }

    private void goToFilterActivity(String category,String color)
    {
        Intent intent=new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("category", category);
        intent.putExtra("color", color);
        startActivity(intent);
    }
}