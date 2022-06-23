package com.example.llegabien.backend.app;
import android.util.Base64;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encriptar {

    private final static  String passwordEncriptacion = "3bu2szseikstS%WeWCkPFG&Eh^Wyef#&^EQZ!&e7";

    public static String Encriptar(String textoPlano){
        String textoEncriptado;
        try{
            textoEncriptado = mEncriptar(textoPlano, passwordEncriptacion);
            return textoEncriptado;
        } catch (Exception e){
            return textoPlano;
        }
    }

    private static String mEncriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;
    }

    private static SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }

}
