package com.example.nadiairwanto.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
                findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
                findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
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
