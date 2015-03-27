package com.mycompany.todolist;


import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

/**
 * Created by ekucukog on 3/25/2015.
 */
public class EditTodoDialog extends DialogFragment// implements TextView.OnEditorActionListener
{

    private EditText mEditText;
    private EditText mEditPri;
    private int mPos;
    private static final String TAG = EditTodoDialog.class.getSimpleName();
    Button saveButton;

    public interface EditTodoDialogListener {
        void onFinishEditDialog(int position, String inputText, int inputPri);
    }

    public EditTodoDialog() {
        // Empty constructor required for DialogFragment
    }

    public static EditTodoDialog newInstance(int position, String oldDesc, int oldPri, String title) {
        EditTodoDialog frag = new EditTodoDialog();
        Bundle args = new Bundle();
        args.putInt("pos", position);
        args.putString("olddesc", oldDesc);
        args.putInt("oldpri", oldPri);
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
        String title = getArguments().getString("title", "Edit below");

        getDialog().setTitle(title);

        mEditText = (EditText) view.findViewById(R.id.editText);
        mEditPri = (EditText) view.findViewById(R.id.editPri);
        mEditText.setText(oldDesc);
        mEditPri.setText(""+oldPri);

        // Show soft keyboard automatically
        mEditText.requestFocus();
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //mEditText.setOnEditorActionListener(this);
        saveButton = (Button) view.findViewById(R.id.buttonSaveItem);
        setupButtonListener();

        return view;
    }

    // Fires whenever the textfield has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)
/*    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
            EditTodoDialogListener listener = (EditTodoDialogListener) getActivity();
            listener.onFinishEditDialog(mPos, mEditText.getText().toString(),(int)Integer.parseInt(mEditPri.getText().toString()));
            dismiss();
            return true;
        }
        return false;
    }*/

    private void setupButtonListener(){

        saveButton.setOnClickListener(new View.OnClickListener() {

            // Called when user clicks Save button
            public void onClick(View v) {

                Log.d(TAG, "save button click event");

                // Return input text to activity
                EditTodoDialogListener listener = (EditTodoDialogListener) getActivity();
                listener.onFinishEditDialog(mPos, mEditText.getText().toString(),(int)Integer.parseInt(mEditPri.getText().toString()));
                dismiss();
            }
        });
    }
}
