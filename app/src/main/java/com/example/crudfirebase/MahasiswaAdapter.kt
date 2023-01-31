package com.example.crudfirebase

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MahasiswaAdapter() : RecyclerView.Adapter<MahasiswaAdapter.ViewHolder>() {

    private val allNotes = ArrayList<Mahasiswa>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val etName = view.findViewById<TextView>(R.id.textNama)
        val etAlamat = view.findViewById<TextView>(R.id.textAlamat)
        val editIcon = view.findViewById<ImageView>(R.id.editIcon)
        val deleteIcon = view.findViewById<ImageView>(R.id.deleteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_view,
            parent, false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val mhs = allNotes.get(position)
        holder.etName.text = mhs.nama
        holder.etAlamat.text = mhs.alamat
        holder.editIcon.setOnClickListener {
            showUpdateDialog(mhs, context)
        }
        holder.deleteIcon.setOnClickListener {
            showDeleteDialog(mhs, context)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AddMatkulActivity::class.java)
            intent.putExtra(AddMatkulActivity.EXTRA_NAMA, mhs.nama)
            intent.putExtra(AddMatkulActivity.EXTRA_ID, mhs.id)
        }
    }

    override fun getItemCount(): Int = allNotes.size

    fun setData(newList: List<Mahasiswa>) {
        allNotes.clear()
        allNotes.addAll(newList)
        notifyDataSetChanged()
    }

    private fun showDeleteDialog(mhs: Mahasiswa, context: Context?) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete the item?")

        builder.setPositiveButton("Yes") { dialog, which ->
            val dbMhs = FirebaseDatabase.getInstance().getReference("mahasiswa")
            dbMhs.child(mhs.id).removeValue().addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Item berhasil di hapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Maaf, Item belum berhasil di hapus", Toast.LENGTH_SHORT).show()
                }
            }

            var mhsList: MutableList<Mahasiswa> = arrayListOf()
            dbMhs.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val mahasiswa = data.getValue(Mahasiswa::class.java)
                            if (mahasiswa != null) {
                                mhsList.add(mahasiswa)
                            }
                        }
                        setData(mhsList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        }

        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun showUpdateDialog(mhs: Mahasiswa, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Edit Data")
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update_dialog, null)

        val etNama = view.findViewById<EditText>(R.id.et_nama_update)
        val etAlamat = view.findViewById<EditText>(R.id.et_alamat_update)

        etNama.setText(mhs.nama)
        etAlamat.setText(mhs.alamat)

        builder.setView(view)
        builder.setPositiveButton("Update") {p0, p1 ->
            val dbMhs = FirebaseDatabase.getInstance().getReference("mahasiswa")
            val nama = etNama.text.toString().trim()
            val alamat = etAlamat.text.toString().trim()
            if (nama.isEmpty()) {
                etNama.error = "Mohon isi nama dahulu!"
                etNama.requestFocus()
                return@setPositiveButton
            }
            if (alamat.isEmpty()) {
                etAlamat.error = "Mohon isi alamat dahulu!"
                etAlamat.requestFocus()
                return@setPositiveButton
            }
            val mahasiswa = Mahasiswa(mhs.id, nama, alamat)
            dbMhs.child(mahasiswa.id!!).setValue(mahasiswa)
            Toast.makeText(context, "Data berhasil di update", Toast.LENGTH_SHORT).show()

            var mhsList: MutableList<Mahasiswa> = arrayListOf()
            dbMhs.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (data in snapshot.children) {
                            val mahasiswa = data.getValue(Mahasiswa::class.java)
                            if (mahasiswa != null) {
                                mhsList.add(mahasiswa)
                            }
                        }
                        setData(mhsList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        builder.setNegativeButton("No") {p0, p1 ->
            p0.dismiss()
        }

        val alert = builder.create()
        alert.show()
    }
}