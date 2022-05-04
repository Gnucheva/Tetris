package ru.netology.tetrisnoactivity.constants

//Константы для числа столбцов и строк игрового поля
//Поле имеет 10 столбцов и 20 строк
enum class FieldConstants(val value: Int) {
    COLUMN_COUNT(10), ROW_COUNT(20);
}