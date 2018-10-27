package com.example.nadiairwanto.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        findViewById(R.id.saveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                String question = ((EditText) findViewById(R.id.qnEditText)).getText().toString();
                String answer = ((EditText) findViewById(R.id.ansEditText)).getText().toString();
                data.putExtra("question", question);
                data.putExtra("answer", answer);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
