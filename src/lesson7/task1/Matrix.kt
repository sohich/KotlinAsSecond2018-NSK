@file:Suppress("UNUSED_PARAMETER", "unused")
package lesson7.task1

/**
 * Ячейка матрицы: row = ряд, column = колонка
 */
data class Cell(val row: Int, val column: Int)

/**
 * Интерфейс, описывающий возможности матрицы. E = тип элемента матрицы
 */
interface Matrix<E> {
    /** Высота */
    val height: Int

    /** Ширина */
    val width: Int

    /**
     * Доступ к ячейке.
     * Методы могут бросить исключение, если ячейка не существует или пуста
     */
    operator fun get(row: Int, column: Int): E
    operator fun get(cell: Cell): E

    /**
     * Запись в ячейку.
     * Методы могут бросить исключение, если ячейка не существует
     */
    operator fun set(row: Int, column: Int, value: E)
    operator fun set(cell: Cell, value: E)
}

/**
 * Простая
 *
 * Метод для создания матрицы, должен вернуть РЕАЛИЗАЦИЮ Matrix<E>.
 * height = высота, width = ширина, e = чем заполнить элементы.
 * Бросить исключение IllegalArgumentException, если height или width <= 0.
 */
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> = MatrixImpl(height, width, e)

/**
 * Средняя сложность
 *
 * Реализация интерфейса "матрица"
 */
class MatrixImpl<E> (override val height : Int, override val width : Int, pDefaultValue : E) : Matrix<E> {
    private var values : MutableList<MutableList<E>>

    init {
        if (width <= 0 || height <= 0)
            throw IllegalArgumentException()

        values  = MutableList(height, {_ -> MutableList(width, {_ -> pDefaultValue})})
    }

    override fun get(row: Int, column: Int): E  = values[row][column]

    override fun get(cell: Cell): E  = values[cell.row][cell.column]

    override fun set(row: Int, column: Int, value: E) {
        values[row][column] = value
    }

    override fun set(cell: Cell, value: E) {
        values[cell.row][cell.column] = value
    }

    override fun toString(): String = "Matrix $width x $height"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MatrixImpl<*>

        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int = values.hashCode()
}


