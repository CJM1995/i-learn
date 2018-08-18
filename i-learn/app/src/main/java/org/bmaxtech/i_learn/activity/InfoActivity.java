package org.bmaxtech.i_learn.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.bmaxtech.i_learn.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    private List<String> suggestionList = new ArrayList<>();
    ImageView search_image;
    private JSONObject sampleWords = new JSONObject();
    private TextView title;
    private ListView suggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        try {
            // read sample json file
            this.readSampleWordsJosn();
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        YoYo.with(Techniques.BounceInUp).delay(200).duration(2000).playOn(findViewById(R.id.title_con));
        YoYo.with(Techniques.BounceInLeft).delay(700).duration(2000).playOn(findViewById(R.id.sub_title));

        search_image = findViewById(R.id.search_image);
        title = findViewById(R.id.title);
        suggestions = (ListView) findViewById(R.id.suggestions);

        // set intent params
        title.setText(this.getIntent().getExtras().getString("title"));

        this.loadImage(this.getIntent().getExtras().getString("title"));
        this.setSuggestions();
    }

    /**
     * Set Suggestion
     */
    private void setSuggestions() {
        try {
            this.suggestionList.clear();
            JSONArray jsonWordsList = this.sampleWords.getJSONArray(title.getText().toString().toLowerCase());
            for (int i = 0; i < jsonWordsList.length(); i++) {
                this.suggestionList.add(jsonWordsList.getString(i));
            }
            this.suggestions.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.word_list_row, R.id.list_item_title, suggestionList));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Load Image For given title
     *
     * @param title
     */
    private void loadImage(String title) {
        Drawable drawable = null;
        if (title.equalsIgnoreCase("apple")) {
            drawable = getResources().getDrawable(R.drawable.icon_apple);
        } else if (title.equalsIgnoreCase("ball")) {
            drawable = getResources().getDrawable(R.drawable.icon_ball);
        } else if (title.equalsIgnoreCase("cat")) {
            drawable = getResources().getDrawable(R.drawable.icon_cat);
        } else if (title.equalsIgnoreCase("dog")) {
            drawable = getResources().getDrawable(R.drawable.icon_dog);
        } else if (title.equalsIgnoreCase("egg")) {
            drawable = getResources().getDrawable(R.drawable.icon_egg);
        } else if (title.equalsIgnoreCase("flower")) {
            drawable = getResources().getDrawable(R.drawable.icon_flower);
        } else if (title.equalsIgnoreCase("goldfish")) {
            drawable = getResources().getDrawable(R.drawable.icon_goldfish);
        } else if (title.equalsIgnoreCase("hat")) {
            drawable = getResources().getDrawable(R.drawable.icon_hat);
        } else if (title.equalsIgnoreCase("ice-cream")) {
            drawable = getResources().getDrawable(R.drawable.icon_ice_cream);
        } else if (title.equalsIgnoreCase("jam")) {
            drawable = getResources().getDrawable(R.drawable.icon_jam);
        }
        search_image.setImageDrawable(drawable);
    }

    /**
     * Read Sample Words Json File
     *
     * @throws Exception
     */
    private void readSampleWordsJosn() throws IOException, JSONException {
        InputStream inputStream = getResources().openRawResource(R.raw.sample_letters);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            inputStream.close();
        }
        this.sampleWords = new JSONObject(writer.toString());
    }
}
