package com.faizal.quizkelompok1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.faizal.quizkelompok1.R.id.mainMenuLayout

// Data class untuk struktur soal
data class SoalTebakGambar(
    val imageResId: Int,      // ID Gambar di drawable
    val jawabanBenar: String, // Jawaban yang benar
    val opsiLain: List<String> // 3 Jawaban pengecoh
)

class MainActivity : AppCompatActivity() {

    // --- Variabel UI ---
    // Layout Menu
    private lateinit var layoutMenu: View
    private lateinit var btnAyoMain: Button

    // Layout Game
    private lateinit var layoutGame: View
    private lateinit var imgSoal: ImageView
    private lateinit var tvLevel: TextView
    private lateinit var buttons: List<Button>

    // --- Variabel Game Logic ---
    private val allQuestions = ArrayList<SoalTebakGambar>() // Bank soal (15 soal)
    private var activeQuestions = ArrayList<SoalTebakGambar>() // Soal aktif (10 soal)
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kita menggunakan teknik setContentView sederhana untuk berpindah antar layout
        // agar lebih mudah dipahami dalam satu file.
        showMenu()
    }

    // 1. TAMPILKAN MENU AWAL
    private fun showMenu() {
        setContentView(R.layout.activity_main)

        // --- PERBAIKAN DI SINI ---
        // Kita panggil langsung ID dari RelativeLayout yang baru kita buat
        layoutMenu = findViewById(mainMenuLayout)

        btnAyoMain = findViewById(R.id.btnAyoMain)

        btnAyoMain.setOnClickListener {
            prepareGameData()
            startGame()
        }
    }

    // 2. SIAPKAN DATA (15 Soal disiapkan di sini)
    private fun prepareGameData() {
        allQuestions.clear()

        // CONTOH: Menambahkan 15 Soal ke Bank Soal
        // Ganti R.drawable.ic_menu_gallery dengan R.drawable.nama_gambar_kamu
        // Pastikan kamu punya gambar di folder res/drawable
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
        allQuestions.add(SoalTebakGambar(placeholderImg, "BATU", listOf("PASIR", "TANAH", "KERIKIL")))

        // LOGIKA PENTING: Shuffle dan Ambil 10 saja
        activeQuestions.clear()
        activeQuestions.addAll(allQuestions.shuffled().take(10))
    }

    // 3. MULAI GAME
    private fun startGame() {
        setContentView(R.layout.activity_game)

        // Inisialisasi UI Game
        imgSoal = findViewById(R.id.imgSoal)
        tvLevel = findViewById(R.id.tvLevel)

        // Setup tombol pilihan (A, B, C, D)
        buttons = listOf(
            findViewById(R.id.btnOptionA),
            findViewById(R.id.btnOptionB),
            findViewById(R.id.btnOptionC),
            findViewById(R.id.btnOptionD)
        )

        currentQuestionIndex = 0
        score = 0

        loadQuestion()
    }

    // 4. LOAD PERTANYAAN
    private fun loadQuestion() {
        // Cek jika sudah selesai 10 soal
        if (currentQuestionIndex >= activeQuestions.size) {
            finishGame()
            return
        }

        val currentQ = activeQuestions[currentQuestionIndex]

        // Update UI
        tvLevel.text = "Soal ${currentQuestionIndex + 1}/10"
        imgSoal.setImageResource(currentQ.imageResId)

        // Siapkan pilihan jawaban (1 Benar + 3 Salah)
        val options = ArrayList<String>()
        options.add(currentQ.jawabanBenar)
        options.addAll(currentQ.opsiLain)
        options.shuffle() // Acak posisi tombol A,B,C,D

        // Pasang text ke tombol dan set Click Listener
        for (i in buttons.indices) {
            buttons[i].text = options[i]
            buttons[i].isEnabled = true
            buttons[i].backgroundTintList = getColorStateList(android.R.color.holo_orange_light) // Reset warna

            buttons[i].setOnClickListener {
                checkAnswer(options[i], currentQ.jawabanBenar)
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
        // Delay sedikit sebelum ganti soal agar user bisa melihat efek (opsional)
        android.os.Handler(mainLooper).postDelayed({
            loadQuestion()
        }, 500) // Jeda 0.5 detik
    }

    // 6. GAME SELESAI
    private fun finishGame() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permainan Selesai!")
        builder.setMessage("Skor Kamu: $score / 100")
        builder.setPositiveButton("Main Lagi") { _, _ ->
            // Reset dan kembali ke menu atau langsung main
            showMenu()
        }
        builder.setCancelable(false)
        builder.show()
    }
}