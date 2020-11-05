package com.example.notin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.Student.pdfViewerActivity;
import com.example.notin.entities.myListData;


public class MyListAdapter extends RecyclerView.Adapter<com.example.notin.adapters.MyListAdapter.ListViewHolder> {

    private myListData[] listdata;
    private  Context mContext;



    public MyListAdapter(Context mContext, myListData[] listdata) {
        this.mContext = mContext;
        this.listdata = listdata;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View listItem= layoutInflater.inflate(R.layout.activity_upload_notes, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, final int position) {
        final myListData myList = listdata[position];
        holder.textTitle.setText(listdata[position].getTitle());
        holder.textAuthor.setText(listdata[position].getAuthor());

        holder.pdf_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,pdfViewerActivity.class);

                //sending position of the listdata to retrieve the path and open new activity
                intent.putExtra("position",position);
                mContext.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.length;
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder{

        public TextView textTitle,textAuthor;
        ImageView img_icon;
        RelativeLayout pdf_item;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.pdfTitle);
            this.textAuthor = (TextView) itemView.findViewById(R.id.sender);
            this.img_icon=(ImageView)itemView.findViewById((R.id.img_pdf));
            this.pdf_item=(RelativeLayout) itemView.findViewById(R.id.pdf_item);
        }
    }
}
