package cz.mendelu.xlinek.eduapp.utils;

import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.auth.oauth2.TokenVerifier;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
public class TokenPayload {
    private String token;
    private TokenInfo tokenInfo = new TokenInfo();

    @Value("${google.clientId}")
    private String client_id;

    public TokenPayload(String token){
        this.token = token;
        getPayload();
    }

    private void getPayload(){
        TokenVerifier tokenVerifier = TokenVerifier.newBuilder()
                .setAudience(client_id)
                .build();
        try {
            JsonWebToken jsonWebToken = tokenVerifier.verify(token);

            JsonWebToken.Payload payload = jsonWebToken.getPayload();

            tokenInfo.setEmail(payload.get("email").toString());
            tokenInfo.setName(payload.get("given_name").toString());
            tokenInfo.setSurname(payload.get("family_name").toString());
            tokenInfo.setPicture(payload.get("picture").toString());
            tokenInfo.setTokenMessage("OK");
            tokenInfo.setTokenValid(true);

        } catch (TokenVerifier.VerificationException e) {
            tokenInfo.setTokenValid(false);
            tokenInfo.setTokenMessage(e.getMessage());
        }
    }
}
