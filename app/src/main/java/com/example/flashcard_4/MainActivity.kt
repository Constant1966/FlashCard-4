package com.example.flashcard_4


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    lateinit var flashcardDatabase: FlashcardDatabase
    var allFlashcards = mutableListOf<Flashcard>()

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val isShowingAnswers = findViewById<ImageView>(R.id.image_plus_cercle)
        val flashcard_question = findViewById<TextView>(R.id.flashcard_question)
        val flashcard_answer = findViewById<TextView>(R.id.flashcard_answer)

        val answerSideView = findViewById<View>(R.id.flashcard_answer)
        val questionSideView = findViewById<View>(R.id.flashcard_question)




        val cx = answerSideView.width / 2
        val cy = answerSideView.height / 2

        val leftOutAnim = AnimationUtils.loadAnimation(this, R.anim.left_out)
        val rightInAnim = AnimationUtils.loadAnimation(this, R.anim.right_in)

        leftOutAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                // this method is called when the animation first starts

                findViewById<TextView>(R.id.flashcard_question).startAnimation(leftOutAnim)
            }

            override fun onAnimationEnd(animation: Animation?) {
                // this method is called when the animation is finished playing
                findViewById<TextView>(R.id.flashcard_answer).startAnimation(rightInAnim)

            }

            override fun onAnimationRepeat(animation: Animation?) {
                // we don't need to worry about this method
            }
        })

        flashcardDatabase = FlashcardDatabase(this)
        allFlashcards = flashcardDatabase.getAllCards().toMutableList()
        if (allFlashcards.size > 0) {
            findViewById<TextView>(R.id.flashcard_question).text = allFlashcards[0].question
            findViewById<TextView>(R.id.flashcard_answer).text = allFlashcards[0].answer
        }
        flashcardDatabase.initFirstCard()



        // All Listeners

            flashcard_question.setOnClickListener {
            flashcard_question.visibility = View.INVISIBLE
            flashcard_answer.visibility = View.VISIBLE


            val finalRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()
            val anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius)



            anim.duration = 500
            anim.start()




            }
        flashcard_answer.setOnClickListener {
            flashcard_question.visibility = View.VISIBLE
            flashcard_answer.visibility = View.INVISIBLE
        }

        isShowingAnswers.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            startActivity(intent)
        }

        flashcard_question.startAnimation(rightInAnim)
        flashcard_answer.startAnimation(rightInAnim)
        var currentCardDisplayedIndex = 0

        findViewById<View>(R.id.next_button).setOnClickListener {
            // don't try to go to next card if you have no cards to begin with
            if (allFlashcards.size == 0) {
                // return here, so that the rest of the code in this onClickListener doesn't execute
                return@setOnClickListener
            }

            flashcard_question.visibility = View.VISIBLE
            flashcard_answer.visibility = View.INVISIBLE




            // advance our pointer index so we can show the next card
            currentCardDisplayedIndex++


            // make sure we don't get an IndexOutOfBoundsError if we are viewing the last indexed card in our list
            if(currentCardDisplayedIndex >= allFlashcards.size) {
                Snackbar.make(
                    findViewById<TextView>(R.id.flashcard_question), // This should be the TextView for displaying your flashcard question
                    "You've reached the end of the cards, going back to start.",
                    Snackbar.LENGTH_SHORT)
                    .show()
                currentCardDisplayedIndex = 0
            }

            // set the question and answer TextViews with data from the database
            allFlashcards = flashcardDatabase.getAllCards().toMutableList()
            val (question, answer) = allFlashcards[currentCardDisplayedIndex]

            findViewById<TextView>(R.id.flashcard_answer).text = answer
            findViewById<TextView>(R.id.flashcard_question).text = question

            findViewById<TextView>(R.id.flashcard_question).startAnimation(leftOutAnim)
//            flashcard_answer.visibility = View.INVISIBLE

        }





        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val extras = data?.extras

            if (extras != null) { // Check that we have data returned
                val question = extras.getString("question")
                val answer = extras.getString("answer")

                // Log the value of the strings for easier debugging
                Log.i("MainActivity", "question: $question")
                Log.i("MainActivity", "answer: $answer")

                // Display newly created flashcard
                findViewById<TextView>(R.id.flashcard_question).text = question
                findViewById<TextView>(R.id.flashcard_answer).text = answer

                // Save newly created flashcard to database
                if (question != null && answer != null) {
                    flashcardDatabase.insertCard(Flashcard(question, answer))
                    // Update set of flashcards to include new card
                    allFlashcards = flashcardDatabase.getAllCards().toMutableList()
                } else {
                    Log.e("TAG", "Missing question or answer to input into database. Question is $question and answer is $answer")
                }
            } else {
                Log.i("MainActivity", "Returned null data from AddCardActivity")
            }
        }

// Lancer MainActivity en attente d'un résultat
        isShowingAnswers.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(intent)


            val i = Intent(this, AddCardActivity::class.java)
            resultLauncher.launch(i)
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }

    }

}