package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var tvCharCounter : TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {

        val context = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        tvCharCounter = findViewById(R.id.tvCharCounter)

        client = TwitterApplication.getRestClient(this)

        //For the character counter
        var charCount = 0
        var charCountDisplay = 0
        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires as the text is changed
                charCount = s.length
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed

                if(charCount <= 280){
                    charCountDisplay = 280 - charCount
                    tvCharCounter.setText("Characters Remaining: " + charCountDisplay.toString())
                    tvCharCounter.setTextColor(Color.GREEN)
                } else {
                    tvCharCounter.setText("Characters Remaining: " + charCountDisplay.toString())
                    tvCharCounter.setTextColor(Color.RED)
                    charCountDisplay = 0
                }

                /*
                tvCharCounter.setText("Characters Remaining: " + charCountDisplay.toString())
                if(charCountDisplay > 0){
                    tvCharCounter.setTextColor(Color.GREEN)
                } else {
                    tvCharCounter.setTextColor(Color.RED)
                }
                */
            }
        })

        //Handling the user's click on the tweet button
        btnTweet.setOnClickListener {
            //Grab the content of the edittext (etCompose)
            val tweetContent = etCompose.text.toString()

            //1.Make sure the tweet is not empty
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Empty tweets are not allowed!",
                            Toast.LENGTH_SHORT).show()
                //Look into displaying SnackBar message
            } else
            //2.Make sure the tweet is under character count
            if(tweetContent.length > 280){
                Toast.makeText(this, "Tweet is longer than 280 characters!",
                            Toast.LENGTH_SHORT).show()
            } else {
                //Make an API call to Twitter to publish the tweet
                client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {

                    override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                        Log.i(TAG, "Successfully published tweet!")
                        Toast.makeText(context, "Succesfully published tweet!",
                                        Toast.LENGTH_SHORT).show()

                        //Send the tweet back to TimelineActivity
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet", tweet)
                        setResult(RESULT_OK, intent)

                        Log.i(TAG, "finish() called in ComposeActivity.kt")
                        finish()
                    }

                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to publish tweet", throwable)
                    }

                })
            }
        }
    }
    companion object{
        val TAG = "ComposeActivity"
    }
}