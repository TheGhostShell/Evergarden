/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hanami.cms.context.admin.application.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.Period;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * A service to create JWT objects, this one is used when an exchange
 * provides basic authentication.
 * If authentication is successful, a token is added in the response
 */
public class JWTTokenService {

    /**
     * Create and sign a JWT object using information from the current
     * authenticated principal
     *
     * @param subject     Name of current principal
     * @param credentials Credentials of current principal
     * @param authorities A collection of granted authorities for this principal
     * @return String representing a valid token
     */
    public static String generateToken(String subject, Object credentials,
                                       Collection<? extends GrantedAuthority> authorities) {
        //TODO refactor this nasty code

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(subject)
                .issuer("evergarden.shell")
                .expirationTime(new Date(getExpiration()))
                .claim("roles", authorities.stream()
                        .map(GrantedAuthority.class::cast)
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(",")))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(new JWTCustomSigner().getSigner());
        } catch (JOSEException e) {
            e.printStackTrace();
        }

        return signedJWT.serialize();
    }

    /**
     * Returns a millisecond time representation 24hrs from now
     * to be used as the time the currently token will be valid
     *
     * @return Time representation 24 from now
     */
    private static long getExpiration() {
        return new Date().toInstant()
                .plus(Period.ofDays(1))
                .toEpochMilli();
    }

    public static String generateGuestToken(String subject) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(Instant.now()));
        cal.add(Calendar.MINUTE, 5);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(subject)
                .issuer("evergarden.shell")
                .expirationTime(cal.getTime())
                .claim("roles", "ROLE_GUEST")
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

        try {
            signedJWT.sign(new JWTCustomSigner().getSigner());
        } catch (JOSEException e) {
            // TODO generate default token for guest with 1 year validity
        }
        return signedJWT.serialize();
    }
}
