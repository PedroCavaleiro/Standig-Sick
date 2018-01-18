package pt.ubi.pedrocavaleiro.standingsick;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.util.Log;
import java.math.BigInteger;

/**
 * Criado por pedrocavaleiro em 17/11/17.
 *
 * Bugs:
 *  Última verificação 2/11/2017 17:00 - 0 Encontrados
 *
 * Todo:
 *  Nada em falta
 */

public class PasswordHandler {

    /**
     * Gera o hash de uma password utilizando o algoritmo SHA-256
     * @param clearTextPassword password para ser efetuado o hash
     * @return hash da password em string
     */
    public static String hashPassword(String clearTextPassword) {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Log.e("StandingSick", "Não foi possível encontrar o algoritmo de hash");
            return "";
        }

        digest.update( clearTextPassword.getBytes(StandardCharsets.UTF_8));
        byte[] bHash = digest.digest();
        return byteToString(bHash);

    }

    /**
     * Converte o array de bytes gerado pela função de hash para string
     * @param hash bytes do hash da password
     * @return string do hash da password
     */
    private static String byteToString(byte[] hash) {
        return String.format("%064x", new BigInteger(1, hash));
    }

}
