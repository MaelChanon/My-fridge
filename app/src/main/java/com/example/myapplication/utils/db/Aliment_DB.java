package com.example.myapplication.utils.db;

import java.io.Serializable;
import java.util.Arrays;
import java.util.GregorianCalendar;

public class Aliment_DB implements Comparable<Aliment_DB>, Serializable {
    public void setAliment_code(String aliment_code) {
        this.aliment_code = aliment_code;
    }


    public void setNb_aliments(int nb_aliments) {
        this.nb_aliments = nb_aliments;
    }

    private String aliment_code;
    private GregorianCalendar date_peremption;
    private int nb_aliments;
    public Aliment_DB(){

    }
    public Aliment_DB(String aliment_code, String date_String, int nb_aliments) {
        this.aliment_code = aliment_code;
        int[] date = Arrays.stream(date_String.split("/")).mapToInt(Integer::parseInt).toArray();
        this.date_peremption = new GregorianCalendar(date[2],date[1]-1,date[0]);
        this.nb_aliments= nb_aliments;
    }

    public String getAliment_code() {
        return aliment_code;
    }
    public void setDate_peremption(String date_String) {
        int[] date = Arrays.stream(date_String.split("/")).mapToInt(Integer::parseInt).toArray();
        this.date_peremption = new GregorianCalendar(date[2],date[1],date[0]);
    }

    public String getDate_peremption() {
        return String.format("%d/%d/%d",date_peremption.get(GregorianCalendar.DAY_OF_MONTH),date_peremption.get(GregorianCalendar.MONTH)+1,date_peremption.get(GregorianCalendar.YEAR));
    }
    public int getNb_aliments() {
        return nb_aliments;
    }

    private long getDatePeremptionInMillis(){
        return this.date_peremption.getTimeInMillis();
    }

    public GregorianCalendar DatePeremption(){
        return this.date_peremption;
    }


    @Override
    public int compareTo(Aliment_DB aliment_db) {
        return (int)(this.getDatePeremptionInMillis() - aliment_db.getDatePeremptionInMillis());
    }
}
