package ru.netology.tetrisnoactivity.models

import ru.netology.tetrisnoactivity.helpers.array2dOfByte

//width целочисленное свойство ,задает ширину генерируемого фрейма -число столбцов в байтовом массиве фрейма
//data список элементов массива в пространстве значений ByteArray
class Frame(private val width: Int) {
    val data: ArrayList<ByteArray> = ArrayList()

    //Обрабатывет строку,преобразуя каждый отдельный символ строки в байтовое представление и доюавляет байтовое представление в байтовый массив
    //После чего байтовый массив добавляется в список данных
    fun addRow(byteStr: String): Frame {
        val row = ByteArray(
            byteStr.length
        )
        for (index in byteStr.indices) {
            row[index] = "${byteStr[index]}".toByte()
        }
        data.add(row)
        return this
    }

    fun as2dByteArray(): Array<ByteArray> {
        val bytes = array2dOfByte(data.size, width)
        return data.toArray(bytes)
    }
}