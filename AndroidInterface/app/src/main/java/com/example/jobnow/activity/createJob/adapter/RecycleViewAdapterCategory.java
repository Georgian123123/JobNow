package com.example.jobnow.activity.createJob.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobnow.entity.Category;

import java.util.List;

public class RecycleViewAdapterCategory extends RecyclerView.Adapter<RecycleViewAdapterCategory.ViewHolder> {
    private Context context;
    private List<Category> basebaseCategories;
    private List<Category> complementaryCategories;
    private int rowDisplay, nameId, buttonId;
    private AdapterSyncCategory adapterSync;

    public RecycleViewAdapterCategory(Context context, List<Category> basebaseCategories, List<Category> complementaryCategories, int rowDisplay, int nameId, int buttonId) {
        this.context = context;
        this.basebaseCategories = basebaseCategories;
        this.complementaryCategories = complementaryCategories;
        this.rowDisplay = rowDisplay;
        this.nameId = nameId;
        this.buttonId = buttonId;
        adapterSync = AdapterSyncCategory.getInstance();
        adapterSync.addAdapter(this);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Category c: basebaseCategories) {
            stringBuilder.append(c.toString() + '\n');
        }
        if (stringBuilder.length() != 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowDisplay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.name.setText(basebaseCategories.get(position).getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complementaryCategories.add(basebaseCategories.remove(position));
                adapterSync.notifyAllAdapters();
            }
        });
    }



    @Override
    public int getItemCount() {
        return basebaseCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageButton button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(nameId);
            button = itemView.findViewById(buttonId);
        }
    }
}
