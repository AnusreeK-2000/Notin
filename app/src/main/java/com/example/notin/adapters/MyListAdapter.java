package com.example.notin.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notin.R;
import com.example.notin.Student.UploadPDFDetails;
import com.example.notin.Student.pdfViewerActivity;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ListViewHolder> {
    ArrayList<UploadPDFDetails> pdfList;
    //private myListData[] listdata;
    private Context mContext;
    ArrayList<UploadPDFDetails> newPdf;

    public MyListAdapter(Context mContext, ArrayList<UploadPDFDetails> pdfList) {
        this.mContext = mContext;
        this.pdfList = pdfList;
        newPdf = pdfList;
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View listItem = layoutInflater.inflate(R.layout.activity_upload_notes, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, final int position) {
        /*
        final myListData myList = listdata[position];
        holder.textTitle.setText(listdata[position].getTitle());
        holder.textAuthor.setText(listdata[position].getAuthor());
        */
        UploadPDFDetails pdf = pdfList.get(position);
        holder.textTitle.setText(pdf.getName());
        holder.textAuthor.setText(pdf.getAuthor());


        holder.pdf_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, pdfViewerActivity.class);

                //sending position of the listdata to retrieve the path and open new activity
                //intent.putExtra("position",position);
                intent.putExtra("title", pdfList.get(position).getName());
                intent.putExtra("link", pdfList.get(position).getUrl());
                mContext.startActivity(intent);

            }
        });

        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile(holder.textTitle.getContext(), pdfList.get(position).getName(), ".pdf", DIRECTORY_DOWNLOADS, pdfList.get(position).getUrl());
            }
        });


    }

    public void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadmanager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle, textAuthor;
        ImageView down;
        RelativeLayout pdf_item;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.pdfTitle);
            this.textAuthor = (TextView) itemView.findViewById(R.id.sender);
            this.down = (ImageView) itemView.findViewById((R.id.download));
            this.pdf_item = (RelativeLayout) itemView.findViewById(R.id.pdf_item);
        }
    }

    public void searchnotes(final String searchKeyword) {

        if (searchKeyword.trim().isEmpty()) {
            pdfList = newPdf;
        } else {
            ArrayList<UploadPDFDetails> temp = new ArrayList<>();
            for (UploadPDFDetails note : newPdf) {
                if (note.getName().toLowerCase().contains(searchKeyword.toLowerCase())
                        || note.getAuthor().toLowerCase().contains(searchKeyword.toLowerCase())) {
                    temp.add(note);
                }
            }
            pdfList = temp;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }
}

