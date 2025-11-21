package com.faizal.quizkelompok1

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

// -------------------------------
// DATA CLASS
// -------------------------------
data class SoalTebakGambar(
    val imageResId: Int,
    val jawabanBenar: String,
    val opsiLain: List<String>
)

class MainActivity : AppCompatActivity() {

    // -------------------------------
    // UI MENU
    // -------------------------------
    private lateinit var layoutMenu: View
    private lateinit var btnAyoMain: ImageButton
    private lateinit var btnSound: ImageButton

    // -------------------------------
    // UI GAME
    // -------------------------------
    private lateinit var imgSoal: ImageView
    private lateinit var tvLevel: TextView
    private lateinit var buttons: List<Button>

    // -------------------------------
    // GAME LOGIC
    // -------------------------------
    private val allQuestions = ArrayList<SoalTebakGambar>()
    private var activeQuestions = ArrayList<SoalTebakGambar>()
    private var currentQuestionIndex = 0
    private var score = 0

    // -------------------------------
    // AUDIO
    // -------------------------------
    private var bgMenuMusic: MediaPlayer? = null
    private var bgGameMusic: MediaPlayer? = null
    private var isSoundOn = true

    private lateinit var prefs: SharedPreferences

    // SoundPool untuk efek
    private lateinit var soundPool: SoundPool
    private var soundBenar = 0
    private var soundSalah = 0
    private var soundSelesai = 0
    private var soundTransisi = 0

    // -------------------------------
    // ON CREATE
    // -------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        isSoundOn = prefs.getBoolean("sound", true)

        // Init SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(6).build()
        soundBenar = soundPool.load(this, R.raw.sound_benar, 1)
        soundSalah = soundPool.load(this, R.raw.sound_salah, 1)
        soundSelesai = soundPool.load(this, R.raw.selesai_sound, 1)
        soundTransisi = soundPool.load(this, R.raw.transisi_sound, 1)

