package com.neurosky.mindwavemobiledemo;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by chaiche on 16/10/16.
 */
public class InforMation {

    int id;
    String name;

    ArrayList<Integer> attenation = new ArrayList();

    ArrayList<Integer> medation = new ArrayList();
    Double avg_attenation,avg_medation;

    DecimalFormat df = new DecimalFormat("#.00");

    public InforMation(int id,String name,String attenation,String medation){
        this.id = id;
        this.name = name;

        avg_attenation = set_att(attenation);
        avg_attenation = Double.parseDouble(df.format(avg_attenation));
        avg_medation = set_med(medation);
        avg_medation = Double.parseDouble(df.format(avg_medation));
    }
    public Double set_att(String tmp){
        Double count=0.0;
        while (tmp.contains("D")) {
            int a = Integer.parseInt(tmp.substring(tmp.indexOf(":") + 1, tmp.indexOf("D")));
            count+=a;
            tmp = tmp.substring(tmp.indexOf("D") + 1);
            attenation.add(a);
        }
        if(attenation.size()==0) return 0.0;
        return count/attenation.size();
    }
    public Double set_med(String tmp){
        Double count=0.0;
        while (tmp.contains("D")) {
            int a = Integer.parseInt(tmp.substring(tmp.indexOf(":") + 1, tmp.indexOf("D")));
            count+=a;
            tmp = tmp.substring(tmp.indexOf("D") + 1);
            medation.add(a);
        }
        if(medation.size()==0) return 0.0;
        return count/medation.size();
    }
}
