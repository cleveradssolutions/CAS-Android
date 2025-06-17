package com.cleveradssolutions.sampleapp.selection

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cleveradssolutions.sampleapp.R
import com.cleversolutions.ads.android.CAS

class SelectionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.selection_activity)

        // Get current SDK version
        findViewById<TextView>(R.id.casVersionText).text = CAS.getSDKVersion()

        findViewById<RecyclerView>(R.id.recyclerView).run {
            adapter = SelectionAdapter()
            addItemDecoration(DividerItemDecoration(context))
        }
    }
}