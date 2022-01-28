package r3.aurangabad.rthreetrade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ngx.BluetoothPrinter;
import com.ngx.DebugLog;
import com.ngx.Enums.NGXBarcodeCommands;
import com.ngx.PrinterWidth;

import ngxtech.selector.FileOperation;
import ngxtech.selector.FileSelector;
import ngxtech.selector.OnHandleFileListener;

public class UnicodeFragment extends Fragment {
    public static final int LOGO_SET_OK = 999;
    private static final int RESULT_LOAD_IMAGE = 99;
    private RadioButton rbTwoInch, rbThreeInch;
    final String[] mFileFilter = {".png", ".bmp", ".jpeg", ".jpg"};
    private BluetoothPrinter mBtp = BluetoothPrinterMain.mBtp;


    /* public View.OnClickListener onbtnBarcodeClicked = new View.OnClickListener() {
         public void onClick(View v) {
             String barcodeString = ((EditText) getView().findViewById(R.id.txtBarcode)).getText().toString(); // ((EditText)v.findViewById(R.id.txtBarcode)).toString();
             if (barcodeString.isEmpty()) {
                 Toast.makeText(getActivity(), "Nothing to Print", Toast.LENGTH_SHORT).show();
                 return;
             }

             if (barcodeString.length() > 12) {
                 Toast.makeText(getActivity(), "Length of String is Greater that 12", Toast.LENGTH_SHORT).show();
                 return;
             }

             *//*TextPaint tp = new TextPaint();
            tp.setColor(Color.BLACK);
            tp.setTextSize(24);*//*
            mBtp.printBarCodeText(barcodeString);

        }
    };*/
    public OnClickListener onRbClicked = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Is the button now checked?
            boolean checked = ((RadioButton) v).isChecked();

