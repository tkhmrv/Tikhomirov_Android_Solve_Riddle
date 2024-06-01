package tikhomirov.android.solve.riddle

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import tikhomirov.android.solve.riddle.databinding.ActivityAnswerBinding

class AnswerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnswerBinding
    private lateinit var answers: List<String>
    private lateinit var correctAnswer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем данные из Intent
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            answers = bundle.getStringArrayList("answers")?.shuffled() ?: listOf()
        }

        // Получаем загадку и отображаем её
        val riddle = intent.getStringExtra("riddle")
        binding.textViewRiddle.text = riddle

        correctAnswer = intent.getStringExtra("correctAnswer").toString()

        // Устанавливаем варианты ответов на радиокнопки
        setupOptions()

        // Устанавливаем слушатель на кнопку "Проверка"
        binding.buttonCheck.setOnClickListener {
            checkAnswer()
        }
    }

    // Метод для установки вариантов ответов на радиокнопки
    private fun setupOptions() {
        // Перемешиваем список ответов
        val options = answers.shuffled()

        // Устанавливаем варианты ответов на радиокнопки
        for ((index, option) in options.withIndex()) {
            val radioButton = RadioButton(this)
            radioButton.id = index
            radioButton.text = option
            binding.radioGroupOptions.addView(radioButton)
        }
    }

    // Метод для проверки ответа пользователя
    private fun checkAnswer() {
        val checkedRadioButtonId = binding.radioGroupOptions.checkedRadioButtonId
        if (checkedRadioButtonId != -1) {
            val selectedOption = findViewById<RadioButton>(checkedRadioButtonId).text.toString()

            if (selectedOption == correctAnswer) {
                // Устанавливаем результат "Правильно!" и возвращаем его в MainActivity
                val resultIntent = Intent().apply {
                    putExtra("result", "Правильно!")
                    putExtra("selectedAnswer", selectedOption)
                }
                setResult(Activity.RESULT_OK, resultIntent)
            } else {
                // Устанавливаем результат "Неправильно!" и возвращаем его в MainActivity
                val resultIntent = Intent().apply {
                    putExtra("result", "Неправильно!\nПравильный ответ: $correctAnswer")
                    putExtra("selectedAnswer", selectedOption)
                }
                setResult(Activity.RESULT_CANCELED, resultIntent)
            }
            finish()
        } else {
            // Если пользователь не выбрал ответ
            binding.textViewResult.text = "Выберите ответ!"
            binding.textViewResult.visibility = View.VISIBLE
        }
    }
}
