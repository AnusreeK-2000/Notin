package com.example.notin.Student;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notin.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class pdfViewerActivity extends AppCompatActivity {
    PDFView pdfView;
    String link="",title="";
    //int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView=findViewById(R.id.reader);

        /*position=getIntent().getIntExtra("position",-1);
        viewPdf();

         */
        title=getIntent().getStringExtra("title");
        link=getIntent().getStringExtra("link");




        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            link = getIntent().getStringExtra("link");
        }
        new RetrievePDFStream().execute(link);

    }
    class RetrievePDFStream extends AsyncTask<String,Void,InputStream> {
        ProgressDialog progressDialog;
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(pdfViewerActivity.this);
            progressDialog.setTitle("Getting the book content...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream=null;

            try{

                URL urlx=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) urlx.openConnection();
                if(urlConnection.getResponseCode()==200){
                    inputStream=new BufferedInputStream(urlConnection.getInputStream());

                }
            }catch (IOException e){
                return null;
            }
            return inputStream;

        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressDialog.dismiss();
        }
    }
    /*
    private void viewPdf() {
        pdfView.fromFile(UploadNotesActivity.mfiles.get(position))
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .swipeHorizontal(false)
                .load();
    }
    */

}