        showMenu()
    }

    // ================================================================
    // 1) TAMPILKAN MENU
    // ================================================================
    private fun showMenu() {
        setContentView(R.layout.activity_main)

        layoutMenu = findViewById(R.id.mainMenuLayout)
        btnAyoMain = findViewById(R.id.btnAyoMain)
        btnSound = findViewById(R.id.btnSound)

        // Animasi tombol Ayo Main
        btnAyoMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_out))

        // Siapkan musik menu jika belum
        if (bgMenuMusic == null) {
            bgMenuMusic = MediaPlayer.create(this, R.raw.bg_music_menu)
            bgMenuMusic?.isLooping = true
        }

        // Sinkron dengan toggle
        updateSoundIcon()
        if (isSoundOn) bgMenuMusic?.start() else bgMenuMusic?.pause()

        // Tombol sound
        btnSound.setOnClickListener { toggleSound() }

        // Tombol mulai game
        btnAyoMain.setOnClickListener {
            if (isSoundOn) {
                soundPool.play(soundTransisi, 1f, 1f, 1, 0, 1f)
            }

            bgMenuMusic?.pause()
            prepareGameData()
            startGame()
        }
    }

    private fun updateSoundIcon() {
        btnSound.setImageResource(
            if (isSoundOn) R.drawable.ic_sound_on else R.drawable.ic_sound_off
        )
    }

    private fun toggleSound() {
        isSoundOn = !isSoundOn
        prefs.edit().putBoolean("sound", isSoundOn).apply()

        if (isSoundOn) bgMenuMusic?.start() else bgMenuMusic?.pause()
        updateSoundIcon()
    }

    // ================================================================
    // 2) SIAPKAN BANK SOAL
    // ================================================================
    private fun prepareGameData() {
        allQuestions.clear()

        val img = android.R.drawable.ic_menu_gallery

        allQuestions.add(SoalTebakGambar(R.drawable.asam_lambung, "ASAM LAMBUNG", listOf("ANJING", "AYAM", "BEBEK")))
        allQuestions.add(SoalTebakGambar(R.drawable.bencana_susulan, "BENCANA SUSULAN", listOf("RUMPUT", "BUNGA", "DAUN")))
        allQuestions.add(SoalTebakGambar(R.drawable.bisikan_jahat, "BISIKAN JAHAT", listOf("GEDUNG", "TOKO", "KANTOR")))
        allQuestions.add(SoalTebakGambar(R.drawable.jurus_andalan, "JURUS ANDALAN", listOf("MOTOR", "SEPEDA", "BUS")))
        allQuestions.add(SoalTebakGambar(R.drawable.kipas_angin, "KIPAS ANGIN", listOf("HP", "TABLET", "TV")))
        allQuestions.add(SoalTebakGambar(R.drawable.kostum_matador, "KOSTUM MATADOR", listOf("PENSIL", "PENGGARIS", "TAS")))
        allQuestions.add(SoalTebakGambar(R.drawable.meminta_imbalan, "MEMINTA IMBALAN", listOf("SANDAL", "KAOS KAKI", "TOPI")))
        allQuestions.add(SoalTebakGambar(R.drawable.motif_garis, "MOTIF GARIS", listOf("PIRING", "MANGKUK", "SENDOK")))
        allQuestions.add(SoalTebakGambar(R.drawable.penduduk_paris, "PENDUDUK PARIS", listOf("KURSI", "LEMARI", "KASUR")))
        allQuestions.add(SoalTebakGambar(R.drawable.pulau_seribu, "PULAU SERIBU", listOf("KIPAS", "AC", "KULKAS")))
        allQuestions.add(SoalTebakGambar(R.drawable.ruangan_terbuka, "RUANGAN TERBUKA", listOf("PENSIL", "PENGGARIS", "TAS")))
        allQuestions.add(SoalTebakGambar(R.drawable.sajian_hajatan, "SEPATU", listOf("SANDAL", "KAOS KAKI", "TOPI")))
        allQuestions.add(SoalTebakGambar(R.drawable.tahan_banting, "TAHAN BANTING", listOf("PIRING", "MANGKUK", "SENDOK")))
        allQuestions.add(SoalTebakGambar(R.drawable.teks_pancasila, "TEKS PANCASILA", listOf("KURSI", "LEMARI", "KASUR")))
        allQuestions.add(SoalTebakGambar(R.drawable.toko_seragam, "TOKO SERAGAM", listOf("KIPAS", "AC", "KULKAS")))

        activeQuestions.clear()
        activeQuestions.addAll(allQuestions.shuffled().take(10))
    }

    // ================================================================
    // 3) MULAI GAME
    // ================================================================
    private fun startGame() {
        setContentView(R.layout.activity_game)

        imgSoal = findViewById(R.id.imgSoal)
        tvLevel = findViewById(R.id.tvLevel)

        buttons = listOf(
            findViewById(R.id.btnOptionA),
            findViewById(R.id.btnOptionB),
            findViewById(R.id.btnOptionC),
            findViewById(R.id.btnOptionD)
        )

        currentQuestionIndex = 0
        score = 0

        // Start musik game
        if (bgGameMusic == null) {
            bgGameMusic = MediaPlayer.create(this, R.raw.bg_music_game)
            bgGameMusic?.isLooping = true
        }
        if (isSoundOn) bgGameMusic?.start()

        loadQuestion()
    }

    // ================================================================
    // 4) LOAD SOAL
    // ================================================================
    private fun loadQuestion() {
        if (currentQuestionIndex >= activeQuestions.size) {
            finishGame()
            return
        }

        val q = activeQuestions[currentQuestionIndex]
        tvLevel.text = "Soal ${currentQuestionIndex + 1}/${activeQuestions.size}"

        imgSoal.setImageResource(q.imageResId)

        val options = (listOf(q.jawabanBenar) + q.opsiLain).shuffled()

        for (i in buttons.indices) {
            val text = options[i]
            val btn = buttons[i]

            btn.text = text
            btn.isEnabled = true
            btn.alpha = 1f
            btn.backgroundTintList = null

            btn.setOnClickListener { checkAnswer(text, q.jawabanBenar) }
        }
    }

    // ================================================================
    // 5) CEK JAWABAN
    // ================================================================
    private fun checkAnswer(selected: String, correct: String) {

        if (selected == correct) {
            score += 10
            if (isSoundOn) soundPool.play(soundBenar, 1f, 1f, 1, 0, 1f)
        } else {
            if (isSoundOn) soundPool.play(soundSalah, 1f, 1f, 1, 0, 1f)
        }

        currentQuestionIndex++
        Handler(mainLooper).postDelayed({ loadQuestion() }, 500)
    }

    // ================================================================
    // 6) FINISH GAME
    // ================================================================
    private fun finishGame() {

        if (isSoundOn) {
            soundPool.play(soundSelesai, 1f, 1f, 1, 0, 1f)
        }

        bgGameMusic?.pause()

        AlertDialog.Builder(this)
            .setTitle("Permainan Selesai!")
            .setMessage("Skor Kamu: $score / 100")
            .setCancelable(false)
            .setPositiveButton("Main Lagi") { _, _ ->
                bgGameMusic?.pause()
                showMenu()
            }
            .show()
    }

    // ================================================================
    // LIFECYCLE
    // ================================================================
    override fun onPause() {
        super.onPause()
        bgMenuMusic?.pause()
        bgGameMusic?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (isSoundOn) {
            if (currentQuestionIndex == 0) {
                bgMenuMusic?.start()
            } else {
                bgGameMusic?.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bgMenuMusic?.release()
        bgGameMusic?.release()
        soundPool.release()
    }
}
