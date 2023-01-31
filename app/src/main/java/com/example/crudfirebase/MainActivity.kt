package com.example.crudfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var btnSave: Button
    private lateinit var ref : DatabaseReference
    private var rvMahasiswa: MahasiswaAdapter = MahasiswaAdapter()
    private lateinit var adapter: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ref = FirebaseDatabase.getInstance().getReference("mahasiswa")

        setData()

        adapter = findViewById<RecyclerView?>(R.id.rv_mahasiswa).apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
        }

        etNama = findViewById(R.id.et_nama_update)
        etAlamat = findViewById(R.id.et_alamat_update)
        btnSave = findViewById(R.id.btn_save_update)

        btnSave.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        saveData()
    }

    private fun saveData() {
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()

        if(nama.isEmpty()) {
            etNama.error = "Isi Nama dahulu!"
            return
        }
        if(alamat.isEmpty()) {
            etAlamat.error = "Isi Alamat dahulu!"
            return
        }

        val mhsId = ref.push().key
        val mhs = Mahasiswa(mhsId!!, nama, alamat)

        if (mhsId != null) {
            ref.child(mhsId).setValue(mhs).addOnCompleteListener {
                Toast.makeText(this, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setData() {
        var mhsList: MutableList<Mahasiswa> = mutableListOf()
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val mahasiswa = data.getValue(Mahasiswa::class.java)
                        if (mahasiswa != null) {
                            mhsList.add(mahasiswa)
                        }
                    }
                    rvMahasiswa.setData(mhsList as List<Mahasiswa>)
                    Log.d("MainActivity", "$mhsList")
                    adapter.adapter = rvMahasiswa
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}