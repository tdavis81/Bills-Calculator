package com.example.xxtyl.billscalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class CalulateActivity extends AppCompatActivity {

    public static ListView listView;
    public static DataAdapter dataAdapter;
    public static String[] labelArray = {};
    public static double[] numberArray = {};
    public static double[] textBoxValue;
    public static String userInput = "0";
    public static boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calulate);
        final EditText monthlyIncome = (EditText) findViewById(R.id.monthlyIncomeTxt);
        final EditText grossIncome = (EditText) findViewById(R.id.result);
        final Button calc = (Button) findViewById(R.id.calculate);
        final Button resetBtn = (Button) findViewById(R.id.reset);
        final double totalBillNumbers = 0.0;


        if(!clicked) {
            amountOfBillsPopup();
        } else {
            initArray();

            listView = findViewById(R.id.listView);
            dataAdapter = new DataAdapter(getBaseContext(), numberArray, labelArray);
            listView.setAdapter(dataAdapter);
            setReadOnlyTextBox(grossIncome);
            resetBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    resultFields(monthlyIncome,grossIncome);
                }
            });

            calc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(monthlyIncome.getText().toString().matches("") || textBoxValue == null ) {
                        Toast.makeText(getBaseContext(), "Data required in textboxes to calculate", Toast.LENGTH_SHORT).show();
                    } else {
                        calculateBills(monthlyIncome,grossIncome,totalBillNumbers);
                    }


                }

            });


        }
    }

    public class DataAdapter extends BaseAdapter {

        private Context context;
        public double[] numList;
        public String[] lblList;

        public DataAdapter(Context context, double[] numList, String[] lblList) {

            this.context = context;
            this.numList = numList;
            this.lblList = lblList;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }
        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public int getCount() {
            return numList.length;
        }

        @Override
        public Object getItem(int position) {
            return numList[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // items inside of view of custom layout on listview
            final DataAdapter.ViewHolder holder;

            if (convertView == null) {
                holder = new DataAdapter.ViewHolder();
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.custom_layout_edittext_textview, null, true);

                holder.recordTxt = (EditText) convertView.findViewById(R.id.billPriceTxt);
                holder.recordLabel = (TextView) convertView.findViewById(R.id.billPriceLabel);

                convertView.setTag(holder);

                //holder.recordTxt.setText(String.valueOf(numList[position]));
                holder.recordLabel.setText(lblList[position]);

                holder.recordTxt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        try {
                            numList[position] = convertToDouble(holder.recordTxt.getText().toString());
                            textBoxValue = numList;


                        } catch (NumberFormatException ex) {
                            numList[position] = -1;
                            textBoxValue = numList;
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) { }
                });

            }else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = (DataAdapter.ViewHolder)convertView.getTag();
            }

            return convertView;
        }

        private class ViewHolder {

            protected EditText recordTxt;
            protected TextView recordLabel;

        }

    }

    protected void amountOfBillsPopup() {

        AlertDialog.Builder amtBills = new AlertDialog.Builder(CalulateActivity.this);
        amtBills.setTitle("Set Bills List");
        amtBills.setMessage("Enter the amount of bills you have.");

        final EditText input = new EditText(CalulateActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setLayoutParams(lp);
        amtBills.setView(input);


        amtBills.setOnCancelListener(
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.cancel();
                        Toast.makeText(getBaseContext(), "Please select how many bills you have.", Toast.LENGTH_SHORT).show();
                        amountOfBillsPopup();
                    }
                }
        );

        amtBills.setNegativeButton("Enter",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userInput = input.getText().toString();
                        double tempInput = Double.parseDouble(userInput);
                        if(tempInput <=0) {
                            Toast.makeText(getBaseContext(), "Cannot enter below zero", Toast.LENGTH_SHORT).show();
                            amountOfBillsPopup();
                        } else {
                            clicked = true;
                            finish();
                            startActivity(getIntent());
                        }


                    }
                });


        AlertDialog saveData = amtBills.create();
        saveData.show();

    }

    protected void initArray() {
        int numOfBills = 0;

        numOfBills = Integer.parseInt(userInput);
        numberArray = new double[numOfBills];
        for(int i = 0; i < numberArray.length; i++) {
            numberArray[i] = 0;
        }

        labelArray = new String[numOfBills];
        for(int i = 0; i < labelArray.length; i++) {
            int f = i + 1;
            labelArray[i] = "Bill " + f;
        }
    }

    protected Double convertToDouble(String value) {
        String cleanString = value.replaceAll("[$,]", "");

        double parsed = Double.parseDouble(cleanString);

        return parsed;
    }

    protected void resultFields(EditText monthlyIncome, EditText grossIncome) {
        for(int i = 0; i < numberArray.length; i++) {
            numberArray[i] = 0;
        }
        monthlyIncome.setText("");
        grossIncome.setText("");
        listView = findViewById(R.id.listView);
        dataAdapter = new DataAdapter(getBaseContext(), numberArray, labelArray);
        listView.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }

    protected void calculateBills(EditText monthlyIncome, EditText grossIncome,double totalBillNumbers) {
        for(int i = 0; i < textBoxValue.length; i++) {
            totalBillNumbers += textBoxValue[i];
        }
        String getMonthlyIncome = monthlyIncome.getText().toString();
        double doubleMonthlyIncome = Double.parseDouble(getMonthlyIncome);
        double finalResult = doubleMonthlyIncome - totalBillNumbers;
        String income = Double.toString(finalResult);
        changeTextColor(grossIncome,finalResult);
        income = convertToCurrency(income);
        //getMonthlyIncome = convertToCurrency(getMonthlyIncome);
        grossIncome.setText(income);
        monthlyIncome.setText(getMonthlyIncome);
    }

    protected String convertToCurrency(String value) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        String formattedString = nf.format(Double.parseDouble(value));
        return formattedString;
    }

    protected void setReadOnlyTextBox(EditText grossIncome) {
        grossIncome.setEnabled(false);
    }

    protected void changeTextColor(EditText grossIncome, double finalResult) {
        if(finalResult >0.0) {
            grossIncome.setTextColor(Color.parseColor("#62f442"));
        } else {
            grossIncome.setTextColor(Color.parseColor("#f44141"));
        }
    }

}
