package ru.netology.tetrisnoactivity.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(ctx: Context) {
    var data: SharedPreferences = ctx.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

    //Принимает целое число
    fun saveHighScore(highScore: Int) {
        data.edit().putInt("HIGH_SCORE", highScore)
            .apply() //putInt вызывается для сохранения целого числа в файле настроек
    }

    fun getHighScore(): Int {
        return data.getInt("HIGH_SCORE", 0)
    }

    fun clearHighScore() {
        data.edit().putInt("HIGH_SCORE", 0).apply()
    }
}