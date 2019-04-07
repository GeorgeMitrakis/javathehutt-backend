package back.util;

import back.model.User;
import back.conf.Configuration;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Map;

public class TokenFactory {

    private static final Key k = Keys.hmacShaKeyFor("javathehuttjavathehuttjavathehutt".getBytes());




    public static String getTokenFor(Map<String, Object> claimsMap){
        return Jwts.builder().signWith(k).setClaims(claimsMap).compact();
    }
}
