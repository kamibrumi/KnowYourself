package yukami.notification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NewActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        textView = (TextView) findViewById(R.id.textView);
        Intent intent = this.getIntent();
        textView.setText(intent.getStringExtra("how"));
    }
}
