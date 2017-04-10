package deskclock.android.com.timer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater layoutInflater=LayoutInflater.from(this);

        Timer timer=(Timer)findViewById(R.id.time_circle);
        TextView timeText=(TextView)findViewById(R.id.timeText);
        timer.setTimeText(timeText );
    }
}
