package com.jueggs.podcaster.ui.category;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.jueggs.podcaster.R;
import com.jueggs.podcaster.model.Category;
import com.jueggs.podcaster.model.Channel;
import com.jueggs.podcaster.utils.DateUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.jueggs.podcaster.utils.DateUtils.*;
import static com.jueggs.podcaster.utils.Util.*;
import static com.jueggs.utils.Utils.*;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    public static final int VIEWTYPE_CATEGORY = 1;
    public static final int VIEWTYPE_CHANNEL = 2;

    @Getter
    private List<Category> categories = new ArrayList<>();
    @Getter
    private List<Channel> channels = new ArrayList<>();

    private Context context;
    private FragmentManager fragmentManager;
    private Callback callback;
    private int level;
    private LinkedList<List<Category>> parentCategories = new LinkedList<>();
    private LinkedList<List<Channel>> parentChannels = new LinkedList<>();

    public CategoryAdapter(Context context, FragmentManager fragmentManager, Callback callback)
    {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.callback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        switch (viewType)
        {
            case VIEWTYPE_CATEGORY:
                view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
                return new CategoryViewHolder(view);
            case VIEWTYPE_CHANNEL:
                view = LayoutInflater.from(context).inflate(R.layout.list_channel, parent, false);
                return new ChannelViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position)
    {
        if (vh instanceof CategoryViewHolder)
        {
            CategoryViewHolder holder = (CategoryViewHolder) vh;
            Category category = categories.get(position);

            holder.category.setText(category.getTitle());
            if (hasElements(category.getSubCategories()))
            {
                int count = category.getSubCategories().size();
                String formatString = context.getResources().getQuantityString(R.plurals.count_subcategories_format, count, count);
                String text = String.format(formatString, count);
                holder.subcategories.setText(text);
            }
            else
                holder.subcategories.setText("");
            holder.itemView.setOnClickListener(holder);
        }
        else if (vh instanceof ChannelViewHolder)
        {
            ChannelViewHolder holder = (ChannelViewHolder) vh;
            Channel channel = channels.get(position - categories.size());
            holder.bindView(context, channel);
            holder.itemView.setOnClickListener(holder);
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
        if (position < categories.size())
            return VIEWTYPE_CATEGORY;
        else
            return VIEWTYPE_CHANNEL;
    }

    public void onCategoriesLoaded(List<Category> categories)
    {
        setCategories(categories);
    }

    public void setCategories(List<Category> categories)
    {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public void setChannels(List<Channel> channels)
    {
        this.channels = channels;
        notifyDataSetChanged();
    }

    public void onNavigateBack(View view)
    {
        setCategories(parentCategories.pollLast());
        setChannels(parentChannels.pollLast());
        callback.onNavigationLevelChanged(--level);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @Bind(android.R.id.text1) TextView category;
        @Bind(android.R.id.text2) TextView subcategories;

        public CategoryViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
            subcategories.setTypeface(null, Typeface.ITALIC);
        }

        @Override
        public void onClick(View view)
        {
            int position = getAdapterPosition();
            Category category = categories.get(position);

            parentCategories.add(categories);
            parentChannels.add(channels);
            callback.onCategorySelected(category);
            callback.onNavigationLevelChanged(++level);

            if (hasElements(category.getSubCategories()))
                setCategories(category.getSubCategories());
            else
                setCategories(new ArrayList<>());
        }
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @Bind(R.id.title) public TextView title;
        @Bind(R.id.image) public ImageView image;
        @Bind(R.id.rating) public TextView rating;
        @Bind(R.id.date) public TextView date;
        @Bind(R.id.subscribers) public TextView subscribers;
        @Bind(R.id.description) public TextView description;

        public ChannelViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindView(Context context, Channel channel)
        {
            title.setText(channel.getTitle());
            description.setText(channel.getDescription());
            rating.setText(channel.getRating());

            int subscriberCount = Integer.parseInt(channel.getSubscribers());
            String text = context.getResources().getQuantityString(R.plurals.count_subscribers_format, subscriberCount, subscriberCount);
            subscribers.setText(text);

            date.setText(createDateString(context, channel.getDate()));

            Glide.with(context).load(channel.getImage()).placeholder(R.drawable.glide_placeholder).error(R.drawable.glide_error).into(image);
        }

        @Override
        public void onClick(View v)
        {
            showChannelDetails((Activity) context, fragmentManager, channels.get(getAdapterPosition() - categories.size()), image);
        }
    }
}
