package com.example.domin.multism;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_SEND_SMS = 123;
    SharedPreferences zapisane_ustawienia;
    String DB_name,name,password,domena,ip;
    ArrayList<Klient> Do_wyslania = new ArrayList<Klient>();


    String Uprawnienia ="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zapisane_ustawienia = PreferenceManager.getDefaultSharedPreferences(this);
        Log.e("aa", zapisane_ustawienia.getString("DB_name","brak"));
        Log.e("aa", zapisane_ustawienia.getString("name","brak"));
        Log.e("aa", zapisane_ustawienia.getString("password","brak"));
        Log.e("aa", zapisane_ustawienia.getString("domena","brak"));
        Log.e("aa", zapisane_ustawienia.getString("ip","brak"));

        DB_name = zapisane_ustawienia.getString("DB_name","brak");
        name =  zapisane_ustawienia.getString("name","brak");
        password = zapisane_ustawienia.getString("password","brak");
        domena = zapisane_ustawienia.getString("domena","brak");
        ip = zapisane_ustawienia.getString("ip","brak");

        requestSmsPermission();

        setContentView(R.layout.activity_main);

        ping p = new ping();

        p.execute("http://dwiniarek.pl");
    }

    public boolean isConnectedToServer(String url, int timeout) {
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            Log.e("połączono", String.valueOf(e));
            // Handle your exceptions
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.settings_id:
            {
                startActivity(new Intent(this, Setings.class));
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
            return false;
        } else {
            // permission already granted run sms send
            Uprawnienia="1";
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                Uprawnienia = "1";
                } else {
                    // permission denied
                   Uprawnienia = "0";
                }
                return;
            }
        }
    }




    public void poloncz(View view) throws ExecutionException, InterruptedException {
        zapisane_ustawienia = PreferenceManager.getDefaultSharedPreferences(this);
        DB_name = zapisane_ustawienia.getString("DB_name","brak");
        name =  zapisane_ustawienia.getString("name","brak");
        password = zapisane_ustawienia.getString("password","brak");
        domena = zapisane_ustawienia.getString("domena","brak");
        ip = zapisane_ustawienia.getString("ip","brak");
        if(DB_name.equals("brak"))
        {
            Toast.makeText(this,"Uzupełnij nazwę bazy danych",Toast.LENGTH_SHORT).show();
        }else if (name.equals("brak"))
        {
            Toast.makeText(this,"Uzupełnij Nazwę użytkownika ",Toast.LENGTH_SHORT).show();
        }else if (password.equals("brak"))
        {
            Toast.makeText(this,"Uzupełnij hasło",Toast.LENGTH_SHORT).show();
        }else if (domena.equals("brak"))
        {
            Toast.makeText(this,"Uzupełnij nazwę domeny",Toast.LENGTH_SHORT).show();
        }else if (ip.equals("brak"))
        {
            Toast.makeText(this,"Uzupełnij adres ip4",Toast.LENGTH_SHORT).show();
        }else
        {
            String type="login";
            Connect connect = new Connect(this, type);
            connect.execute(domena,  DB_name, name, password, ip);
            Do_wyslania=connect.Klienci;
        }








    }
    private static boolean isAirplaneModeOn(Context context) {

        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

    }
    public void wyslij(View view)
    {
//        if(isAirplaneModeOn(this))
//        {
//            Toast.makeText(this, "Wyłącz tryb samolotowy.", Toast.LENGTH_SHORT).show();
//        }else
//        {
            if (Do_wyslania.size() > 0)
            {
                String type = "sent";
                Connect connect = new Connect(this, type);
                connect.Klienci = Do_wyslania;
                // Log.e("roz " ,String.valueOf(Do_wyslania.size()));
                connect.execute(domena, DB_name, name, password, ip);
            } else {
                Toast.makeText(this, "Brak numerów do wysłania.", Toast.LENGTH_SHORT).show();
            }
        //}
    }
}
