package com.cyruszhang.cluboard.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cyruszhang.cluboard.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;


public class ChangePasswordFragment extends DialogFragment {

    public ChangePasswordFragment() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_message_change_password)
                .setTitle(R.string.dialog_title_change_password);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View changePasswordView = inflater.inflate(R.layout.change_password_dialog, null);
        builder.setView(changePasswordView);
        Button buttonConfirm = (Button) changePasswordView.findViewById(R.id.change_password_confirm_button);
        Button buttonCancel = (Button) changePasswordView.findViewById(R.id.change_password_cancel_button);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText oldPw = (EditText) changePasswordView.findViewById(R.id.txt_change_password_old);
                EditText newPw = (EditText) changePasswordView.findViewById(R.id.txt_change_password_new);
                try {
                    ParseUser.logIn(ParseUser.getCurrentUser().getUsername(), oldPw.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Incorrect password. Please try again",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d("ChangePassword", newPw.getText().toString());
                ParseUser.getCurrentUser().setPassword(newPw.getText().toString());
                ParseUser.getCurrentUser().saveInBackground();
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully changed password",
                        Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
        return builder.create();

    }



}
