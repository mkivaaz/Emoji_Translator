package com.mkivaaz.emoji_translator;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mkivaaz.emoji_translator.Data.Emoji;
import com.mkivaaz.emoji_translator.Data.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emojiET;
    TextView translateTV;
    Button translateBtn;
    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Emoji> emojiList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("emojiList");
        emojiET = findViewById(R.id.emojiET);
        translateBtn = findViewById(R.id.translateBtn);
        translateTV = findViewById(R.id.translatedTV);
        translateBtn.setOnClickListener(this);
        emojiET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                translate2Emoji();
            }
        });
    }

    public void translate2Emoji(){
        emojiList = getEmojiList();
        String[] words = splitSentence(emojiET.getText().toString().trim());
        List<String> newSentence = new ArrayList<>();
        for (int i = 0; i<words.length ;i++){
            String trimWord = getLetters(words[i]);
            String trimSymbols = getSymbols(words[i]);
            Log.v("Output",trimWord + "_" + trimSymbols);
            int emojiCode = isEmoji(emojiList, trimWord);
            if (emojiCode != 0){
                newSentence.add(getEmoji(emojiCode) + trimSymbols);
            }else {
                newSentence.add(words[i]);
            }
        }

        displayText(newSentence);
    }

    private void displayText(List<String> newSentence) {
        String text = "";
        for (String word : newSentence){
            text = text + " " + word;
        }

        translateTV.setText(text);
        translateTV.setVisibility(View.VISIBLE);
    }

    private int isEmoji(List<Emoji> emojiList, String word) {
//        word = similarWord(emojiList,word);
        Log.v("SimilarWord ", word);
        for (Emoji emoji: emojiList){
            if (emoji.getEmojiName().equalsIgnoreCase(word)){
                return emoji.getEmojiCode();
            }
        }
        return 0;
    }

    private String[] splitSentence(String sentence){
        return sentence.split(" ");
    }

    private String getSymbols(String sentence){
        Pattern pattern = Pattern.compile("[^a-zA-Z]+");
        Matcher matcher = pattern.matcher(sentence);
        if (matcher.find()){
            Log.v(" Found: " , matcher.group());
            return sentence.substring(matcher.start());
        }
        return "";
    }

    private String getLetters(String sentence){
        Pattern pattern = Pattern.compile("[^a-zA-Z]+");
        Matcher matcher = pattern.matcher(sentence);
        if (matcher.find()){
            Log.v(" Found: " , matcher.group());
            return sentence.substring(0,matcher.start());
        }
        return sentence;
    }

    private List<Emoji> getEmojiList() {
        final List<Emoji> list = new ArrayList<>();
        list.add(new Emoji("Car",0x1F3CE));
        list.add(new Emoji("Grin",0x1F605));
        list.add(new Emoji("House",0x1F3E0));
        list.add(new Emoji("Card",0x1F0CF	));
        list.add(new Emoji("Smile",0x263A));
        list.add(new Emoji("OMG",0x1F631));
        list.add(new Emoji("LOL",0x1F602));
        list.add(new Emoji("Wonder",0x1F914));
        return list;
    }

    private String getEmoji(int emojicode) {
        return new String(Character.toChars(emojicode));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.translateBtn:
                translate2Emoji();
        }
    }

    private boolean isLetter(char character){
        return Character.isLetter(character);
    }

    private char[] word2Char(String word){
        return word.toCharArray();
    }

    private String similarWord(List<Emoji> emojiList, String word){
        char[] chkLetters = word2Char(word);
        List<Match> matches = new ArrayList<>();
        Match bestMatch = null;

        for (Emoji emoji : emojiList){
            char[] letters = word2Char(emoji.getEmojiName().toLowerCase());

            if ((chkLetters.length>0) && (chkLetters.length >= letters.length) && (letters[0] == chkLetters[0])){
                double check = 0;
                for(int i = 0; i<letters.length; i++){
                    if (letters[i] == chkLetters[i]){
                        check = check + 1;
                    }
                }
                Log.v("Check ", "" + String.valueOf(check/letters.length));
                matches.add(new Match(emoji.getEmojiName(),check/letters.length));
            }
        }

        for (int i = 0; i<matches.size(); i++){
            for (int j = 0; j<matches.size(); j++){
                if (matches.get(i).getMatch() >= matches.get(j).getMatch()){
                    bestMatch = matches.get(i);
                }else {
                    bestMatch = matches.get(j);
                }
            }
        }
        if (bestMatch != null){
            if (bestMatch.getMatch() >= 0.9){
                return bestMatch.getEmojiName();
            }
        }
        return word;
    }
}
