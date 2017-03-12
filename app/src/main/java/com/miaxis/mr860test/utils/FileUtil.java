package com.miaxis.mr860test.utils;

import android.os.Environment;

import com.miaxis.mr860test.Constants.Constants;
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

    public static final String HISTORY_PATH         = "MR860Test_history.txt";
    public static final String BEFORE_PATH          = "MR860Test_before.txt";
    public static final String AFTER_PATH           = "MR860Test_after.txt";
    public static final String INSPECTION_PATH      = "MR860Test_inspection.txt";
    public static final String VERSION_CONFIG_PATH  = "MR860Test_config.txt";

    public static void addRecord(String path, TestItem item) throws IOException {
        writeFile(path, parseItemToString(item) + "=", true);
    }

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
        item.setOpdate(fields[1]);
        item.setName(fields[2]);
        item.setStatus(Integer.valueOf(fields[3]));
        item.setRemark(fields[4]);
        return item;
    }

    public static String parseToString(List<TestItem> list) {
        StringBuilder sb = new StringBuilder("");
        for (int i=0; i<list.size(); i++) {
            sb.append(list.get(i).getId() + "_");
            sb.append(list.get(i).getOpdate() + "_");
            sb.append(list.get(i).getName() + "_");
            sb.append(list.get(i).getStatus() + "_");
            sb.append(list.get(i).getRemark());
            if (i < list.size() - 1) {
                sb.append("=");
                sb.append("\r\n");
            }
        }
        return sb.toString();
    }

    public static String parseItemToString(TestItem item) {
        StringBuilder sb = new StringBuilder("");
        sb.append(item.getId() + "_");
        sb.append(item.getOpdate() + "_");
        sb.append(item.getName() + "_");
        sb.append(item.getStatus() + "_");
        sb.append(item.getRemark());
        sb.append("\r\n");
        return sb.toString();
    }

    public static void writeFile(File file, String content, boolean isAdd) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, isAdd));
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

    public static void writeFilePath(String path, String name, String content, boolean isAdd) {
        BufferedWriter bw = null;
        try {
            File file = new File(path, name);
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file, isAdd));
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

    public static void writeFile(String name, String content, boolean isAdd) {
        BufferedWriter bw = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), name);
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file, isAdd));
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
            String readLine;
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
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

    public static List<String> readFileToList(File file) {
        List<String> stringList = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                stringList.add(readLine);
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
        return stringList;
    }

}
