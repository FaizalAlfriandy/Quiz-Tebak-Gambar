package com.faizal.quizkelompok1

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    // Deklarasi properti
    private lateinit var builder: AlertDialog.Builder
    private lateinit var radiogroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi RadioGroup menggunakan lateinit
        // findViewById<T>() adalah cara Kotlin yang lebih ringkas
        radiogroup = findViewById(R.id.radioGroup)
    }

    // Memilih RadioButton (Metode yang dipanggil dari XML android:onClick)
    fun onRadioButton(view: View) {
        // Safe casting View ke RadioButton
        val checked = (view as RadioButton).isChecked

        when (view.id) {
            R.id.radioButton -> {
                if (checked) {
                    tampilDialog()
                }
            }
            R.id.radioButton2, R.id.radioButton3, R.id.radioButton4 -> {
                if (checked) {
                    jawabanSalah()
                }
            }
        }
    }

    // Menampilkan dialog untuk jawaban benar
    private fun tampilDialog() {
        // Inisialisasi builder
        builder = AlertDialog.Builder(this)

        builder.apply {
            setCancelable(false)
            setTitle("Selamat !!!")
            setMessage("Jawaban kamu benar : Real Marid")
            // Penggunaan lambda untuk DialogInterface.OnClickListener
            setPositiveButton("OKE") { _, _ ->
                Toast.makeText(this@MainActivity, "Selamat", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("ULANGI") { _, _ ->
                radiogroup.clearCheck()
            }
        }.create().show()
    }

    // Menampilkan Toast untuk jawaban salah
    private fun jawabanSalah() {
        Toast.makeText(this, "Jawaban kamu Salah", Toast.LENGTH_SHORT).show()
    }
}