package com.example.crudfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AddMatkulActivity : AppCompatActivity() {

    private lateinit var tvNama : TextView
    private lateinit var etMatkul: EditText
    private lateinit var etSks: EditText
    private lateinit var btnSave: Button
    private lateinit var rvMatkul: RecyclerView

    companion object {
        const val EXTRA_NAMA = "extra_nama"
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_matkul)
    }
}