package com.example.myeyes.UI;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myeyes.Constant;
import com.example.myeyes.Database;
import com.example.myeyes.Inferface.AdrressDialogListener;
import com.example.myeyes.R;

import org.w3c.dom.Text;

public class DeleteAdrressDialog {

    private Context context;
    private Button deleteAdrressButton,cancleButton;
    private String deleteAdrress;
    public AdrressDialogListener adrressDialogListener;
    public Dialog dialog;
    public TextView textView;

    public DeleteAdrressDialog(Context context, String deleteAdrress){
        this.context = context;
        this.deleteAdrress = deleteAdrress;
    }

    public void setDialogListener(AdrressDialogListener adrressDialogListener){
        this.adrressDialogListener = adrressDialogListener;
    }

    public void makeDeleteAdrressDialog(){
        dialog = new Dialog(context);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_delete_adrress);

        dialog.show();

        deleteAdrressButton = (Button)dialog.findViewById(R.id.DeleteRegisterAdrressButton);
        cancleButton = (Button)dialog.findViewById(R.id.CancleDeleteAdrressButton);
        textView = (TextView)dialog.findViewById(R.id.DeleteAdrressText);

        textView.setText(deleteAdrress + " 주소를 삭제 하시겠습니까?");

        deleteAdrressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database.getInstance(context).deleteAdrress(Database.getInstance(context).getWritableDatabase(),deleteAdrress);
                adrressDialogListener.onRegisterButtonClicked();
                dialog.dismiss();
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }
}