            // Check which radio button was clicked
            switch (v.getId()) {
                case R.id.two_inch:
                    if (checked) {
                        boolean r = mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_48MM);
                        BluetoothPrinterMain.mSp.edit().putInt("PRINTER_SELECTION", 2).commit();
                        if (r) {
                            Toast.makeText(getActivity(), "Two Inch Printer Selected",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.three_inch:
                    if (checked) {
                        boolean r = mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_72MM);
                        BluetoothPrinterMain.mSp.edit().putInt("PRINTER_SELECTION", 3).commit();
                        if (r) {
                            Toast.makeText(getActivity(), "Three Inch Printer Selected",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };
    private FileSelector mFileSel;
    OnHandleFileListener mLoadFileListener = new OnHandleFileListener() {
        @Override
        public void handleFile(final String filePath) {

            Uri fileUri = Uri.parse(filePath);
//			mBtp.setLogo(fileUri.getPath(),true,false,127);
            BluetoothPrinterMain.mBtp.printImage(fileUri.getPath());
            //	mBtp.setLogo(fileUri.getPath());
            mFileSel.dismiss();
        }
    };
    private EditText mEdit;
    private Typeface tf = null;
    private int fontSize = 20;

	/*private void FillFonts()
	{
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.Fonts,android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sFont.setAdapter(arrayAdapter);
	}

	private void FillFontSize()
	{
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.FontSize,android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sFontSize.setAdapter(arrayAdapter);
	}*/

    public void printerSelection() {
        int printer_selection = BluetoothPrinterMain.mSp.getInt("PRINTER_SELECTION", 0);
        if (printer_selection == 3) {
            BluetoothPrinterMain.mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_72MM);
            rbThreeInch.setChecked(true);
        } else {
            BluetoothPrinterMain.mBtp.setPrinterWidth(PrinterWidth.PRINT_WIDTH_48MM);
            rbTwoInch.setChecked(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DebugLog.setDebugMode(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_unicode_fragment, container, false);

        initControls(view);
        return view;
    }

    private void initControls(final View v) {
        rbTwoInch = (RadioButton) v.findViewById(R.id.two_inch);
        rbTwoInch.setOnClickListener(onRbClicked);
        rbThreeInch = (RadioButton) v.findViewById(R.id.three_inch);
        rbThreeInch.setOnClickListener(onRbClicked);
        printerSelection();
        mEdit = (EditText) v.findViewById(R.id.txt);
        // Spinner sFont = (Spinner) v.findViewById(R.id.sFont);
        // Spinner sFontSize = (Spinner) v.findViewById(R.id.sFontSize);

        //Log.i("Layout width", sFont.getHeight() + "");

       /* sFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String fontPath = "";
                String selectedFontName = adapterView.getItemAtPosition(pos).toString();
                String fontName = selectedFontName + ".ttf";
                fontPath = "fonts/" + fontName;
                tf = null;
                tf = Typeface.createFromAsset(getActivity().getAssets(), fontPath);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("Font selection", "Nothing selected");
            }
        });*/

        /*sFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String font = adapterView.getItemAtPosition(pos).toString();
                fontSize = Integer.valueOf(font)*//*.intValue()*//*;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("Font Size selection", "Nothing selected");
            }
        });*/


        //All format Barcode Printing
        Button btnPrintBarcodeImg = (Button) v.findViewById(R.id.btnPrintBarcodeImg);
        btnPrintBarcodeImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    // for(int i=100;i<1000;i+=100) {
                    mBtp.printBarcode("12345678901234", NGXBarcodeCommands.CODE128, 100, 300,false);
                    mBtp.printUnicodeText("\n\n\n");
                    // }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    Toast.makeText(UnicodeFragment.this.getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*Button btnPrint = (Button) v.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tf != null) {
                    TextPaint tp = new TextPaint();
                    tp.setColor(Color.BLACK);
                    tp.setTextSize(fontSize);
                    tp.setTypeface(tf);
                    String txt = mEdit.getText().toString();
                    mBtp.printUnicodeText(txt, Alignment.ALIGN_NORMAL, tp);
                }
            }
        });*/

       /* Button btnPrintBarcode = (Button) v.findViewById(R.id.btnPrintBarcode);
        btnPrintBarcode.setOnClickListener(onbtnBarcodeClicked);*/


        Button btnPrintLogo = (Button) v.findViewById(R.id.btnPrintLogo);
        btnPrintLogo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //mBtp.disconnectFromPrinter();
                dialogBox();
                //	mBtp.printLogo();
            }
        });

	/*	Button btnChangeLogo = (Button) v.findViewById(R.id.btnChangeLogo);
		btnChangeLogo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
			//	mBtp.disconnectFromPrinter();
				dialogBox();
			}
		});*/

        Button btnUnicodeLeftAlign = (Button) v.findViewById(R.id.btnUnicodeLeftAlign);
        btnUnicodeLeftAlign.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(16);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_NORMAL, tp);
            }
        });

        Button btnUnicodeRightAlign = (Button) v
                .findViewById(R.id.btnUnicodeRightAlign);
        btnUnicodeRightAlign.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(16);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_OPPOSITE, tp);
            }
        });

        Button btnUnicodeCenterAlign = (Button) v
                .findViewById(R.id.btnUnicodeCenterAlign);
        btnUnicodeCenterAlign.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(16);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_CENTER, tp);
            }
        });

        Button btnPrintFs16 = (Button) v.findViewById(R.id.btnPrintFs16);
        btnPrintFs16.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(16);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_NORMAL, tp);
            }
        });
        Button btnPrintFs20 = (Button) v.findViewById(R.id.btnPrintFs20);
        btnPrintFs20.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(20);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_NORMAL, tp);
            }
        });
        Button btnPrintFs24 = (Button) v.findViewById(R.id.btnPrintFs24);
        btnPrintFs24.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(24);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_NORMAL, tp);
            }
        });
        Button btnPrintFs28 = (Button) v.findViewById(R.id.btnPrintFs28);
        btnPrintFs28.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                TextPaint tp = new TextPaint();
                tp.setColor(Color.BLACK);
                tp.setTextSize(28);
                String txt = mEdit.getText().toString();
                mBtp.printUnicodeText(txt, Alignment.ALIGN_NORMAL, tp);
            }
        });


        Button btnGetHiTxt = (Button) v.findViewById(R.id.btnGetHiTxt);
        btnGetHiTxt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                /*String txt = "कम्प्यूटर, मूल रूप से, नंबरों से सम्बंध रखते हैं। ये प्रत्येक अक्षर और वर्ण के लिए एक नंबर निर्धारित करके अक्षर और वर्ण संग्रहित करते हैं। यूनिकोड का आविष्कार होने से पहले, ऐसे नंबर देने के लिए सैंकडों विभिन्न संकेत लिपि प्रणालियां थीं। किसी एक संकेत लिपि में पर्याप्त अक्षर नहीं हो सकते हैं : उदाहरण के लिए, यूरोपिय संघ को अकेले ही, अपनी सभी भाषाऒं को कवर करने के लिए अनेक विभिन्न संकेत लिपियों की आवश्यकता होती है। अंग्रेजी जैसी भाषा के लिए भी, सभी अक्षरों, विरामचिन्हों और सामान्य प्रयोग के तकनीकी प्रतीकों हेतु एक ही संकेत लिपि पर्याप्त नहीं थी।";
                mEdit.setText(txt);*/
                printHindiBill();
            }
        });

        Button btnGetEnTxt = (Button) v.findViewById(R.id.btnGetEnTxt);
        btnGetEnTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                printEnglishBill();
            }
        });

        Button btnGetKnTxt = (Button) v.findViewById(R.id.btnGetKnTxt);
        btnGetKnTxt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                printKannadaBill();
            }
        });

        Button btnGetTaTxt = (Button) v.findViewById(R.id.btnGetTaTxt);
        btnGetTaTxt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String txt = "யூனிக்கோடு எந்த இயங்குதளம் ஆயினும், எந்த நிரல் ஆயினும், எந்த மொழி ஆயினும் ஒவ்வொரு எழுத்துக்கும் தனித்துவமான எண் ஒன்றை வழங்குகிறது.";
                mEdit.setText(txt);
            }
        });
        /*Button btnGetTeTxt = (Button) v.findViewById(R.id.btnGetTeTxt);
        btnGetTeTxt.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String txt = "జరిగిందంతా చూస్తూ \nఎరగనట్లు పడి ఉండగ \nసాక్షీ భూతుణ్ణి గాను \nసాక్షాత్తూ మానవుణ్ణి";
                mEdit.setText(txt);
            }
        });*/


	/*	Button btnSetLogo = (Button) v.findViewById(R.id.btnChangeLogo);
		btnSetLogo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment sl = new SetLogo();
				BluetoothPrinterMain.changeFragment(R.id.container, sl, true);
			}
		});
*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE
                && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            BluetoothPrinterMain.mBtp.connectToPrinter(getActivity());
			/*try {
				while (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
					Thread.sleep(100);
					continue;
				}
			}
			catch (Exception e)
			{

			}*/

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if(cursor!=null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);

                cursor.close();
                Uri fileUri = Uri.parse(picturePath);
                if (fileUri != null) {
                    BluetoothPrinterMain.mBtp.printImage(fileUri.getPath());
                }
                else {
                    Toast.makeText(getActivity(),
                            "media handler not available, choose another image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void dialogBox() {
        CharSequence storage[] = new CharSequence[]{"Select from Gallery", "Select from SD Card"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a storage");
        builder.setItems(storage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, RESULT_LOAD_IMAGE);
//						loadImage(i);
                        break;
                    case 1:
                        //			mBtp.connectToPrinter();
                        mFileSel = new FileSelector(getActivity(), FileOperation.LOAD, mLoadFileListener, mFileFilter);
                        mFileSel.show();
                        break;
                }
            }
        });
        builder.show();
    }

    private void printEnglishBill() {
        try {
            if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
                Toast.makeText(this.getActivity(), "Printer is not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String separator = "--------------------------------";
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DroidSansMono.ttf");

            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);
            mBtp.addText("Cash Bill", Alignment.ALIGN_CENTER, tp);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Date: 31-05-2017  Bill No: 001\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Name: Vinayak\n");
            stringBuilder.append("Place: Bangalore\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Particulars    Qty   Rate    Amt\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Reynolds Pen     2     10     20\n");
            stringBuilder.append("Nataraj Eraser  10      5     50\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("Tot Items: 2      Amount: 66.50\n");
            stringBuilder.append("Tot Qty  :12     Vat Amt:  3.50\n");
            stringBuilder.append("                  -------------");
            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);
            stringBuilder.append("           Net Amt: 70.00");
            tp.setTextSize(25);
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);


            stringBuilder.setLength(0);
            stringBuilder.append("Payment Mode: CASH\n\n\n");
            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
            mBtp.print();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void printKannadaBill() {

        try {
            if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
                Toast.makeText(this.getActivity(), "Printer is not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String separator = "--------------------------------";
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DroidSansMono.ttf");

            TextPaint tp = new TextPaint();

            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);
            mBtp.addText("ನಗದು ಬಿಲ್ಲು", Alignment.ALIGN_CENTER, tp);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ದಿನಾಂಕ: 31-05-2017  ಬಿಲ್ ಸಂಖ್ಯೆ: 001\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("ಹೆಸರು: ವಿನಾಯಕ\n");
            stringBuilder.append("ಸ್ಥಳ: ಬೆಂಗಳೂರು\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("ವಿವರಣೆ        ಪ್ರಮಾಣ   ದರ    ಮೊತ್ತ\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("ರೆನಾಲ್ಡ್ಸ್ ಪೆನ್        2   10     20\n");
            stringBuilder.append("ನಟರಾಜ್ ಎರೇಸರ್    10    5     50\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("ಒಟ್ಟು ಐಟಂಗಳು: 2       ಮೊತ್ತ:  66.50\n");
            stringBuilder.append("ಒಟ್ಟು ಪ್ರಮಾಣ: 12   ವ್ಯಾಟ್ ಮೊತ್ತ: 3.50\n");
            stringBuilder.append("                  -------------");
            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);
            stringBuilder.append("         ನಿವ್ವಳ ಮೊತ್ತ: 70.00");
            tp.setTextSize(25);
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);
            stringBuilder.append("ಪಾವತಿ ಮೋಡ್: ನಗದು\n\n\n");
            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
            mBtp.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printHindiBill() {
        try {
            if (mBtp.getState() != BluetoothPrinter.STATE_CONNECTED) {
                Toast.makeText(this.getActivity(), "Printer is not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            String separator = "--------------------------------";
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DroidSansMono.ttf");
            TextPaint tp = new TextPaint();
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            tp.setTextSize(30);
            tp.setColor(Color.BLACK);
            mBtp.addText("नकद बिल", Alignment.ALIGN_CENTER, tp);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("दिनांक: 31-05-2017    बिल संख्या: 001\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("नाम: विनायक\n");
            stringBuilder.append("स्थान: बैंगलोर\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("विवरण         मात्रा     मूल्य     रकम\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("रेनॉल्ड्स पेन      2      10      20\n");
            stringBuilder.append("नटराज इरेज़र     10      5      50\n");
            stringBuilder.append(separator);
            stringBuilder.append("\n");
            stringBuilder.append("कुल सामान: 2            रकम: 66.50\n");
            stringBuilder.append("कुल मात्रा :12         वैट रकम:  3.50\n");
            stringBuilder.append("                  --------------");
            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);
            stringBuilder.append("            नेट रकम: 70.00");
            tp.setTextSize(25);
            tp.setTypeface(Typeface.create(tf, Typeface.BOLD));
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);

            stringBuilder.setLength(0);
            stringBuilder.append("भुगतान मोड: नकद\n\n\n");
            tp.setTextSize(20);
            tp.setTypeface(tf);
            mBtp.addText(stringBuilder.toString(), Alignment.ALIGN_NORMAL, tp);
            mBtp.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
