package com.example.domin.multism;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by domin on 22.02.2018.
 */

public class Connect extends AsyncTask<String,Integer,ArrayList<Klient>>{


    Context context;
    String type=" ";
    ArrayList<String> fragmenty =null;
    ArrayList<Klient> Klienci = new ArrayList<Klient>();
    Button polacz ;
    Button wyslij;
    TextView pobrane ;
    TextView wyslane ;
    int potwierdzenie=0;
    String SENT ="SMS_SENT";
    String DELIVERED ="SMS_DELIVERED";
    BroadcastReceiver smsSentReciver;
    private String result="";
    private ProgressDialog dialog_conn, dialog_sent;


    Connect(Context ctx, String ty)
    {

        type=ty;
        context=ctx;
        dialog_conn = new ProgressDialog(context);
        dialog_sent = new ProgressDialog(context);
         polacz = (Button) ((Activity) context).findViewById(R.id.b1);
         wyslij = (Button) ((Activity) context).findViewById(R.id.b2);
         pobrane = (TextView) ((Activity) context).findViewById(R.id.tv);
         wyslane = (TextView) ((Activity) context).findViewById(R.id.liczba_wysłanych);
    }
    int li=0;
    //po wykonaniu
    @Override
    protected void onPreExecute() {

        if(type.equals("login")) {
            dialog_conn.setMessage("Loading..., please wait.");
            dialog_conn.setCancelable(false);
            dialog_conn.show();
            polacz.setEnabled(false);
            wyslij.setEnabled(false);

            wyslane.setText("0");
            super.onPreExecute();
        }else if(type.equals("sent"))
        {
            dialog_sent.setMessage("Sent..., please wait.");
            dialog_sent.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog_sent.setCancelable(false);
            dialog_sent.setMax(Klienci.size());
            dialog_sent.setProgress(0);
            dialog_sent.show();
            pobrane.setText("Pobierzponownie");
            Log.e("PRE if: ", String.valueOf(Klienci.size()));
            polacz.setEnabled(false);
            wyslij.setEnabled(false);
            super.onPreExecute();
        }

    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        dialog_sent.setProgress(values[0]);
    }

