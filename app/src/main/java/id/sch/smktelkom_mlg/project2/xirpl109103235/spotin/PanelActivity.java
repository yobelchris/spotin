package id.sch.smktelkom_mlg.project2.xirpl109103235.spotin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

public class PanelActivity extends AppCompatActivity {
    private final static String WISATA = "wisata";
    private final static String MAKAN = "makan";
    private final static String TIDUR = "tidur";
    private final static String TIPE = "tipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_panel);

        ImageButton toGo = (ImageButton) findViewById(R.id.toGo);
        ImageButton toEat = (ImageButton) findViewById(R.id.toEat);
        ImageButton toSleep = (ImageButton) findViewById(R.id.toSleep);
        //makan tidur wisata
        toGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, MainActivity.class);
                intent.putExtra(TIPE, WISATA);
                startActivity(intent);
            }
        });

        toEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, MainActivity.class);
                intent.putExtra(TIPE, MAKAN);
                startActivity(intent);
            }
        });

        toSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PanelActivity.this, MainActivity.class);
                intent.putExtra(TIPE, TIDUR);
                startActivity(intent);
            }
        });
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PanelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
