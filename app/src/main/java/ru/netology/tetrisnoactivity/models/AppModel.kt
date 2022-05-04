package ru.netology.tetrisnoactivity.models

import android.graphics.Point
import ru.netology.tetrisnoactivity.constants.CellConstants
import ru.netology.tetrisnoactivity.constants.FieldConstants
import ru.netology.tetrisnoactivity.helpers.array2dOfByte
import ru.netology.tetrisnoactivity.storage.AppPreferences

class AppModel() {

    var score: Int = 0 //Текущее сохранение счета игрока в игровом сеансе
    private var preferences: AppPreferences? =
        null //Свойство , которое содержит объект AppPreferences для доступа к файлу SharedPreferences приложения

    var currentBlock: Block? = null //Содержит текущий блок трансляции через игровое поле
    var currentState: String = Statuses.AWAITING_START.name //Состояние игры

    private var field: Array<ByteArray> =
        array2dOfByte( //Двумерный массив ,служащий в качестве игрового поля
            FieldConstants.ROW_COUNT.value,
            FieldConstants.COLUMN_COUNT.value
        )

    //Генерирует обновление поля
    fun generateField(action: String) {
        if (isGameActive()) {
            resetField()

            var frameNumber: Int? = currentBlock?.frameNumber
            val coordinate: Point? = Point()
            coordinate?.x = currentBlock?.position?.x
            coordinate?.y = currentBlock?.position?.y

            when (action) {
                Motions.LEFT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.minus(1)
                }
                Motions.RIGHT.name -> {
                    coordinate?.x = currentBlock?.position?.x?.plus(1)
                }
                Motions.DOWN.name -> {
                    coordinate?.y = currentBlock?.position?.y?.plus(1)
                }
                Motions.ROTATE.name -> {
                    frameNumber = frameNumber?.plus(1)

                    if (frameNumber != null) {
                        if (frameNumber >= currentBlock?.frameCount as Int) {
                            frameNumber = 0
                        }
                    }
                }
            }

            if (!moveValid(coordinate as Point, frameNumber)) {
                translateBlock(currentBlock?.position as Point, currentBlock?.frameNumber as Int)

                if (Motions.DOWN.name == action) {
                    boostScore()
                    persistCellData()
                    assessField()
                    generateNextBlock()

                    if (!blockAdditionPossible()) {
                        currentState = Statuses.OVER.name;
                        currentBlock = null;
                        resetField(false);
                    }
                }

            } else {
                if (frameNumber != null) {
                    translateBlock(coordinate, frameNumber)
                    currentBlock?.setState(frameNumber, coordinate)
                }
            }
        }
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter { !ephemeralCellsOnly || field[i][it] == CellConstants.EPHEMERAL.value }
                .forEach { field[i][it] = CellConstants.EMPTY.value }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber)

            if (shape != null) {
                // All cell is correct - add the data:
                for (i in shape.indices) {
                    for (j in 0 until shape[i].size) {
                        val y = position.y + i
                        val x = position.x + j
                        if (CellConstants.EMPTY.value != shape[i][j]) {
                            field[y][x] = shape[i][j]
                        }
                    }
                }
            }
        }
    }

    //Разрешен ли выполненный игроком ход
    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        val shape: Array<ByteArray>? = currentBlock?.getShape(frameNumber as Int)
        return validTranslation(position, shape as Array<ByteArray>)
    }

    //Функция служит для проверки допустимости поступательного движения тетрамино в игровом поле на основе набора условий
    //true- если трансляция корректна
    //Первые три условия проверяют,находится ли в поле позиция , в которую переводится тетрамино
    //Блок else проверяет , свободны ли клетки , в которые пытается перейти тетрамино
    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) {
            false
        } else {
            // Check all the items in field:
            for (i in 0 until shape.size) {
                for (j in 0 until shape[i].size) {

                    val y = position.y + i
                    val x = position.x + j

                    if (CellConstants.EMPTY.value != shape[i][j] &&
                        CellConstants.EMPTY.value != field[y][x]
                    ) {
                        return false
                    }
                }
            }
            true
        }
    }

    //После завершения оценки поля создается новый блок
    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
    }

    //Провнерка , что поле еще не заполнено , а блок может перемещаться в поле
    private fun blockAdditionPossible(): Boolean {
        // Check the validity of new block:
        if (!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber)) {
            // GAME IS OVER!
            return false
        }
        return true
    }

    //Состояния всех ячеек поля сохраняются
    private fun persistCellData() {
        // set all the dynamic data as static
        for (i in 0 until field.size) {
            for (j in 0 until field[i].size) {
                var status = getCellStatus(i, j)

                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.staticValue
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    //Для построчного сканирования строк поля и проверки заполняемости находящихся в строках ячеек
    private fun assessField() {
        for (i in 0 until field.size) {
            var emptyCells = 0;

            for (j in 0 until field[i].size) {
                val status = getCellStatus(i, j)
                val isEmpty = CellConstants.EMPTY.value == status

                if (isEmpty)
                    emptyCells++
            }
            if (emptyCells == 0)
                shiftRows(i)
        }
    }

    //Если в строке заполнены все ячейки , строка очищается и сдвигается на величину, определенную с помощью метода shiftRows
    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0) {
                for (m in 0 until field[j].size) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }
        for (j in 0 until field[0].size) {
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    //Устанавливает свойство предпочтений для AppModel, что позволяет передавать экземпляр класса AppPreferences в виде аргументв этой функции
    fun setPreferences(preferences: AppPreferences?) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte? {
        return field[row][column]
    }

    //Устанавливает состояние имеющейся в поле ячейки равным указанному байту
    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) {
            field[row][column] = status
        }
    }

    fun startGame() {
        if (!isGameActive()) {
            currentState = Statuses.ACTIVE.name
            generateNextBlock()
        }
    }

    fun restartGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        score = 0
        currentState = AppModel.Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
    }

    //Увеличивается текущий счет игрока +10, после чего проверяется , не превышает ли текущий счет установленный рекорд,записанный в файле настроек .Если больше то переписывается
    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int)
            preferences?.saveHighScore(score)
    }

    fun isGameOver(): Boolean {
        return currentState == Statuses.OVER.name
    }

    fun isGameActive(): Boolean {
        return currentState == Statuses.ACTIVE.name
    }

    fun isGameAwaitingStart(): Boolean {
        return currentState == Statuses.AWAITING_START.name

    }

    enum class Statuses {
        AWAITING_START, //Представление игры до ее запуска
        ACTIVE,
        OVER //Статус принимаемый игрой на момент ее завершения
    }

    enum class Motions {
        LEFT,
        RIGHT,
        DOWN,
        ROTATE
    }
}





