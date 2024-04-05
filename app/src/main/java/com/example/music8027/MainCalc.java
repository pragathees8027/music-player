package com.example.music8027;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class MainCalc extends AppCompatActivity {

    private static final char add = '+';
    private static final char sub = '-';
    private static final char prod = '*';
    private static final char div = '/';
    private static final char mod = '%';
    private char curSymbol = '0';
    private double firstVal = Double.NaN;
    private double secondVal;
    private TextView inDisp, outDisp;
    private DecimalFormat decForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calc_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_calc), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        decForm = new DecimalFormat("#.#####");
        inDisp = findViewById(R.id.input);
        outDisp = findViewById(R.id.output);

        MaterialButton button0 = findViewById(R.id.zero);
        MaterialButton button1 = findViewById(R.id.one);
        MaterialButton button2 = findViewById(R.id.two);
        MaterialButton button3 = findViewById(R.id.three);
        MaterialButton button4 = findViewById(R.id.four);
        MaterialButton button5 = findViewById(R.id.five);
        MaterialButton button6 = findViewById(R.id.six);
        MaterialButton button7 = findViewById(R.id.seven);
        MaterialButton button8 = findViewById(R.id.eight);
        MaterialButton button9 = findViewById(R.id.nine);
        MaterialButton buttonDot = findViewById(R.id.dot);
        MaterialButton buttonAdd = findViewById(R.id.sum);
        MaterialButton buttonSub = findViewById(R.id.diff);
        MaterialButton buttonProd = findViewById(R.id.product);
        MaterialButton buttonDiv = findViewById(R.id.divide);
        MaterialButton buttonEqual = findViewById(R.id.result);
        MaterialButton buttonClear = findViewById(R.id.clear);
        MaterialButton buttonClose = findViewById(R.id.close);
        MaterialButton buttonRem = findViewById(R.id.rem);

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MainAct = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(MainAct);
                Toast.makeText(getApplicationContext(), "Closing Calculator", Toast.LENGTH_SHORT).show();
            }
        });

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol == '/' || curSymbol == '%'){
                } else {
                    inDisp.setText(inDisp.getText() + "0");
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "1");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "2");
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "3");
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "4");
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "5");
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "6");
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "7");
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "8");
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inDisp.setText(inDisp.getText() + "9");
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol != '0') {
                    if (inDisp.getText().length()>0) {
                        CalcOperations();
                        outDisp.setText(decForm.format(firstVal));
                        inDisp.setText(decForm.format(firstVal));
                        /*firstVal = Double.parseDouble(outDisp.getText().toString());*/
                        curSymbol = '0';
                        curSymbol = add;
                        outDisp.setText(decForm.format(firstVal) + "+");
                        inDisp.setText(null);
                    }
                } else {
                    firstVal = Double.parseDouble(inDisp.getText().toString());
                    CalcOperations();
                    curSymbol = add;
                    outDisp.setText(decForm.format(firstVal) + "+");
                    inDisp.setText(null);
                }
            }
        });

        buttonSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol != '0') {
                    if (inDisp.getText().length()>0) {
                        CalcOperations();
                        outDisp.setText(decForm.format(firstVal));
                        inDisp.setText(decForm.format(firstVal));
                        /*firstVal = Double.parseDouble(outDisp.getText().toString());*/
                        curSymbol = '0';
                        curSymbol = sub;
                        outDisp.setText(decForm.format(firstVal) + "-");
                        inDisp.setText(null);
                    }
                } else {
                    firstVal = Double.parseDouble(inDisp.getText().toString());
                    CalcOperations();
                    curSymbol = sub;
                    outDisp.setText(decForm.format(firstVal) + "-");
                    inDisp.setText(null);
                }
            }
        });

        buttonProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol != '0') {
                    if (inDisp.getText().length()>0) {
                        CalcOperations();
                        outDisp.setText(decForm.format(firstVal));
                        inDisp.setText(decForm.format(firstVal));
                        /*firstVal = Double.parseDouble(outDisp.getText().toString());*/
                        curSymbol = '0';
                        curSymbol = prod;
                        outDisp.setText(decForm.format(firstVal) + "*");
                        inDisp.setText(null);
                    }
                } else {
                    firstVal = Double.parseDouble(inDisp.getText().toString());
                    CalcOperations();
                    curSymbol = prod;
                    outDisp.setText(decForm.format(firstVal) + "*");
                    inDisp.setText(null);
                }
            }
        });

        buttonDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol != '0') {
                    if (inDisp.getText().length()>0) {
                        CalcOperations();
                        outDisp.setText(decForm.format(firstVal));
                        inDisp.setText(decForm.format(firstVal));
                        /*firstVal = Double.parseDouble(outDisp.getText().toString());*/
                        curSymbol = '0';
                        curSymbol = div;
                        outDisp.setText(decForm.format(firstVal) + "/");
                        inDisp.setText(null);
                    }
                } else {
                    firstVal = Double.parseDouble(inDisp.getText().toString());
                    CalcOperations();
                    curSymbol = div;
                    outDisp.setText(decForm.format(firstVal) + "/");
                    inDisp.setText(null);
                }
            }
        });

        buttonRem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol != '0') {
                    if (inDisp.getText().length()>0) {
                        CalcOperations();
                        outDisp.setText(decForm.format(firstVal));
                        inDisp.setText(decForm.format(firstVal));
                        /*firstVal = Double.parseDouble(outDisp.getText().toString());*/
                        curSymbol = '0';
                        curSymbol = mod;
                        outDisp.setText(decForm.format(firstVal)  + "%");
                        inDisp.setText(null);
                    }
                } else {
                    firstVal = Double.parseDouble(inDisp.getText().toString());
                    CalcOperations();
                    curSymbol = mod;
                    outDisp.setText(decForm.format(firstVal) + "%");
                    inDisp.setText(null);
                }
            }
        });

        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inDisp.getText().toString().matches("[0-9]*\\.[0-9]*")){
                } else if (inDisp.getText().toString().equals("")) {
                    inDisp.setText("0.");
                } else{
                    inDisp.setText(inDisp.getText() + ".");
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curSymbol != '0'){
                    CharSequence curText = outDisp.getText();
                    inDisp.setText(curText.subSequence(0,curText.length() - 1));
                    outDisp.setText(curText.subSequence(0,curText.length() - 1));
                    curSymbol = '0';
                } else if (inDisp.getText().length()>0){
                    CharSequence curText = inDisp.getText();
                    inDisp.setText(curText.subSequence(0,curText.length() - 1));
                } else {
                    firstVal = Double.NaN;
                    secondVal = Double.NaN;
                    inDisp.setText("");
                    outDisp.setText("");
                }
            }
        });

        buttonClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                firstVal = Double.NaN;
                secondVal = Double.NaN;
                inDisp.setText("");
                outDisp.setText("");
                return true;
            }
        });

        buttonEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inDisp.getText().toString().equals("")) {
                }
                else if (!inDisp.getText().toString().equals("") && curSymbol == '0'){
                    firstVal = Double.parseDouble(inDisp.getText().toString());
                    outDisp.setText(decForm.format(firstVal));
                    //inDisp.setText(decForm.format(firstVal));
                }else{
                    CalcOperations();
                    outDisp.setText(decForm.format(firstVal));
                    inDisp.setText(decForm.format(firstVal));
                    /*firstVal = Double.parseDouble(outDisp.getText().toString());*/
                    curSymbol = '0';
                }
            }
        });
    }
    private void CalcOperations(){
        if (!Double.isNaN(firstVal)){
            secondVal = Double.parseDouble(inDisp.getText().toString());
            inDisp.setText(null);

            switch (curSymbol) {
                case add:
                    firstVal = this.firstVal + secondVal;
                    break;

                case sub:
                    firstVal = this.firstVal - secondVal;
                    break;

                case prod:
                    firstVal = this.firstVal * secondVal;
                    break;

                case div:
                    if (secondVal == 0)
                        break;
                    firstVal = this.firstVal / secondVal;
                    break;

                case mod:
                    if (secondVal == 0)
                        break;
                    firstVal = this.firstVal % secondVal;
                    break;
            }
        } else {
            try{
                firstVal = Double.parseDouble(inDisp.getText().toString());
            } catch (Exception e){

            }
        }
    }
}