package com.example.nadiairwanto.flashcard;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final int ADD_CARD_RESET_CODE = 100;
    private final int EDIT_CARD_REQUEST_CODE = 101;

    FlashcardDatabase flashcardDatabase;
    List<Flashcard> allFlashcards;
    int currentCardDisplayedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize database and get flashcards
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

        // show first flashcard if flashcards exist
        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(0).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(0).getAnswer());
        }

        // show answer when question clicked
        findViewById(R.id.flashcard_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
//                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);

                View questionSideView = findViewById(R.id.flashcard_question);
                View answerSideView = findViewById(R.id.flashcard_answer);

                // get the center for the clipping circle
                int cx = answerSideView.getWidth() / 2;
                int cy = answerSideView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerSideView, cx, cy, 0f, finalRadius);

                // hide the question and show the answer to prepare for playing the animation!
                questionSideView.setVisibility(View.INVISIBLE);
                answerSideView.setVisibility(View.VISIBLE);

                anim.setDuration(300);
                anim.start();
            }
        });

        // show question when answer clicked
        findViewById(R.id.flashcard_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            }
        });

        // go to AddCardActivity when add button clicked
        findViewById(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_card = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(add_card,ADD_CARD_RESET_CODE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        // go to next flashcard in database
        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get next card index
                int originalIndex = currentCardDisplayedIndex;
                while (currentCardDisplayedIndex == originalIndex)
                    currentCardDisplayedIndex = getRandomNumber(0, allFlashcards.size() - 1);

                if (allFlashcards.size() > 0) {
                    // set the question and answer TextViews with data from the database
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                    // reset to question view
                    findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                }

                final Animation leftOutAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(v.getContext(), R.anim.right_in);

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        // this method is called when the animation first starts
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        findViewById(R.id.flashcard_question).startAnimation(rightInAnim);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });

                findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);
            }
        });

        // delete current flashcard
        findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFlashcards.size() > 0)
                    flashcardDatabase.deleteCard(((TextView) findViewById(R.id.flashcard_question)).getText().toString());

                // update list of all cards
                allFlashcards = flashcardDatabase.getAllCards();

                // change displayed card
                currentCardDisplayedIndex = getRandomNumber(0, allFlashcards.size() - 1);

                if (allFlashcards.size() > 0) {
                    // set the question and answer TextViews with data from the database
                    ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(currentCardDisplayedIndex).getQuestion());
                    ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(currentCardDisplayedIndex).getAnswer());

                    // reset to question view
                    findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
                    findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
                }
            }
        });

        // edit current flashcard
        findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit_card = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(edit_card,EDIT_CARD_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CARD_RESET_CODE && resultCode == RESULT_OK) {
            String question = data.getExtras().getString("question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String answer = data.getExtras().getString("answer");

            // update display card and insert new card into database
            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);
            flashcardDatabase.insertCard(new Flashcard(question, answer));
            allFlashcards = flashcardDatabase.getAllCards();
        } else if (requestCode == EDIT_CARD_REQUEST_CODE && resultCode == RESULT_OK) {
            // grab the data passed from AddCardActivity
            String question = data.getExtras().getString("question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String answer = data.getExtras().getString("answer");

            // set the TextViews to show the EDITED question and answer
            ((TextView) findViewById(R.id.flashcard_question)).setText(question);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(answer);

            Flashcard edittedCard = allFlashcards.get(currentCardDisplayedIndex);
            edittedCard.setQuestion(question);
            edittedCard.setAnswer(answer);
            flashcardDatabase.updateCard(edittedCard);
        }

    }

    // returns a random number between minNumber and maxNumber, inclusive.
    // for example, if i called getRandomNumber(1, 3), there's an equal chance of it returning either 1, 2, or 3.
    public int getRandomNumber(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }
}
