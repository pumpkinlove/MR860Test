package com.miaxis.mr860test.utils;

import android.os.Environment;
import com.miaxis.mr860test.domain.TestItem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu.nan on 2016/12/19.
 */

public class FileUtil {

    public static List<TestItem> parseFromString(String str) {
        List<TestItem> list = new ArrayList<>();
        String[] itemsStr = str.split("=");
        for (int i=0; i<itemsStr.length; i++) {
            list.add(parseItemFromString(itemsStr[i]));
        }
        return list;
    }

    public static TestItem parseItemFromString(String str) {
        TestItem item = new TestItem();
        String[] fields = str.split("_");
        item.setId(Integer.valueOf(fields[0]));
        item.setName(fields[1]);
        item.setStatus(Integer.valueOf(fields[2]));
        item.setOpdate(fields[3]);
        item.setRemark(fields[4]);
        return item;
    }

    public static String parseToString(List<TestItem> list) {
        StringBuilder sb = new StringBuilder("");
        for (int i=0; i<list.size(); i++) {
            sb.append(list.get(i).getId() + "_");
            sb.append(list.get(i).getName() + "_");
            sb.append(list.get(i).getStatus() + "_");
            sb.append(list.get(i).getOpdate() + "_");
            sb.append(list.get(i).getRemark());
            if (i < list.size() - 1) {
                sb.append("=");
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    public static void writeFile(String path, String content) {
        BufferedWriter bw = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), path);
            //第二个参数意义是说是否以append方式添加内容
            bw = new BufferedWriter(new FileWriter(file, false));
            bw.write(content);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                sb.append(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
