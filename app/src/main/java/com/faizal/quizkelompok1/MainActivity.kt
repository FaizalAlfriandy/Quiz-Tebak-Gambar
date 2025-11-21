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

data class SoalTebakGambar(
    val imageResId: Int,
    val jawabanBenar: String,
    val opsiLain: List<String>
)

class MainActivity : AppCompatActivity() {

    // UI GAME
    private lateinit var imgSoal: ImageView
    private lateinit var tvLevel: TextView
    private lateinit var buttons: List<Button>
    private lateinit var imgFeedback: ImageView

    // GAME LOGIC
    private val allQuestions = ArrayList<SoalTebakGambar>()
    private var activeQuestions = ArrayList<SoalTebakGambar>()
    private var currentQuestionIndex = 0
    private var score = 0

    // AUDIO
    private var bgMenuMusic: MediaPlayer? = null
    private var bgGameMusic: MediaPlayer? = null
    private var isSoundOn = true
    private lateinit var prefs: SharedPreferences
    private lateinit var soundPool: SoundPool
    private var soundBenar = 0
    private var soundSalah = 0
    private var soundSelesai = 0
    private var soundTransisi = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("settings", MODE_PRIVATE)
        isSoundOn = prefs.getBoolean("sound", true)

        // SoundPool
        soundPool = SoundPool.Builder().setMaxStreams(6).build()
        soundBenar = soundPool.load(this, R.raw.sound_benar, 1)
        soundSalah = soundPool.load(this, R.raw.sound_salah, 1)
        soundSelesai = soundPool.load(this, R.raw.selesai_sound, 1)
        soundTransisi = soundPool.load(this, R.raw.transisi_sound, 1)

