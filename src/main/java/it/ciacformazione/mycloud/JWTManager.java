/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.ciacformazione.mycloud;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import javax.json.stream.JsonParser;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.eclipse.microprofile.jwt.Claims;

/**
 *
 * @author alfonso
 */
public class JWTManager {

    public static String generateJWTString(String jsonResource) {
        return generateJWTString(jsonResource, null);
    }

    public static String generateJWTString(String jsonResource, String usr) {

        try {

            byte[] byteBuffer = new byte[16384];
            Thread.currentThread().getContextClassLoader()
                    .getResource(jsonResource)
                    .openStream()
                    .read(byteBuffer);

            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            JSONObject jwtJson = (JSONObject) parser.parse(byteBuffer);

            long currentTimeInSecs = (System.currentTimeMillis() / 1000);
            long expirationTime = currentTimeInSecs + 1000;

            if (usr != null) {
                jwtJson.put(Claims.upn.name(), usr);
            }
            jwtJson.put(Claims.iat.name(), currentTimeInSecs);
            jwtJson.put(Claims.auth_time.name(), currentTimeInSecs);
            jwtJson.put(Claims.exp.name(), expirationTime);

            System.out.println("------- JWT unsigned ----------------");
            System.out.println(jwtJson);

            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .keyID("/privateKey.pem")
                    .type(JOSEObjectType.JWT)
                    .build(), JWTClaimsSet.parse(jwtJson));

            signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));
            String token = signedJWT.serialize();
            System.out.println("------------ curl command for test -------------");
            System.out.println("curl -i -H'Authorization: Bearer " + token + "' http://localhost:8080/mycloud/resources/users");
            return token;
        } catch (JOSEException | IOException | ParseException | net.minidev.json.parser.ParseException ex) {
            throw new RuntimeException("error in create JWT string", ex);
        }
    }

    public static PrivateKey readPrivateKey(String resourceName) {
        try {
            byte[] byteBuffer = new byte[16384];
            int length = Thread.currentThread().getContextClassLoader()
                    .getResource(resourceName)
                    .openStream()
                    .read(byteBuffer);

            String key = new String(byteBuffer, 0, length).replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)----", "")
                    .replaceAll("\r\n", "")
                    .replaceAll("\n", "")
                    .trim();

            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("error reading private key", ex);
        }
    }
}
