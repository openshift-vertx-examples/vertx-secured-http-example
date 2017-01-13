package org.obsidiantoaster.quickstart;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.vertx.ext.auth.jwt.impl.JWT;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Test {

    public static void main(String[] args) {
        /* Red Hat SSO Key */
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjSLQrbpwNkpuNc+LxcrG711/oIsqUshISLWjXALgx6/L7NItNrPjJTwzqtWCTJrl0/eQLcPdi7UeZA1qjPGa1l+AIj+FnLyCOl7gm65xB3xUpRuGNe5mJ9a+ZtzprXOKhd0WRC8ydiMwyFxIQJPjt7ywlNvU0hZR1U3QboLRICadP5WPaoYNOaYmpkX34r+kegVfdga+1xqG6Ba5v2/9rRg74KxJubCQxcinbH7gVIYSyFQPP5OpBo14SuynFL1YhWDpgUhLz7gr60sG+RC5eC0zuvCRTELn+JquSogPUopuDej/Sd3T5VYHIBJ8P4x4MIz9/FDX8bOFwM73nHgL5wIDAQAB";

        /* Token Received by Vert.x when running locally */
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiJkODgzMDI0NS1lM2YxLTRiZTctYjZiNi0xOGZmM2FiMWNiY2YiLCJleHAiOjE0ODQzMDE5NjQsIm5iZiI6MCwiaWF0IjoxNDg0MzAxOTA0LCJpc3MiOiJodHRwczovL3NlY3VyZS1zc28tc3NvdmVydHguZThjYS5lbmdpbnQub3BlbnNoaWZ0YXBwcy5jb20vYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoiZGVtb2FwcCIsInN1YiI6ImRkNmIxNmIxLWYzZGItNDEwYS1iMGM0LWQxYjBmYzIzMDA4YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRlbW9hcHAiLCJzZXNzaW9uX3N0YXRlIjoiYWZhYzZiYzYtMTE4Ni00OWEzLWJhZDMtMzY4ZjhiZDZlNzk1IiwiY2xpZW50X3Nlc3Npb24iOiIzYWUzNWRhMi0yZjlmLTQ1N2MtODg5Ni0zZjYzNDg2NGQ5ZmEiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiY3JlYXRlLXJlYWxtIiwiYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJtYXN0ZXItcmVhbG0iOnsicm9sZXMiOlsibWFuYWdlLWV2ZW50cyIsInZpZXctcmVhbG0iLCJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsIm1hbmFnZS1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwidmlldy1ldmVudHMiLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1jbGllbnRzIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIn0.Tteyc3DTuxizVi4tHxgcPgKcrA0kcp58UHsBWynnLIFaS3m2mfm5-F_oZzrVcRSmGoYPyPlTegaNIiGAQKzeiEWqgJHOfQLOXMP8ch7P5U2wG0MsOq36_jERK3ykjytUMaZNviCVzK0_Sn3_Aqkh_Q9G5dC_gN0a79jX33RR1v91163Plu72FzJEu-1FSYHLZBiX2n-uhJA1QmjpNKCtKnZldzZjFr0IFpOFOAlSJxl75TnNG1gqXNhpubG9Y4ICfVnjr9AdEhzAfdjWe6vr_SK16ifASR0Ckl6LMpha0WCuTnpE6uDS1OYs_a-Py666_zeuhTUoKyV_w6jzBgky1g";

        /* Token Received by Vert.X when running as a pod */
        // String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIwNWQ3Nzk5OS0yMzU1LTRlMjItOWI1MS01NmQzNGI2OTIxMmQiLCJleHAiOjE0ODQzMDIwNTQsIm5iZiI6MCwiaWF0IjoxNDg0MzAxOTk0LCJpc3MiOiJodHRwczovL3NlY3VyZS1zc28tc3NvdmVydHguZThjYS5lbmdpbnQub3BlbnNoaWZ0YXBwcy5jb20vYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoiZGVtb2FwcCIsInN1YiI6ImRkNmIxNmIxLWYzZGItNDEwYS1iMGM0LWQxYjBmYzIzMDA4YyIsInR5cCI6IkJlYXJlciIsImF6cCI6ImRlbW9hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNmQwZjhjNjYtOGNlYi00MDQwLWI2MjEtOWY4NjA1YzlkZDg1IiwiY2xpZW50X3Nlc3Npb24iOiIzMzAwYzE4MC1jZDg5LTRkZWUtOWU0Zi0wZjU4YTMxMDMzMDAiLCJhbGxvd2VkLW9yaWdpbnMiOltdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiY3JlYXRlLXJlYWxtIiwiYWRtaW4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJtYXN0ZXItcmVhbG0iOnsicm9sZXMiOlsibWFuYWdlLWV2ZW50cyIsInZpZXctcmVhbG0iLCJ2aWV3LWlkZW50aXR5LXByb3ZpZGVycyIsIm1hbmFnZS1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwidmlldy1ldmVudHMiLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1jbGllbnRzIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IiIsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluIn0.H-dkkAkxx6Oj6gx4DADLiHDxSdreZQvELhCR3YToQDQjhqUbGzFNamc-JMW1HHifIjhGEikKW9FzPqqqW9nchPOJDS9UxcPyxsYrY4jW63_eBOv6xIBpUQfBH3ZRbp2-GXr-MvAeCLz2uhEzefg7_oLHQHmy3ra5T_gSwiATHCcAWQ6bo1c1Y3PC6U_XfQ6tUHEAkoz0cvoPuYZEB2TlbHA9I9XFu2USXO-MPW5sddy7WmeQrG9gLs9dsYlrcOZnJ2Sz332o_Gb6HLOhmXcI4B2xcuQC4thcA97EkbFsKRgsYTZJ94KYPgxXu-gWtzAzA7zHUC0COefCtfg7DmoILA";

        try {
            useAuth0(key, token);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            useVertx(key, token);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void useAuth0(String key, String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(key));

        JWTVerifier verifier = com.auth0.jwt.JWT.require(Algorithm.RSA256((RSAKey) kf.generatePublic(spec))).build(); //Reusable verifier instance
        verifier.verify(token);
    }

    public static void useVertx(String key, String token) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JWT jwt = new JWT(key);
        System.out.println(
                jwt.decode(token).encodePrettily()
        );
    }
}
