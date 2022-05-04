package ru.netology.tetrisnoactivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.tetrisnoactivity.storage.AppPreferences

class MainActivity : AppCompatActivity() {

    var tvHighScore: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val btnNewGame = findViewById<Button>(R.id.btn_new_game)
        val btnResetScore = findViewById<Button>(R.id.btn_reset_score)
        val btnExit = findViewById<Button>(R.id.btn_exit)
        tvHighScore = findViewById<TextView>(R.id.tv_high_score)

        btnNewGame.setOnClickListener(this::onBtnNewGameClick)
        btnResetScore.setOnClickListener(this::onBtnResetScoreClick)
        btnExit.setOnClickListener(this::onBtnExitClick)

        updateHighScore()
    }

    private fun onBtnNewGameClick(view: View) {
        //Новый экземпляр класса Intent
        //Передает конструктору текущий контекст и требуемый класс действия
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n")
    private fun onBtnResetScoreClick(view: View) {
        val preferences = AppPreferences(this)
        preferences.clearHighScore()
        Snackbar.make(
            view, "Score successfully reset",
            Snackbar.LENGTH_SHORT
        ).show()
        tvHighScore?.text = "High score: ${preferences.getHighScore()}"
    }

    @SuppressLint("SetTextI18n")
    fun updateHighScore() {
        val preferences = AppPreferences(this)
        tvHighScore?.text = "High score: ${preferences.getHighScore()}"
    }

    private fun onBtnExitClick(view: View) {
        //рекращает дальнейшее выполнение программы и завершает ее,если в качестве аргумента передается целое число 0
        System.exit(0)
    }
}