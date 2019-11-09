package com.interview.jvm;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author WDS
 * @date 2019/11/8 19:20
 */
public class StringOOMTest {


    public static void main(String[] args) {
        String orgStr = null;
        StringBuffer sb = new StringBuffer();
        for(int i =0;i<1000;i++){
            sb.append(i);
            sb.append(";");
        }
        orgStr = sb.toString();
        long start = System.currentTimeMillis();
        for (int i=0;i<10000;i++){
            orgStr.split(";");
        }
        System.out.println(System.currentTimeMillis()-start);

        StringTokenizer stringTokenizer = new StringTokenizer(orgStr,";");
        long start2 = System.currentTimeMillis();
        for (int i=0;i<10000;i++){
            while (stringTokenizer.hasMoreTokens()){
                stringTokenizer.nextToken();
            }
            stringTokenizer = new StringTokenizer(orgStr,";");
        }
        System.out.println(System.currentTimeMillis()-start2);
    }


}
