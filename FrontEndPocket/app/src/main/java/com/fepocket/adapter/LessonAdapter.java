package com.fepocket.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fepocket.R;
import com.fepocket.db.DatabaseHelper;
import com.fepocket.model.Lesson;
import com.fepocket.ui.AddLessonActivity;
import com.fepocket.ui.LessonDetailActivity;

import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private Context context;
    private List<Lesson> lessons;
    private DatabaseHelper dbHelper;
    private boolean isAdmin;

    public LessonAdapter(Context context, List<Lesson> lessons, DatabaseHelper dbHelper, boolean isAdmin) {
        this.context = context;
        this.lessons = lessons;
        this.dbHelper = dbHelper;
        this.isAdmin = isAdmin;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvLessonTitle.setText(lesson.getTitle());
        holder.tvLessonDescription.setText(lesson.getDescription());

        String difficulty = lesson.getDifficulty();
        holder.tvLessonDifficulty.setText(difficulty);
        switch (difficulty.toLowerCase()) {
            case "beginner":
                holder.tvLessonDifficulty.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "intermediate":
                holder.tvLessonDifficulty.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "advanced":
                holder.tvLessonDifficulty.setTextColor(Color.parseColor("#F44336"));
                break;
        }

        holder.ivFavorite.setImageResource(
                lesson.isFavorite() ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border
        );

        holder.ivFavorite.setOnClickListener(v -> {
            boolean newState = !lesson.isFavorite();
            lesson.setFavorite(newState);
            dbHelper.toggleFavorite(lesson.getId(), newState);
            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LessonDetailActivity.class);
            intent.putExtra("lesson", lesson);
            intent.putExtra("is_admin", isAdmin);
            context.startActivity(intent);
        });

        if (isAdmin) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        }

        holder.ivEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddLessonActivity.class);
            intent.putExtra("lesson_id", lesson.getId());
            intent.putExtra("category_id", lesson.getCategoryId());
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Lesson")
                    .setMessage("Are you sure you want to delete \"" + lesson.getTitle() + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        dbHelper.deleteLesson(lesson.getId());
                        lessons.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, lessons.size());
                        Toast.makeText(context, "Lesson deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void updateList(List<Lesson> newLessons) {
        this.lessons = newLessons;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardLesson;
        TextView tvLessonTitle, tvLessonDescription, tvLessonDifficulty;
        ImageView ivFavorite, ivEdit, ivDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            cardLesson = itemView.findViewById(R.id.cardLesson);
            tvLessonTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvLessonDescription = itemView.findViewById(R.id.tvLessonDescription);
            tvLessonDifficulty = itemView.findViewById(R.id.tvLessonDifficulty);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
