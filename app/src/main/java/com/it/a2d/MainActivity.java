package com.it.a2d;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    String TAG="MainActivity";
    String barCode="";
    EditText data1;
    Button btn;
    Barcode2DWithSoft barcode2DWithSoft=null;
    String seldata="ASCII";
    private ArrayAdapter adapterTagType;
    private Spinner spTagType;
    HomeKeyEventBroadCastReceiver     receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        barcode2DWithSoft=Barcode2DWithSoft.getInstance();

        receiver = new HomeKeyEventBroadCastReceiver();
        registerReceiver(receiver, new IntentFilter("com.rscja.android.KEY_DOWN"));


        data1= (EditText) findViewById(R.id.editText);
        btn=(Button)findViewById(R.id.button);
        spTagType=(Spinner)findViewById(R.id.spTagType);
        adapterTagType = ArrayAdapter.createFromResource(this,
                R.array.arrayTagType, android.R.layout.simple_spinner_item);

        adapterTagType
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTagType.setAdapter(adapterTagType);
        spTagType.setSelection(1);

        spTagType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id) {
                //获取选中值
                Spinner spinner = (Spinner) adapterView;
                seldata = (String) spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanBarcode();


            }
        });

        new InitTask().execute();

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

/*
        if (barcode2DWithSoft != null) {
            new InitTask().execute();
        }*/
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        Log.i(TAG,"onDestroy");
        if(barcode2DWithSoft!=null){
            barcode2DWithSoft.stopScan();
            barcode2DWithSoft.close();
        }
        super.onDestroy();
        //android.os.Process.killProcess(Process.myPid());
    }

    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog mypDialog;

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub


            boolean reuslt=false;
            if(barcode2DWithSoft!=null) {
                reuslt=  barcode2DWithSoft.open(MainActivity.this);
                Log.i(TAG,"open="+reuslt);

            }
            return reuslt;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
//                barcode2DWithSoft.setParameter(324, 1);
//                barcode2DWithSoft.setParameter(300, 0); // Snapshot Aiming
//                barcode2DWithSoft.setParameter(361, 0); // Image Capture Illumination

                // interleaved 2 of 5
                barcode2DWithSoft.setParameter(6, 1);
                barcode2DWithSoft.setParameter(22, 0);
                barcode2DWithSoft.setParameter(23, 55);
                barcode2DWithSoft.setParameter(402, 1);
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"fail",Toast.LENGTH_SHORT).show();
            }
            mypDialog.cancel();
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(MainActivity.this);
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }

    }
    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";//home key
        static final String SYSTEM_RECENT_APPS = "recentapps";//long home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.rscja.android.KEY_DOWN")) {
                int reason = intent.getIntExtra("Keycode",0);
                //getStringExtra
                boolean long1 = intent.getBooleanExtra("Pressed",false);
                // home key处理点
                if(reason==280 || reason==66){

                    ScanBarcode();


                }
                // Toast.makeText(getApplicationContext(), "home key="+reason+",long1="+long1, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Barcode2DWithSoft.ScanCallback  ScanBack= new Barcode2DWithSoft.ScanCallback(){
        @Override
        public void onScanComplete(int i, int length, byte[] bytes) {
            if (length < 1) {
                if (length == -1) {
                    data1.setText("Scan cancel");
                } else if (length == 0) {
                    data1.setText("Scan TimeOut");
                } else {
                    Log.i(TAG,"Scan fail");
                }
            }else{
                SoundManage.PlaySound(MainActivity.this, SoundManage.SoundType.SUCCESS);
                barCode="";


                //  String res = new String(dd,"gb2312");
                try {
                    Log.i("Ascii",seldata);
                    barCode = new String(bytes, 0, length, seldata);
                    zt();
                }
                catch (UnsupportedEncodingException ex)   {}
                data1.setText(barCode);
            }

        }
    };
    void zt() {

        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(100);
    }
    private void ScanBarcode(){
        if(barcode2DWithSoft!=null) {
            Log.i(TAG,"ScanBarcode");

            barcode2DWithSoft.scan();
            barcode2DWithSoft.setScanCallback(ScanBack);
        }
    }
}
