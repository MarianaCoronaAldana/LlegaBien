package com.example.llegabien.backend.app;
import android.annotation.SuppressLint;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encriptar {

    private final static  String passwordEncriptacion = "3bu2szseikstS%WeWCkPFG&Eh^Wyef#&^EQZ!&e7";

    public static String EncriptarContrasena(String textoPlano){
        String textoEncriptado;
        try{
            textoEncriptado = mEncriptar(textoPlano);
            return textoEncriptado;
        } catch (Exception e){
            return textoPlano;
        }
    }

    private static String mEncriptar(String datos) throws Exception{
        SecretKeySpec secretKey = generateKey();
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        return Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
    }

    private static SecretKeySpec generateKey() throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = Encriptar.passwordEncriptacion.getBytes(StandardCharsets.UTF_8);
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }

}
