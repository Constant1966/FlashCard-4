package com.example.flashcard_4


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class AddCardActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        val editTextField = findViewById<EditText>(R.id.question)
        val editTextField1 = findViewById<EditText>(R.id.answer)
        val ShowingAnswers = findViewById<ImageView>(R.id.cross_sign)
        val answers = findViewById<ImageView>(R.id.save_button)

        ShowingAnswers.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            overridePendingTransition(R.anim.left_out, R.anim.right_in)

            startActivity(intent)

        }

        answers.setOnClickListener {
            val question = editTextField.text.toString()
            val answer = editTextField1.text.toString()

            if (question.isNotEmpty() && answer.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra("question", question)
                intent.putExtra("answer", answer)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Snackbar.make(
                    findViewById<TextView>(R.id.flashcard_question),
                    "Please enter both a question and an answer.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }






    }
}