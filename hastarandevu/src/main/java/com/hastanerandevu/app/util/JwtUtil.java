package com.hastanerandevu.app.util;

import com.hastanerandevu.app.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final String secretKey = "ctrlAltDeleteSuperSecretKey123456789121234567893456789123456789";
    // en az 32 karakter Token icin gizli anahtar
    private static final long expirationTime=1000*60*60;//1 saat gecerli

    //kullancii icin JWT token olusturma
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role",user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    //Tokeni dogrulamak icin
    public boolean validateToken(String token){
        try{
          Jwts.parserBuilder()
                  .setSigningKey(secretKey)
                  .build()
                  .parseClaimsJws(token);
            return true;
        }catch (Exception exception){
            return false;
        }
    }
    // Token içinden kullanıcı email’ini çekme
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Token içinden kullanıcı rolünü çekme
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
    // Token içinden claim bilgisini çekme
    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);    //metod token içindeki bilgileri çekmek için genişletilebilir
    }


}
