package com.faizal.quizkelompok1

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.faizal.quizkelompok1.R.id.mainMenuLayout

// Data class untuk struktur soal
data class SoalTebakGambar(
    val imageResId: Int,
    val jawabanBenar: String,
    val opsiLain: List<String>
)

class MainActivity : AppCompatActivity() {

    // Layout Menu
    private lateinit var layoutMenu: View
    private lateinit var btnAyoMain: Button

    // Layout Game
    private lateinit var imgSoal: ImageView
    private lateinit var tvLevel: TextView
    private lateinit var buttons: List<Button>
    private lateinit var btnKeluar: Button

    // Logic
    private val allQuestions = ArrayList<SoalTebakGambar>()
    private var activeQuestions = ArrayList<SoalTebakGambar>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMenu()
    }

    // 1. TAMPILKAN MENU
    private fun showMenu() {
        setContentView(R.layout.activity_main)

        layoutMenu = findViewById(mainMenuLayout)
        btnAyoMain = findViewById(R.id.btnAyoMain)

        val anim = AnimationUtils.loadAnimation(this, R.anim.zoom_in_out)
        btnAyoMain.startAnimation(anim)

        btnAyoMain.setOnClickListener {
            prepareGameData()
            startGame()
        }
    }

    // 2. SIAPKAN BANK SOAL
    private fun prepareGameData() {
        allQuestions.clear()
        val placeholderImg = android.R.drawable.ic_menu_gallery

        allQuestions.add(SoalTebakGambar(placeholderImg, "KUCING", listOf("ANJING", "AYAM", "BEBEK")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "POHON", listOf("RUMPUT", "BUNGA", "DAUN")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "RUMAH", listOf("GEDUNG", "TOKO", "KANTOR")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "MOBIL", listOf("MOTOR", "SEPEDA", "BUS")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "LAPTOP", listOf("HP", "TABLET", "TV")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "BUKU", listOf("PENSIL", "PENGGARIS", "TAS")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "SEPATU", listOf("SANDAL", "KAOS KAKI", "TOPI")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "GELAS", listOf("PIRING", "MANGKUK", "SENDOK")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "MEJA", listOf("KURSI", "LEMARI", "KASUR")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "LAMPU", listOf("KIPAS", "AC", "KULKAS")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "JAM", listOf("KALENDER", "WAKTU", "DETIK")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "ROTI", listOf("NASI", "MIE", "KEJU")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "AIR", listOf("API", "TANAH", "UDARA")))
        allQuestions.add(SoalTebakGambar(placeholderImg, "API", listOf("AIR", "BATU", "KAYU")))
        // <-- FIXED: memperbaiki typo (sebelumnya listOf("PASIR, TANAH", "KERIKIL"))
        allQuestions.add(SoalTebakGambar(placeholderImg, "BATU", listOf("PASIR", "TANAH", "KERIKIL")))

        // Ambil 10 soal acak
        activeQuestions.clear()
        activeQuestions.addAll(allQuestions.shuffled().take(10))
    }

    // 3. MULAI GAME
    private fun startGame() {
        setContentView(R.layout.activity_game)

        imgSoal = findViewById(R.id.imgSoal)
        tvLevel = findViewById(R.id.tvLevel)
        btnKeluar = findViewById(R.id.btnKeluar)

        buttons = listOf(
            findViewById(R.id.btnOptionA),
            findViewById(R.id.btnOptionB),
            findViewById(R.id.btnOptionC),
            findViewById(R.id.btnOptionD)
        )

        currentQuestionIndex = 0
        score = 0

        btnKeluar.setOnClickListener {
            showExitDialog()
        }

        loadQuestion()
    }

    // 4. MUAT SOAL
    private fun loadQuestion() {
        if (currentQuestionIndex >= activeQuestions.size) {
            finishGame()
            return
        }

        val currentQ = activeQuestions[currentQuestionIndex]
        tvLevel.text = "Soal ${currentQuestionIndex + 1}/${activeQuestions.size}"
        imgSoal.setImageResource(currentQ.imageResId)

        val options = ArrayList<String>()
        options.add(currentQ.jawabanBenar)
        options.addAll(currentQ.opsiLain)
        options.shuffle()

        // DEFENSIVE: pastikan selalu ada 4 item (jika data kurang, tambahkan placeholder yang non-aktif)
        while (options.size < 4) {
            options.add("—")
        }

        for (i in buttons.indices) {
            val optText = options[i]
            buttons[i].text = optText

            // Jika placeholder (tanda data kurang), non-aktifkan tombolnya
            if (optText == "—") {
                buttons[i].isEnabled = false
                buttons[i].alpha = 0.6f
            } else {
                buttons[i].isEnabled = true
                buttons[i].alpha = 1.0f
            }

            // Reset warna/tint jika kamu memakai custom drawable
            try {
                buttons[i].backgroundTintList = null
            } catch (e: Exception) {
                // ignore if not supported on some API/theme combos
            }

            buttons[i].setOnClickListener {
                // hanya tangani klik kalau tombol aktif
                if (buttons[i].isEnabled) {
                    checkAnswer(optText, currentQ.jawabanBenar)
                }
            }
        }
    }

    // 5. CEK JAWABAN
    private fun checkAnswer(selected: String, correct: String) {
        if (selected == correct) {
            score += 10
            Toast.makeText(this, "Benar! +10 Poin", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Salah! Jawaban: $correct", Toast.LENGTH_SHORT).show()
        }

        currentQuestionIndex++
        android.os.Handler(mainLooper).postDelayed({
            loadQuestion()
        }, 500)
    }

    // 6. DIALOG SELESAI
    private fun finishGame() {
        AlertDialog.Builder(this)
            .setTitle("Permainan Selesai!")
            .setMessage("Skor Kamu: $score / 100")
            .setCancelable(false)
            .setPositiveButton("Main Lagi") { _, _ ->
                showMenu()
            }
            .show()
    }

    // 7. DIALOG KELUAR
    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Keluar?")
            .setMessage("Yakin ingin keluar dari quiz?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Keluar") { _, _ ->
                showMenu()
            }
            .show()
    }
}
