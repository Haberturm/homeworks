package com.haberturm.homeworks.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.haberturm.homeworks.R
import com.haberturm.homeworks.util.Util
import com.haberturm.homeworks.util.Util.dp2px
import org.jetbrains.annotations.Nullable
import java.util.*


class Clock(context: Context, @Nullable attrs: AttributeSet) :
    View(context, attrs) {

    //все нули успевают проинициализироваться, поэтому, кажется, это безопасно так объявлять в данном случае
    private val clockCircleWidth = 20f //ширина обода часов
    private var clockRadius = 0f
    private var viewWidth = 0
    private var viewHeight = 0
    private var centreCoordinate = Pair<Int, Int>(0, 0) //first - x, second - y

    //ширина стрелок
    private var hoursArrowWidth = 0f
    private var minutesArrowWidth = 0f
    private var secondsArrowWidth = 0f

    //длина стрелок
    private var hoursArrowLength = 0f
    private var minutesArrowLength = 0f
    private var secondsArrowLength = 0f

    // цвета
    private var painter: Paint? = null
    private var hoursArrowColor = 0
    private var minutesArrowColor = 0
    private var secondsArrowColor = 0

    private var illegalArrowSize = false  //false - безопасный режим, true - стрелкам можно назначить любое значение ширины и длины

    //угол на который повернута стрелка
    private var hoursArrowAngle = 0f
    private var minutesArrowAngle = 0f
    private var secondsArrowAngle = 0f


    private val timer = Timer()
    private val task: TimerTask = object : TimerTask() {
        override fun run() {
            calculateArrowsAngle()
            postInvalidate()
        }
    }

    init {
        initPainter()
        getAttrs(context, attrs)
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        initArrowsAngle(hours, minutes, seconds)
        timer.schedule(task, 0, 1000)  //каждую секунду обновляет угол поворота стрелки
    }

    private fun getAttrs(context: Context, attributeSet: AttributeSet) {
        val attrsTypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.Clock)
        hoursArrowWidth =
            attrsTypedArray.getDimension(
                R.styleable.Clock_hours_arrow_width,
                dp2px(context, 8f).toFloat()
            )
        minutesArrowWidth =
            attrsTypedArray.getDimension(
                R.styleable.Clock_minutes_arrow_width,
                dp2px(context, 4f).toFloat()
            )
        secondsArrowWidth =
            attrsTypedArray.getDimension(
                R.styleable.Clock_seconds_arrow_width,
                dp2px(context, 2f).toFloat()
            )
        hoursArrowLength =
            attrsTypedArray.getDimension(
                R.styleable.Clock_hours_arrow_length,
                dp2px(context, 70f).toFloat()
            )
        minutesArrowLength =
            attrsTypedArray.getDimension(
                R.styleable.Clock_minutes_arrow_length,
                dp2px(context, 85f).toFloat()
            )
        secondsArrowLength =
            attrsTypedArray.getDimension(
                R.styleable.Clock_seconds_arrow_length,
                dp2px(context, 105f).toFloat()
            )
        hoursArrowColor =
            attrsTypedArray.getColor(R.styleable.Clock_hours_arrow_color, Color.BLACK)
        minutesArrowColor =
            attrsTypedArray.getColor(R.styleable.Clock_minutes_arrow_color, Color.BLUE)
        secondsArrowColor =
            attrsTypedArray.getColor(R.styleable.Clock_seconds_arrow_color, Color.RED)

        illegalArrowSize =
            attrsTypedArray.getBoolean(R.styleable.Clock_illegal_arrow_size, false)

        attrsTypedArray.recycle()
    }

    private fun initArrowsAngle(h: Int, m: Int, s: Int) {
        hoursArrowAngle = (h + m * 1.0f / 60f + s * 1.0f / 3600f) * 30f - 180
        minutesArrowAngle = (m + s * 1.0f / 60f) * 6f - 180
        secondsArrowAngle = s * 6f - 180
    }

    private fun initPainter() {
        painter = Paint()
        painter!!.isAntiAlias = true
        painter!!.style = Paint.Style.STROKE

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = getSize(Util.WIDTH, widthMeasureSpec)
        val height = getSize(Util.HEIGHT, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun getSize(type: String, measureSpec: Int): Int {
        return when (MeasureSpec.getMode(measureSpec)) {
            MeasureSpec.UNSPECIFIED -> {
                if (type == Util.HEIGHT) {
                    suggestedMinimumHeight
                } else {
                    suggestedMinimumWidth
                }
            }
            MeasureSpec.AT_MOST -> Util.fail("'wrap_content' lead to unpredictable consequences") //кидает exception при wrap_content. Для конкретно этого CustomView, wrap_content - нежалательное значение
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(measureSpec)
            else -> Util.fail("something went wrong...") //недостижимая точка при нормальной работе, если уж попали сюда, то что то пошло не так...
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        centreCoordinate = Pair(w / 2, h / 2)
        clockRadius = w / 2 - clockCircleWidth //'- clockCircleWidth' чтобы не обрезало, часть обода
        /*
        * Считаю, что длина стрелки не должна выходить за обод часов,
        * а ширина была приемлемых размеров
        * Об этом предупредил в документации к атрибутам, чтобы не было неожиданностей
        * */
        if (illegalArrowSize == false) {
            changeIllegalArrowSizeIfNeeded()

        }
    }

    private fun changeIllegalArrowSizeIfNeeded() {
        if (hoursArrowLength > clockRadius) {
            hoursArrowLength = clockRadius
        }
        if (minutesArrowLength > clockRadius) {
            minutesArrowLength = clockRadius
        }
        if (secondsArrowLength > clockRadius) {
            secondsArrowLength = clockRadius
        }
        if (hoursArrowWidth > viewWidth / 6f) { // 6 - магическая константа, экспериминтально вычислил, что это послнднее приемлимое соотношение
            secondsArrowWidth = viewWidth / 6f
        }
        if (minutesArrowWidth > viewWidth / 6f) {
            secondsArrowWidth = viewWidth / 6f
        }
        if (secondsArrowWidth > viewWidth / 6f) {
            secondsArrowWidth = viewWidth / 6f
        }
    }
    //изменяет угол поворота стрелок
    private fun calculateArrowsAngle() {
        if (secondsArrowAngle == 360f) {
            secondsArrowAngle = 0f
        }
        if (minutesArrowAngle == 360f) {
            minutesArrowAngle = 0f
        }
        if (hoursArrowAngle == 360f) {
            hoursArrowAngle = 0f
        }
        secondsArrowAngle += 6
        minutesArrowAngle += 0.1f
        hoursArrowAngle += 1.0f / 120
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // начало координат в центр часов
        canvas.translate(
            centreCoordinate.first.toFloat(),
            centreCoordinate.second.toFloat()
        )
        drawClockCircle(canvas)
        drawArrows(canvas)
    }

    private fun drawClockCircle(canvas: Canvas) {
        painter!!.strokeWidth = clockCircleWidth
        painter!!.color = Color.BLACK
        canvas.drawCircle(0f, 0f, clockRadius, painter!!)

        val dashWidth = 20f //ширина риски часов
        val dashLength = 40f //длина риски часов
        repeat(12) {
            painter!!.strokeWidth = dashWidth
            painter!!.color = Color.BLACK
            canvas.drawLine(
                0f, -clockRadius + clockCircleWidth / 2, 0f, -clockRadius + dashLength,
                painter!!
            )
            canvas.rotate(30f)
        }
    }

    private fun drawArrows(canvas: Canvas) {
        // часовая стрелка
        canvas.save()
        painter!!.color = hoursArrowColor
        painter!!.strokeWidth = hoursArrowWidth
        canvas.rotate(hoursArrowAngle, 0f, 0f)
        canvas.drawLine(
            0f, -60f, 0f,
            hoursArrowLength, painter!!
        )
        canvas.restore()

        // минутная стрелка
        canvas.save()
        painter!!.color = minutesArrowColor
        painter!!.strokeWidth = minutesArrowWidth
        canvas.rotate(minutesArrowAngle, 0f, 0f)
        canvas.drawLine(
            0f, -60f, 0f,
            minutesArrowLength, painter!!
        )
        canvas.restore()

        //секундная стрелка
        canvas.save()
        painter!!.color = secondsArrowColor
        painter!!.strokeWidth = secondsArrowWidth
        canvas.rotate(secondsArrowAngle, 0f, 0f)
        canvas.drawLine(
            0f, -60f, 0f,
            secondsArrowLength, painter!!
        )
        canvas.restore()
    }
}