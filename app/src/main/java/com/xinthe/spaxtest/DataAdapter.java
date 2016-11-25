package com.xinthe.spaxtest;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinthe on 21-Nov-16.
 */
public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private Context mContext;
    private List<DataModel> notificationList;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Title, Message, couponcode, validity;
        public ImageView imageView, settings;

        public MyViewHolder(View view) {
            super(view);
            Title = (TextView) view.findViewById(R.id.Title);
            Message = (TextView) view.findViewById(R.id.Message);
            couponcode = (TextView) view.findViewById(R.id.Couponcode);
            validity = (TextView) view.findViewById(R.id.Validity);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            settings = (ImageView) view.findViewById(R.id.settings);
        }
    }


    public DataAdapter( List<DataModel> notificationList, Context context) {

        this.mContext = context;

        this.notificationList = notificationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DataModel album = notificationList.get(position);

        DataModel data = notificationList.get(position);

//        String a = holder.Title.toString();
//        holder.Title.setText(a);
//
//        String b = holder.Message.toString();
//        holder.Message.setText(b);
//
//
//
//        String c = holder.couponcode.toString();
//        holder.couponcode.setText(c);
//
//        String d = holder.validity.toString();
//        holder.validity.setText(d);

//        TextView Title = holder.Title;
//        TextView Message = holder.Message;
//        TextView Couponcode = holder.couponcode;
//        TextView Validity = holder.validity;
//       ImageView imageView = holder.imageView;



        Picasso.with(mContext)
                .load(notificationList.get(position).getAttachement())
                .placeholder(R.drawable.ic_stat_ic_notification)
                .error(R.drawable.error1)      // optional
                .resize(250, 200)              // optional
                .onlyScaleDown()
                // .rotate(90)                 // optional
                .into(holder.imageView);


        holder.Title.setText(notificationList.get(position).getTitle());
        holder. Message.setText(notificationList.get(position).getMessage());
        holder.couponcode.setText("Coupon Code:"+notificationList.get(position).getCouponcode());
        holder.validity.setText("Validity:"+notificationList.get(position).getValidity());





       // holder.count.setText(album.getNumOfSongs() + " songs");

        // loading album cover using Glide library
   //     Glide.with(mContext).load(MyAdapter.getAttachement()).into(holder.imageView);




//        holder.settings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.settings);
//            }
//        });
    }
//
//    /**
//     * Showing popup menu when tapping on 3 dots
//     */
//    private void showPopupMenu(View view) {
//        // inflate menu
//        PopupMenu popup = new PopupMenu(mContext, view);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu, popup.getMenu());
//        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
//        popup.show();
//    }
//
//    /**
//     * Click listener for popup menu items
//     */
//    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
//
//        public MyMenuItemClickListener() {
//        }
//
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            switch (menuItem.getItemId()) {
//                case R.id.action_refresh:
//                    Toast.makeText(mContext, "Refreshed", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.action_expand:
//                    Toast.makeText(mContext, "Expand", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//            }
//            return false;
//        }
//    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


}