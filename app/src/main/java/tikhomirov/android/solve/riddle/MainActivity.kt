package tikhomirov.android.solve.riddle

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import tikhomirov.android.solve.riddle.databinding.ActivityMainBinding
import kotlin.random.Random

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val ANSWER_REQUEST_CODE = 1 // Уникальный код запроса для ответа на загадку

    // Список загадок и ответов
    private val riddlesAndAnswers = listOf(
        Pair("Что делает переменная в программировании?", "Хранит данные"),
        Pair(
            "Какой язык программирования используется для создания мобильных приложений?",
            "Kotlin"
        ),
//        Pair("Что такое цикл в программировании?", "Повторение действий"),
//        Pair("Что делает оператор '=='?", "Сравнивает значения"),
//        Pair("Что делает функция в программировании?", "Выполняет часть кода"),
//        Pair(
//            "Какой язык программирования часто используется для разработки веб-сайтов",
//            "JavaScript"
//        ),
//        Pair("Что такое комментарий в коде?", "Примечание к коду"),
//        Pair("Что делает оператор '!'?", "Отрицает условия"),
//        Pair("Что значит if в программировании?", "Условие"),
//        Pair("Что такое массив в программировании?", "Список элементов")
    )

    private var currentRiddleIndex = 0 // Переменная для отслеживания количества отгаданных загадок
    private var correctAnswerCount = 0 // Количество правильных ответов
    private var totalCount = riddlesAndAnswers.size // Общее количество загадок

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Восстанавливаем состояние, если оно было сохранено
        savedInstanceState?.let {
            currentRiddleIndex = it.getInt("currentRiddleIndex", 0)
            correctAnswerCount = it.getInt("correctAnswerCount", 0)
            binding.textViewRiddle.text = it.getString("currentRiddle", "")
            binding.textViewUserAnswer.text = it.getString("userAnswer", "")
            binding.textViewResult.text = it.getString("result", "")
            binding.TextViewAnswerLabel.visibility =
                it.getInt("answerLabelVisibility", View.INVISIBLE)
            binding.textViewUserAnswer.visibility =
                it.getInt("userAnswerVisibility", View.INVISIBLE)
            binding.textViewRiddlesCount.visibility =
                it.getInt("riddlesCountVisibility", View.INVISIBLE)
        }

        // Назначаем обработчики нажатия кнопок
        binding.buttonGetRiddle.setOnClickListener {
            onButtonGetRiddleClick()
        }

        binding.buttonStatistics.setOnClickListener {
            onButtonStatisticsClick()
        }

        binding.buttonAnswer.setOnClickListener {
            onButtonAnswerClick()
        }

        binding.buttonNewGame.setOnClickListener {
            onButtonNewGameClick()
        }

        binding.buttonExitApp.setOnClickListener {
            onButtonExitAppClick()
        }

        // Изначально кнопки "Ответ" и "Статистика" отключены
        binding.buttonAnswer.isEnabled = false
        binding.buttonStatistics.isEnabled = false
        binding.buttonGetRiddle.isEnabled = true

        checkButtonsState()

        updateButtonStyles()

        // Обновляем текст с информацией о прогрессе
        updateProgressText()
    }

    // Проверка состояния кнопок в зависимости от прогресса игры
    private fun checkButtonsState() {
        if (currentRiddleIndex >= totalCount) {
            binding.buttonGetRiddle.isEnabled = false
            binding.buttonAnswer.isEnabled = false
            binding.buttonStatistics.isEnabled = true
        } else {
            binding.buttonGetRiddle.isEnabled = true
            binding.buttonAnswer.isEnabled = false
            binding.buttonStatistics.isEnabled = false
        }
    }

    // Сохранение состояния активности перед её пересозданием
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentRiddleIndex", currentRiddleIndex)
        outState.putInt("correctAnswerCount", correctAnswerCount)
        outState.putString("currentRiddle", binding.textViewRiddle.text.toString())
        outState.putString("userAnswer", binding.textViewUserAnswer.text.toString())
        outState.putString("result", binding.textViewResult.text.toString())
        outState.putInt("answerLabelVisibility", binding.TextViewAnswerLabel.visibility)
        outState.putInt("userAnswerVisibility", binding.textViewUserAnswer.visibility)
        outState.putInt("riddlesCountVisibility", binding.textViewRiddlesCount.visibility)
    }

    // Генерация случайной загадки
    private val usedIndexes = mutableListOf<Int>()

    private fun generateNextRiddle(): String {
        var randomIndex = Random.nextInt(0, riddlesAndAnswers.size)
        while (usedIndexes.contains(randomIndex)) {
            randomIndex = Random.nextInt(0, riddlesAndAnswers.size)
        }
        usedIndexes.add(randomIndex)
        if (usedIndexes.size == riddlesAndAnswers.size) {
            usedIndexes.clear() // Если все индексы использованы, очищаем список и начинаем сначала
        }
        return riddlesAndAnswers[randomIndex].first
    }

    // Обработчик нажатия кнопки "Ответ"
    private fun onButtonAnswerClick() {
        val currentRiddle = binding.textViewRiddle.text.toString()
        val answerIntent = Intent(this, AnswerActivity::class.java).apply {
            putExtra("riddle", currentRiddle)
            putExtra("correctAnswer", getCorrectAnswer(currentRiddle))
            putStringArrayListExtra("answers", ArrayList(riddlesAndAnswers.map { it.second }))
        }
        startActivityForResult(answerIntent, ANSWER_REQUEST_CODE)

        currentRiddleIndex++
        updateProgressText()

        checkButtonsState()
//        if (currentRiddleIndex >= totalCount){
//            binding.buttonGetRiddle.backgroundTintList = ContextCompat.getColorStateList(this, R.color.accent_color)
//        }

        // Проверяем, нужно ли отключить кнопку "Ответ" и включить кнопку "Статистика"
        if (currentRiddleIndex == totalCount) {
            binding.buttonGetRiddle.isEnabled = false
            binding.buttonAnswer.isEnabled = false
            binding.buttonStatistics.isEnabled = true
        } else {
            binding.buttonGetRiddle.isEnabled = true
            binding.buttonAnswer.isEnabled = false
        }

        binding.TextViewAnswerLabel.visibility = View.VISIBLE
        binding.textViewUserAnswer.visibility = View.VISIBLE
        binding.textViewRiddlesCount.visibility = View.VISIBLE

        updateButtonStyles()
    }

    // Обработчик нажатия кнопки "Загадка"
    private fun onButtonGetRiddleClick() {
        val randomRiddle = generateNextRiddle()
        binding.textViewRiddle.text = randomRiddle

        binding.buttonAnswer.isEnabled = true
        binding.buttonGetRiddle.isEnabled = false

        binding.textViewResult.text = ""
        binding.textViewUserAnswer.text = ""

        updateButtonStyles()
    }

    // Обработчик нажатия кнопки "Статистика"
    private fun onButtonStatisticsClick() {
        val statisticIntent = Intent(this, StatisticsActivity::class.java).apply {
            putExtra("correctAnswerCount", correctAnswerCount)
            putExtra("totalCount", totalCount)
        }
        startActivity(statisticIntent)
    }

    // Обработчик нажатия кнопки "Новая игра"
    private fun onButtonNewGameClick() {
        currentRiddleIndex = 0
        correctAnswerCount = 0
        updateProgressText()

        binding.buttonGetRiddle.isEnabled = true
        binding.buttonAnswer.isEnabled = false
        binding.buttonStatistics.isEnabled = false

        binding.textViewResult.text = ""
        binding.textViewUserAnswer.text = ""
        binding.textViewRiddle.text = ""

        updateButtonStyles()
    }

    // Обработчик нажатия кнопки "Закрыть приложение"
    private fun onButtonExitAppClick() {
        finish()
    }

    // Обновление стиля кнопок в зависимости от их состояния
    private fun updateButtonStyles() {
        binding.buttonAnswer.backgroundTintList = if (binding.buttonAnswer.isEnabled) {
            ContextCompat.getColorStateList(this, R.color.accent_color)
        } else {
            ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        }

        binding.buttonStatistics.backgroundTintList = if (binding.buttonStatistics.isEnabled) {
            ContextCompat.getColorStateList(this, R.color.accent_color)
        } else {
            ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        }

        binding.buttonGetRiddle.backgroundTintList = if (binding.buttonGetRiddle.isEnabled) {
            ContextCompat.getColorStateList(this, R.color.accent_color)
        } else {
            ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        }
    }

    // Обновление текста с информацией о прогрессе
    private fun updateProgressText() {
        binding.textViewRiddlesCount.text = "Решено: $currentRiddleIndex/$totalCount"
    }

    // Получение правильного ответа по текущей загадке
    private fun getCorrectAnswer(currentRiddle: String): String {
        return riddlesAndAnswers.firstOrNull { it.first == currentRiddle }?.second ?: ""
    }

    // Обработка результата ответа на загадку из AnswerActivity
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ANSWER_REQUEST_CODE) {
            val selectedAnswer = data?.getStringExtra("selectedAnswer")
            if (resultCode == RESULT_OK) {
                binding.textViewUserAnswer.text = selectedAnswer
                binding.textViewResult.text = "Правильно!"
                // Изменение цвета текста на цвет из вашего собственного ресурса цветов
                binding.textViewResult.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.light_green
                    )
                )
                correctAnswerCount++
            } else if (resultCode == RESULT_CANCELED) {
                val result = data?.getStringExtra("result")
                binding.textViewUserAnswer.text = selectedAnswer
                binding.textViewResult.text = result
                binding.textViewResult.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bright_red
                    )
                )
            }
        }
    }
}
