package com.uzhnu.reedmuller;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.*;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;
import com.uzhnu.reedmuller.api.APIClient;
import com.uzhnu.reedmuller.api.MullerHandler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class StartupActivity extends BaseActivity
        implements MullerHandler.OnSuccessCompletedListener, NumberPicker.OnValueChangeListener {

    private NumberPicker numberPicker;
    private int rate;
    private TextView codeLength;

    public StartupActivity() {
        super(R.string.base_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        rate = 1;
        codeLength = (TextView) findViewById(R.id.codeLength);
        codeLength.setText(getCodeLength(rate));
        initNumberPicker();
        fetchMatrix();
    }

    private String getCodeLength(int rate){
        return getString(R.string.code_length) + rate + " = " + Math.pow(2, rate);
    }

    private void initNumberPicker() {
        String[] nums = {"1","2","3","4","5","6","7"};
        numberPicker = (NumberPicker) findViewById(R.id.np);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMaxValue(nums.length);
        numberPicker.setMinValue(1);
        numberPicker.setValue(rate);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setDisplayedValues(nums);
    }

    private void fetchMatrix() {
        APIClient client = new APIClient(this);
        MullerHandler handler = new MullerHandler(this, ("muller/" + rate));
        handler.setOnSuccessCompleted(this);
        client.getRemoteOrCached(handler);
    }

    public void generate(View v) {
        rate = numberPicker.getValue();
        fetchMatrix();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        numberPicker.setValue(rate);
        fetchMatrix();
    }

    @Override
    public void onSuccessCompleted(LinkedHashMap<String, LinkedList<Integer>> data) {
        TableLayout tableLayout = (TableLayout) findViewById(R.id.matrixTable);
        tableLayout.removeAllViews();

        LayoutInflater inflater = getLayoutInflater();

        MatrixTable table = new MatrixTable(inflater, tableLayout);
        table.generate(data);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        String text = getCodeLength(numberPicker.getValue());
        codeLength.setText(text);
    }

    private class MatrixTable {
        private LayoutInflater inflater;
        private ViewGroup matrixTable;

        public MatrixTable(LayoutInflater inflater, ViewGroup matrixTable) {
            this.inflater = inflater;
            this.matrixTable = matrixTable;
        }

        public void generate(LinkedHashMap<String,LinkedList<Integer>> data){
            Iterator it = data.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                LinkedList<Integer> list = (LinkedList<Integer>) pairs.getValue();
                String vectorName = (String) pairs.getKey();

                ViewGroup tableRow = (ViewGroup) inflater.inflate(R.layout.table_row, matrixTable, false);
                createCell(tableRow, vectorName);
                populateTableRow(tableRow, list);
                matrixTable.addView(tableRow);

                it.remove();
            }
        }

        private void populateTableRow(ViewGroup tableRow, LinkedList<Integer> list) {
            for(Integer bit: list){
                createCell(tableRow, String.valueOf(bit));
            }
        }

        private void createCell(ViewGroup tableRow, String text) {
            TextView textView = (TextView) inflater.inflate(R.layout.table_cell, tableRow, false);
            textView.setText(text);
            tableRow.addView(textView);
        }
    }
}
