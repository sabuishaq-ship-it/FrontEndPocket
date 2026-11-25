package com.fepocket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fepocket.R;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.Category;
import com.fepocket.ui.LessonsActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvCategoryName.setText(category.getName());
        holder.tvCategoryLessons.setText(category.getLessonCount() + " lessons");

        switch (category.getName()) {
            case "HTML":
                holder.cardCategory.setCardBackgroundColor(context.getColor(R.color.html));
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_html);
                break;
            case "CSS":
                holder.cardCategory.setCardBackgroundColor(context.getColor(R.color.css));
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_css);
                break;
            case "JavaScript":
                holder.cardCategory.setCardBackgroundColor(context.getColor(R.color.javascript));
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_javascript);
                break;
            case "Bootstrap":
                holder.cardCategory.setCardBackgroundColor(context.getColor(R.color.bootstrap));
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_bootstrap);
                break;
            case "React":
                holder.cardCategory.setCardBackgroundColor(context.getColor(R.color.react));
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_react);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessonsActivity.class);
            intent.putExtra("category_id", category.getId());
            intent.putExtra("category_name", category.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardCategory;
        ImageView ivCategoryIcon;
        TextView tvCategoryName, tvCategoryLessons;

        public ViewHolder(View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryLessons = itemView.findViewById(R.id.tvCategoryLessons);
        }
    }
}
