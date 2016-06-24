package com.jueggs.podcaster.ui.category;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jueggs.utils.Utils.*;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
{
    private List<Category> categories = new ArrayList<>();
    private List<Channel> channels = new ArrayList<>();
    private Context context;

    public CategoryAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Category category = categories.get(position);
        holder.category.setText(category.getTitle());
        if (hasElements(category.getSubCategories()))
        {
            int count = category.getSubCategories().size();
            String formatString = context.getResources().getQuantityString(R.plurals.count_subcategories_format, count, count);
            String text = String.format(formatString, count);
            holder.subcategories.setText(text);
        }
    }

    @Override
    public int getItemCount()
    {
        return categories.size() + channels.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return super.getItemViewType(position);
    }

    public void setCategories(List<Category> categories)
    {
        this.categories = categories;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        @Bind(android.R.id.text1) TextView category;
        @Bind(android.R.id.text2) TextView subcategories;

        public ViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            subcategories.setTypeface(null, Typeface.ITALIC);
        }
    }
}
