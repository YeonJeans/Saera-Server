package yeonjeans.saera.util;

import java.util.ArrayList;
import java.util.List;

public class Parsing {

    public static List<Integer> getIntegerList(String str){
        List<Integer> list = new ArrayList<>();
        System.out.println(str.substring(1, str.length()-1));
        String sub = str.substring(1, str.length()-1);
        System.out.println(sub);
        String[] array = sub.split(", ");
        for(String x : array){
            list.add(Integer.valueOf(x));
        }
        return list;
    }
    public static List<Double> getDoubleList(String str){
        List<Double> list = new ArrayList<>();
        System.out.println(str.substring(1, str.length()-1));
        String sub = str.substring(1, str.length()-1);
        System.out.println(sub);
        String[] array = sub.split(", ");
        for(String x : array){
            list.add(Double.valueOf(x));
        }
        return list;
    }
}
