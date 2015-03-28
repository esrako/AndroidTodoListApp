package com.mycompany.todolist;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by ekucukog on 3/25/2015.
 */
public class EditTodoDialog extends DialogFragment// implements TextView.OnEditorActionListener
{

    private EditText mEditText;
    private EditText mEditPri;
    private EditText mEditDue;
    private int mPos;
    private static final String TAG = EditTodoDialog.class.getSimpleName();
    Button saveButton;
    Button pickDateButton;
    private int mYear=0, mMonth=0, mDay=0;
    private Context m_Context;

    public interface EditTodoDialogListener {
        void onFinishEditDialog(int position, String inputText, int inputPri, String inputDue);
    }

    public EditTodoDialog() {
        // Empty constructor required for DialogFragment
    }

    public EditTodoDialog(Context conte) {
        m_Context = conte;
    }

    public static EditTodoDialog newInstance(Context c, int position, String oldDesc, int oldPri, String oldDue, String title) {
        EditTodoDialog frag = new EditTodoDialog(c);
        Bundle args = new Bundle();

        args.putInt("pos", position);
        args.putString("olddesc", oldDesc);
        args.putInt("oldpri", oldPri);
        args.putString("olddue", oldDue);
        args.putString("title", title);

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_todo, container);

        mPos = getArguments().getInt("pos");
        String oldDesc = getArguments().getString("olddesc");
        int oldPri = getArguments().getInt("oldpri");
        String oldDue = getArguments().getString("olddue");
        String title = getArguments().getString("title", "Edit below");

        getDialog().setTitle(title);

        mEditText = (EditText) view.findViewById(R.id.editText);
        mEditPri = (EditText) view.findViewById(R.id.editPri);
        mEditDue = (EditText) view.findViewById(R.id.txtDate);
        mEditText.setText(oldDesc);
        mEditPri.setText(""+oldPri);
        mEditDue.setText(oldDue);

        // Show soft keyboard automatically
        mEditText.requestFocus();

        saveButton = (Button) view.findViewById(R.id.buttonSaveItem);
        pickDateButton = (Button) view.findViewById(R.id.pick_date_button);

        setupButtonListener();
        setupPickDateListener();

        return view;
    }

    private void setupButtonListener(){

        saveButton.setOnClickListener(new View.OnClickListener() {

            // Called when user clicks Save button
            public void onClick(View v) {

                Log.d(TAG, "save button click event");

                // Return input text to activity
                EditTodoDialogListener listener = (EditTodoDialogListener) getActivity();
                listener.onFinishEditDialog(mPos,
                                            mEditText.getText().toString(),
                                            (int)Integer.parseInt(mEditPri.getText().toString()),
                                            mEditDue.getText().toString() );
                dismiss();
            }
        });
    }


    private void setupPickDateListener(){

        pickDateButton.setOnClickListener(new View.OnClickListener() {

            // Called when user clicks Save button
            public void onClick(View v) {

                Log.d(TAG, "Entering showDatePickerDialog");

                // Process to get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(m_Context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                mEditDue.setText(TodoActivity.dateToString(year, monthOfYear, dayOfMonth));
                            }
                        }, mYear, mMonth, mDay);

                Log.d(TAG, "mYear: " + mYear + ", mMonth: " + mMonth + ", mDay: " + mDay);
                Log.d(TAG, "Done with showTimePickerDialog");
                dpd.show();
            }
        });
    }
}
