package ru.netology.tetrisnoactivity.helpers

//Первый аргумент - номер строки создаваемого массива , второй - номер столбца сгенерированного массива байтов
//Метод array2dOfByte генерирует и возвращает новый массив с указанными свойствами - вспомогательная функция генерации байтового массива
fun array2dOfByte(sizeOuter: Int, sizeInner: Int): Array<ByteArray> = Array(sizeOuter) {
    ByteArray(
        sizeInner
    )
}