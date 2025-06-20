package com.cleveradssolutions.sampleapp.selection;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cleveradssolutions.sampleapp.R;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.Holder> {

    private final AdContainers[] items = AdContainers.values();

    @Override
    public int getItemCount() {
        return items.length;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selection_item, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        AdContainers item = items[position];
        holder.textView.setText(item.getTitleResId());
        holder.textView.setOnClickListener(view -> {
            Context context = view.getContext();
            Intent intent = new Intent(context, item.getActivity());
            context.startActivity(intent);
        });
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView textView;

        public Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

}
