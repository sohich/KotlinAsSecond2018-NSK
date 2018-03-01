@file:Suppress("UNUSED_PARAMETER")
package lesson6.task1

import lesson1.task1.sqr
import java.lang.*
import java.lang.Math.*

/**
 * Точка на плоскости
 */
data class Point(val x: Double = 0.0, val y: Double = 0.0) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point): this(linkedSetOf(a, b, c))
    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point = Point(), val radius: Double = 0.0) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double = max(center.distance(other.center) - (radius + other.radius), 0.0)

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = center.distance(p) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
            other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
            begin.hashCode() + end.hashCode()
}

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
fun diameter(vararg points: Point): Segment {
    if (points.size < 2) {
        throw IllegalArgumentException()
    }

    var maxDistance = 0.0
    var segmentA = Point()
    var segmentB = Point()

    for (i in 1..(points.size - 1))
        for (j in 0..(i - 1)) {
            val a = points[j]
            val b = points[i]
            val dist = a.distance(b)

            if (dist > maxDistance) {
                maxDistance = dist
                segmentA = a
                segmentB = b
            }
        }

    return Segment(segmentA, segmentB)
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val a = diameter.begin
    val b = diameter.end

    val midX = (a.x + b.x) / 2
    val midY = (a.y + b.y) / 2

    val radius = a.distance(b) / 2

    return Circle(Point(midX, midY), radius)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        assert(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double): this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        if (abs(cos(angle)) < 1e-5) {
            return Point(-b, tan(other.angle) * b + other.b)
        }

        if (abs(cos(other.angle)) < 1e-5) {
            return Point(other.b, tan(angle) * other.b + b)
        }

        val x = ( other.b / cos(other.angle) - b / cos(angle)) / (tan(angle) - tan(other.angle))

        return Point(x, tan(angle) * x + b / cos(angle))
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = lineByPoints(s.begin, s.end)

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    var x1 : Double
    var y1 : Double
    var x2 : Double
    var y2 : Double

    if (a.y < b.y) {
        x1 = a.x
        y1 = a.y
        x2 = b.x
        y2 = b.y
    } else {
        x1 = b.x
        y1 = b.y
        x2 = a.x
        y2 = a.y
    }

    if (x1 == x2)
        return Line(a, PI / 2)

    if (y1 == y2)
        return Line(a, 0.0)

    var angle =  atan((y2 - y1) / (x2 - x1))
    if (angle < 0)
        angle += PI

    return Line(a, angle)
}
/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val mid = Point((a.x + b.x) / 2, (a.y + b.y) / 2)
    var angle = lineByPoints(a, b).angle

    if (angle >= PI / 2) angle -= PI / 2
    else angle += PI / 2

    return Line(mid, angle)
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    if (circles.size < 2) {
        throw IllegalArgumentException()
    }

    var minDistance = Double.MAX_VALUE
    var circleA = Circle()
    var circleB = Circle()

    for (i in 1..(circles.size - 1))
        for (j in 0..(i - 1)) {
            val a = circles[j]
            val b = circles[i]
            val dist = a.distance(b)

            if (dist < minDistance) {
                minDistance = dist
                circleA = a
                circleB = b
            }
        }

    return Pair(circleA, circleB)
}

/**
 * Сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(p1: Point, p2: Point, p3: Point): Circle {
    val offset = pow(p2.x, 2.0) + pow(p2.y, 2.0)
    val bc = (pow(p1.x, 2.0) + pow(p1.y, 2.0) - offset) / 2.0
    val cd = (offset - pow(p3.x, 2.0) - pow(p3.y, 2.0)) / 2.0
    val det = (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p2.y)

    if (abs(det) < 1e-5) {
        throw IllegalArgumentException()
    }

    val idet = 1 / det

    val centerX = (bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) * idet
    val centerY = (cd * (p1.x - p2.x) - bc * (p2.x - p3.x)) * idet
    val radius = sqrt(pow(p2.x - centerX, 2.0) + pow(p2.y - centerY, 2.0))

    return Circle(Point(centerX, centerY), radius)
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
fun minContainingCircle(vararg points: Point): Circle = TODO()

