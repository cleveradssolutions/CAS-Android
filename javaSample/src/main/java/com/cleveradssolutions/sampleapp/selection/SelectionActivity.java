package com.cleveradssolutions.sampleapp.selection;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cleveradssolutions.sampleapp.R;
import com.cleversolutions.ads.android.CAS;

public class SelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_activity);

        // Get current SDK version
        TextView versionText = findViewById(R.id.casVersionText);
        versionText.setText(CAS.getSDKVersion());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new SelectionAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
    }

}