        showMenu()
    }

    // =================== MENU ===================
    private lateinit var btnAyoMain: ImageButton
    private lateinit var btnSound: ImageButton
    private lateinit var layoutMenu: View

    private fun showMenu() {
        setContentView(R.layout.activity_main)

        layoutMenu = findViewById(R.id.mainMenuLayout)
        btnAyoMain = findViewById(R.id.btnAyoMain)
        btnSound = findViewById(R.id.btnSound)

        btnAyoMain.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_in_out))

        if (bgMenuMusic == null) {
            bgMenuMusic = MediaPlayer.create(this, R.raw.bg_music_menu)
            bgMenuMusic?.isLooping = true
        }

        updateSoundIcon()
        if (isSoundOn) bgMenuMusic?.start() else bgMenuMusic?.pause()

        btnSound.setOnClickListener { toggleSound() }

        btnAyoMain.setOnClickListener {
            if (isSoundOn) soundPool.play(soundTransisi, 1f, 1f, 1, 0, 1f)
            bgMenuMusic?.pause()
            prepareGameData() // shuffle soal setiap mulai game
            startGame()
        }
    }

    private fun updateSoundIcon() {
        btnSound.setImageResource(if (isSoundOn) R.drawable.ic_sound_on else R.drawable.ic_sound_off)
    }

    private fun toggleSound() {
        isSoundOn = !isSoundOn
        prefs.edit().putBoolean("sound", isSoundOn).apply()
        if (isSoundOn) bgMenuMusic?.start() else bgMenuMusic?.pause()
        updateSoundIcon()
    }

    // =================== GAME DATA ===================
    private fun prepareGameData() {
        allQuestions.clear()
        allQuestions.add(
            SoalTebakGambar(
                R.drawable.asam_lambung,
                "ASAM LAMBUNG",
                listOf("MAAG", "PERUT KEMBUNG", "ASAM PERUT")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.bencana_susulan,
                "BENCANA SUSULAN",
                listOf("GEMPA SUSULAN", "BANJIR SUSULAN", "PEROBAHAN CUACA")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.bisikan_jahat,
                "BISIKAN JAHAT",
                listOf("GODHAAN BURUK", "BISIKAN SETAN", "NIAT JELEK")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.jurus_andalan,
                "JURUS ANDALAN",
                listOf("GERAKAN UTAMA", "JURUS PAMUNGKAS", "GERAKAN ANDAL")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.kipas_angin,
                "KIPAS ANGIN",
                listOf("KIPAS ANGIN MINI", "KIPAS LISTRIK", "PENDINGIN ANGIN")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.kostum_matador,
                "KOSTUM MATADOR",
                listOf("BAJU TORERO", "PAKAIAN ARENA", "BAJU PENANTANG")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.meminta_imbalan,
                "MEMINTA IMBALAN",
                listOf("MEMINTA BALASAN", "MENGHARAP UPAH", "MINTA HADIAH")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.motif_garis,
                "MOTIF GARIS",
                listOf("POLA GARIS", "GARIS POLOS", "DESAIN GARIS")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.penduduk_paris,
                "PENDUDUK PARIS",
                listOf("ORANG PARIS", "WARGA PARIS", "PENDUDUK PERANCIS")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.pulau_seribu,
                "PULAU SERIBU",
                listOf("KEPULAUAN SERIBU", "PULAU-PULAU", "PULAU KECIL")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.ruangan_terbuka,
                "RUANGAN TERBUKA",
                listOf("RUANG TERBUKA", "AREA TERBUKA", "TEMPAT TERBUKA")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.sajian_hajatan,
                "SAJIAN HAJATAN",
                listOf("MAKANAN HAJATAN", "MENU PESTA", "HIDANGAN ACARA")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.tahan_banting,
                "TAHAN BANTING",
                listOf("KUAT BANTING", "ANTI BANTING", "SUSAH RUSAK")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.teks_pancasila,
                "TEKS PANCASILA",
                listOf("ISI PANCASILA", "NASKAH PANCASILA", "TEKS KEBANGSAAN")
            )
        )

        allQuestions.add(
            SoalTebakGambar(
                R.drawable.toko_seragam,
                "TOKO SERAGAM",
                listOf("TOKO PAKAIAN", "PENJAHIT SERAGAM", "TOKO JAHIT")
            )
        )
        // Ambil 10 soal random
        activeQuestions = ArrayList(allQuestions.shuffled().take(10))
    }

    // =================== START GAME ===================
    private fun startGame() {
        setContentView(R.layout.activity_game)

        imgSoal = findViewById(R.id.imgSoal)
        tvLevel = findViewById(R.id.tvLevel)
        imgFeedback = findViewById(R.id.imgFeedback)

        buttons = listOf(
            findViewById(R.id.btnOptionA),
            findViewById(R.id.btnOptionB),
            findViewById(R.id.btnOptionC),
            findViewById(R.id.btnOptionD)
        )

        currentQuestionIndex = 0
        score = 0

        if (bgGameMusic == null) {
            bgGameMusic = MediaPlayer.create(this, R.raw.bg_music_game)
            bgGameMusic?.isLooping = true
        }
        if (isSoundOn) bgGameMusic?.start()

        loadQuestion()
    }

    // =================== LOAD SOAL ===================
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
            val btn = buttons[i]
            btn.text = options[i]
            btn.isEnabled = true
            btn.alpha = 1f
            btn.backgroundTintList = null
            btn.setOnClickListener { checkAnswer(options[i], q.jawabanBenar) }
        }
    }

    // =================== CEK JAWABAN ===================
    private fun checkAnswer(selected: String, correct: String) {
        // Tampilkan feedback overlay
        if (selected == correct) {
            score += 10
            if (isSoundOn) soundPool.play(soundBenar, 1f, 1f, 1, 0, 1f)
            imgFeedback.setImageResource(R.drawable.benar)
        } else {
            if (isSoundOn) soundPool.play(soundSalah, 1f, 1f, 1, 0, 1f)
            imgFeedback.setImageResource(R.drawable.salah)
        }

        imgFeedback.visibility = View.VISIBLE
        buttons.forEach { it.isEnabled = false }

        // Lanjut soal berikutnya setelah 1 detik
        Handler(mainLooper).postDelayed({
            imgFeedback.visibility = View.GONE
            currentQuestionIndex++
            loadQuestion()
        }, 1000)
    }

    // =================== FINISH GAME ===================
    private fun finishGame() {
        if (isSoundOn) soundPool.play(soundSelesai, 1f, 1f, 1, 0, 1f)
        bgGameMusic?.pause()

        AlertDialog.Builder(this)
            .setTitle("Permainan Selesai!")
            .setMessage("Skor Kamu: $score / 100")
            .setCancelable(false)
            .setPositiveButton("Main Lagi") { _, _ ->
                showMenu()
            }
            .show()
    }

    // =================== LIFECYCLE ===================
    override fun onPause() {
        super.onPause()
        bgMenuMusic?.pause()
        bgGameMusic?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (isSoundOn) {
            if (currentQuestionIndex == 0) bgMenuMusic?.start()
            else bgGameMusic?.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bgMenuMusic?.release()
        bgGameMusic?.release()
        soundPool.release()
    }
}
