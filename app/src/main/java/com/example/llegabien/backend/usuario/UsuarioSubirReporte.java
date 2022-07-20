package com.example.llegabien.backend.usuario;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.InputStream;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class UsuarioSubirReporte {
    private Intent mIntent;
    public Intent getmIntent(){ return mIntent; }

    public void inicializarIntent(){
        mIntent = new Intent();
        //sets the select file to all types of files
        mIntent.setType("*/*");
        //allows to select data and return it
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
    }

    public void obtenerArchivo(int resultCode, Intent data, Fragment fragment) {
        if(resultCode == RESULT_OK){
                if(data != null){
                    leerArchivoExcel(data.getData(),fragment);
                }
            }
    }

    public void leerArchivoExcel(Uri uri, Fragment fragment) {
        try {
            InputStream inputStream = fragment.requireActivity().getContentResolver().openInputStream(uri);
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int row = sheet.getRows();
            int column = 1;
            StringBuilder nombreDireccion = new StringBuilder();

            for (int i = 0; i < row; i++)
            {
                for(int c = 0; c < column; c++) {
                    Cell cell = sheet.getCell(c, i);
                    nombreDireccion.append(cell.getContents());
                }
                nombreDireccion.append("\n");
            }
            Toast.makeText(fragment.getActivity(), "SI SE PUDO", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ignored){}
    }

}