    @Override
    protected ArrayList<Klient> doInBackground(String... params) {

        String name =params[1];
        String name_user =params[2];
        String password =params[3];
        String ip =params[4];


        if(type.equals("login"))
        {

            String login_url= "http://"+params[0]+"/login.php";
            String blad="brak";
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                        +URLEncoder.encode("name_user","UTF-8")+"="+URLEncoder.encode(name_user,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                        +URLEncoder.encode("ip","UTF-8")+"="+URLEncoder.encode(ip,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();


                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                result="";
                String line="";
                int licznik=0;
                while((line = bufferedReader.readLine())!= null) {
                    if (line.equals(blad)) {

                        break;
                    } else {

                        result += line + "\n";
                        Log.e("Linia :  ", line);
                        Log.e("result :  ", line);
                        String podziel = new String(line);
                        String[] podzielona_tablica = null;
                        podzielona_tablica = podziel.split(";");
                        for (int i = 0; i < podzielona_tablica.length; i++) {
                            Log.e("peta "+Integer.toString(i), podzielona_tablica[i]);
                        }
                        licznik++;
                        if(9 == podzielona_tablica[1].length()) {
                            Klient k = new Klient(Integer.parseInt(podzielona_tablica[0]), podzielona_tablica[1], podzielona_tablica[2]);
                            Klienci.add(k);
                            Thread.sleep(10);
                        }
                        //Log.e("k "  , Klienci.get(0).getId()+" "+Klienci.get(0).getNumer()+" "+Klienci.get(0).getDescryption()+"\n");

                    }
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return Klienci;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        else if(type.equals("sent"))
        {


            Log.e("else if: ", String.valueOf(Klienci.size()));
            int licznik=0;
            if(Klienci.size()>0){

                for(Klient x : Klienci)
                {
                    Log.e("licznik ", String.valueOf(licznik));
                        publishProgress(licznik);
                        licznik++;

                    Log.e("przed dunkja ", String.valueOf(potwierdzenie));
                        sendSms(x.getNumer(), x.getDescryption());

                    Log.e("po funkcji  ", String.valueOf(potwierdzenie));
                        Log.e("klient x :", x.getId() + " " +x.getNumer() + " " + x.getDescryption() + " " + x.getStatus() + " " + x.getData());
                        publishProgress(licznik);
                        switch (potwierdzenie)
                        {
                            case 0:
                            {
                                x.setStatus("nie wysłano");//SMS sent
                                break;
                            }
                            case 1:
                            {
                                x.setStatus("wysłano");//SMS sent
                                break;
                            }
                            case 2:
                            {
                                x.setStatus("błąd");//General fail
                                break;
                            }
                            case 3:
                            {
                                x.setStatus("Brak zasięgu");//No service
                                break;
                            }
                            case 4:
                            {
                                x.setStatus("Brak danych");//null PDU
                                break;
                            }
                            case 5:
                            {
                                x.setStatus("Radio off");//Radio off
                                break;
                            }
                            default:
                            {
                                x.setStatus("Nie znany błąd");
                                break;
                            }

                        }
                        x.setData(getkalendarz())  ;
                        Log.e("klient x :", x.getId() + " " +x.getNumer() + " " + x.getDescryption() + " " + x.getStatus() + " " + x.getData());

                    //start try ==============================

                        String st  = x.getStatus();
                        String id = String.valueOf(x.getId());
                        String data = x.getData();
                        String status_url= "http://"+params[0]+"/status.php";

                        try {

                            URL url = new URL(status_url);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setDoInput(true);
                            Log.e("aa", status_url);


                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                                    +URLEncoder.encode("name_user","UTF-8")+"="+URLEncoder.encode(name_user,"UTF-8")+"&"
                                    +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                                    +URLEncoder.encode("ip","UTF-8")+"="+URLEncoder.encode(ip,"UTF-8")+"&"
                                    +URLEncoder.encode("st","UTF-8")+"="+URLEncoder.encode(st,"UTF-8")+"&"
                                    +URLEncoder.encode("data","UTF-8")+"="+URLEncoder.encode(data,"UTF-8")+"&"
                                    +URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
                            bufferedWriter.write(post_data);
                            Log.e("aa", post_data);
                            bufferedWriter.flush();
                            bufferedWriter.close();

                            outputStream.close();
                            InputStream inputStream = httpURLConnection.getInputStream();
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                            String result="";
                            String line="";

                            while((line = bufferedReader.readLine())!= null) {
                                result += line;
                                li++;
                            }

                            bufferedReader.close();
                            inputStream.close();
                            httpURLConnection.disconnect();


                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            Log.e("aa1", String.valueOf(e));
                        } catch (ProtocolException e) {
                            e.printStackTrace();
                            Log.e("aa2", String.valueOf(e));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("aa3", String.valueOf(e));
                        }




                //end try ------------------------------
                }





                Log.e("data: ", getkalendarz());


                return Klienci;
            }


        }
        return null;
    }

//przed wykonaniem
    @Override
    protected void onPostExecute(ArrayList<Klient> result) {
        if (type.equals("login")) {


            if (dialog_conn.isShowing()) {
                dialog_conn.dismiss();
            }
                polacz.setEnabled(true);
                wyslij.setEnabled(true);
                pobrane.setText(String.valueOf(Klienci.size()));
                wyslane.setText("Czekam na wysłanie");
                super.onPostExecute(result);
            } else if (type.equals("sent")) {
            if (dialog_sent.isShowing()) {
                dialog_sent.dismiss();
            }
                polacz.setEnabled(true);
                wyslij.setEnabled(true);
                Log.e("POST if: ", String.valueOf(Klienci.size()));
                pobrane.setText("Połącz ponownie");
                wyslane.setText(String.valueOf(Klienci.size()));
                Klienci.clear();
            }
        }


    private void sendSms(String phoneNumber, String mm){
        SmsManager smsManager = SmsManager.getDefault();
        fragmenty=smsManager.divideMessage(mm);
        int ct = fragmenty.size();
        final ArrayList<PendingIntent> sentPis = new ArrayList<PendingIntent>(ct);
        final ArrayList<PendingIntent> delPis = new ArrayList<PendingIntent>(ct);
        Log.e("intent ", String.valueOf(potwierdzenie));
        for (int i = 0; i < ct; i++) {

            final PendingIntent piSent =
                    PendingIntent.getBroadcast(context,
                            i,
                            new Intent(SENT),
                            0);
            final PendingIntent piDel =
                    PendingIntent.getBroadcast(context,
                            i,
                            new Intent(DELIVERED),
                            0);

            sentPis.add(piSent);
            delPis.add(piDel);
        }
        Log.e("wysyłanie  ", String.valueOf(potwierdzenie));
        smsManager.sendMultipartTextMessage(phoneNumber, null, fragmenty, sentPis, delPis);
        potwierdzenie=0;
        Log.e("po wysłaniu  ", String.valueOf(potwierdzenie));
int licznik=0;
        while(0==potwierdzenie) {
            powierdzenie();
            licznik++;
            try {
                Log.e("licznik :", String.valueOf(licznik));
                Thread.sleep(100);
                if(licznik>300)
                {
                    potwierdzenie=6;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e("potwierdzenie x :", String.valueOf(potwierdzenie));
    }

    public void powierdzenie()
    {
        smsSentReciver =new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("brodcast  ", String.valueOf(potwierdzenie));
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        {
                        potwierdzenie=1;//SMS sent
                        break;
                        }
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        {
                        potwierdzenie = 2;//General fail
                        break;
                        }
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                    {
                        potwierdzenie = 3;//No service
                        break;
                    }
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    {
                        potwierdzenie = 4;//null PDU
                        break;
                    }
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    {
                        potwierdzenie = 5;//Radio off
                        break;
                    }
                    default:
                    {
                        potwierdzenie = 6;//Nieznany błąd
                        break;
                    }
                }
            }


        };
        Log.e("przed rejestracją pot  ", String.valueOf(potwierdzenie));

        context.registerReceiver(smsSentReciver, new IntentFilter(SENT));

        Log.e("po rejestracją pot  ", String.valueOf(potwierdzenie));
    }

    public String getkalendarz()
    {
        Calendar c = Calendar.getInstance();
        int godzina = c.get(Calendar.HOUR_OF_DAY);
        int minuta = c.get(Calendar.MINUTE);
        int sekundy = c.get(Calendar.SECOND);
        int rok = c.get(Calendar.YEAR);
        int miesiac = c.get(Calendar.MONTH);
        int dzien = c.get(Calendar.DAY_OF_MONTH);
        String obecna_data=rok+"-"+String.format("%02d",(miesiac+1))+"-"+String.format("%02d",dzien)+" "
                +String.format("%02d",godzina)+":"+String.format("%02d",minuta)+":"+String.format("%02d",sekundy);


        return obecna_data;
    }


}
