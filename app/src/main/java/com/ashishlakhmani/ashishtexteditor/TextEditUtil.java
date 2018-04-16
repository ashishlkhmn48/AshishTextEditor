package com.ashishlakhmani.ashishtexteditor;

import android.text.Layout;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

public class TextEditUtil {

    private EditText text;

    public TextEditUtil(EditText text) {
        this.text = text;
    }

    //To Get Individual Lines from EditText to ArrayList of String.
    public ArrayList<String> getLines() {
        final ArrayList<String> lines = new ArrayList<>();
        final Layout layout = text.getLayout();

        if (layout != null) {
            final int lineCount = layout.getLineCount();
            final CharSequence text = layout.getText();

            for (int i = 0, startIndex = 0; i < lineCount; i++) {
                final int endIndex = layout.getLineEnd(i);

                lines.add(text.toString().substring(startIndex, endIndex));
                startIndex = endIndex;
            }
        }
        return lines;
    }


    //To Display All the Lines
    public HashMap<Integer, String> displayAll() {
        HashMap<Integer, String> map = new HashMap<>();
        ArrayList<String> list = getLines();

        for (int i = 0; i < list.size(); i++) {
            map.put(i + 1, list.get(i));
        }
        return map;
    }

    //To Display Multiple Lines from m to n (m and n both inclusive)
    public HashMap<Integer, String> displayMultipleLines(int m, int n) {
        ArrayList<String> old = getLines();
        HashMap<Integer, String> map = new HashMap<>();
        for (int i = m - 1; i <= n - 1; i++) {
            map.put(i + 1, old.get(i));
        }

        return map;
    }

    //To Insert text At Line m
    public void insertAtLine(int m, String val) {

        ArrayList<String> list = getLines();

        String newVal = list.get(m - 1).concat(val);
        list.set(m - 1, newVal);
        String newStr = convertArrayListToString(list);
        text.setText(newStr);
        text.setSelection(text.getText().toString().length());
    }

    //To Delete Single Line m
    public void deleteSingleLine(int m) {
        ArrayList<String> list = getLines();
        list.remove(m - 1);
        String newStr = convertArrayListToString(list);
        text.setText(newStr);
        text.setSelection(text.getText().toString().length());
    }

    //To Delete Multiple Lines from m to n (m and n both inclusive)
    public void deleteMultipleLines(int m, int n) {
        ArrayList<String> list = getLines();
        for (int i = 0; i < n - m + 1; i++) {
            list.remove(m - 1);
        }
        String newStr = convertArrayListToString(list);
        text.setText(newStr);
        text.setSelection(text.getText().toString().length());
    }

    //To copy the Lines
    public String copy(int m, int n) {
        ArrayList<String> list = getLines();
        StringBuilder sb = new StringBuilder();
        for (int i = m - 1; i <= n - 1; i++){
            sb.append(list.get(i));
        }

        return sb.toString();
    }

    //Convert ArrayList to String i.e. [ab,cd] = "abcd"
    private String convertArrayListToString(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String val : list) {
            sb.append(val);
        }
        return sb.toString();
    }

}
