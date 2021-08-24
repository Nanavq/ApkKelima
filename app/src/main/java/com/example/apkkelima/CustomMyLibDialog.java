package com.example.apkkelima;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gzeinnumer.edf.MyLibDialog;

public class CustomMyLibDialog extends MyLibDialog {

    public static CustomMyLibDialog newInstance() {
        return new CustomMyLibDialog();
    }

    public CustomMyLibDialog() {
        super(R.style.CustomDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();

        // disable cancel
        //getDialog().setCancelable(false);

        // disable dismiss dialog out side
        //getDialog().setCanceledOnTouchOutside(false);

        // set dialog full screen
//        setFullScreen(true);

        // set canvas width, avaliable value 0.1 - 1.0
        setCanvasWidth(0.9);

        // set BackButton to dismiss dialog
        enableBackButton(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_schedule_crud, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout _dialogCanvas = view.findViewById(R.id.dialog_canvas);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        _dialogCanvas.setBackground(requireActivity().getResources().getDrawable(R.drawable.rounded_corner));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }
}


