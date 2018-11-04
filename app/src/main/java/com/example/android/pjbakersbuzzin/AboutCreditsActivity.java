package com.example.android.pjbakersbuzzin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AboutCreditsActivity extends AppCompatActivity {

    private static final String TAG = AboutCreditsActivity.class.getSimpleName();
    final private StringBuilder text = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_about);
        // Set setDisplayHomeAsUpEnabled to true on the support ActionBar
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.credits);
        TextView textView = (TextView) findViewById(R.id.tv_about);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(getResources().openRawResource(R.raw.credits)));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = bufferedReader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), R.string.credits_file_error_message, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.d(TAG, "bufferedReader.close: IOException ");
                }
            }
            textView.setText(Html.fromHtml(text.toString(), Html.FROM_HTML_MODE_LEGACY));
            textView.startAnimation(animation);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
