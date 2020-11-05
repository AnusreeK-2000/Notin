package com.example.notin.Student;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notin.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

public class pdfViewerActivity extends AppCompatActivity {
    PDFView pdfView;
    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView=findViewById(R.id.reader);
        position=getIntent().getIntExtra("position",-1);
        viewPdf();

    }

    private void viewPdf() {
        pdfView.fromFile(UploadNotesActivity.mfiles.get(position))
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .swipeHorizontal(false)
                .load();
    }
}