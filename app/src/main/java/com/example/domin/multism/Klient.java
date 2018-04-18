package com.example.domin.multism;

/**
 * Created by domin on 22.02.2018.
 */

public class Klient {

    private int id=0;
    private String numer="";
    private String descryption="";
    private String data="";
    private String status="";
    private String dostarczona="";

    public Klient(int ID, String tel, String inf)
    {
        id=ID;
        numer =tel;
        descryption = inf;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumer() {
        return numer;
    }


    public String getDescryption() {
        return descryption;
    }



    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
