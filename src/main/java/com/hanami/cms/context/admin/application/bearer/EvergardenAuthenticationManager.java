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
package com.hanami.cms.context.admin.application.bearer;

import com.hanami.cms.context.admin.application.jwt.JWTCustomSigner;
import com.hanami.cms.context.admin.application.jwt.JWTCustomVerifier;
import com.hanami.cms.context.admin.domain.entity.RoleEnume;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * An authentication manager intended to authenticate a JWT exchange
 * JWT tokens contain all information within the token itself
 * so an authentication manager is not necessary but we provide this
 * implementation to follow a standard.
 * Invalid tokens are filtered one previous step
 */
@Component
public class EvergardenAuthenticationManager implements ReactiveAuthenticationManager {
    
    Logger logger;
    
    @Autowired
    public EvergardenAuthenticationManager(Logger logger) {
        this.logger = logger;
    }
    
    /**
     * Successfully authenticate an Authentication object
     *
     * @param authentication A valid authentication object
     * @return authentication A valid authentication object
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        
        String authToken = authentication.getCredentials().toString();
        JWTCustomVerifier jwtCustomVerifier = new JWTCustomVerifier();
        
        Mono<SignedJWT> jwtCustomSignerMono = jwtCustomVerifier.check(authToken);

        List<SimpleGrantedAuthority> roles = new ArrayList<>();

        roles.add(new SimpleGrantedAuthority(RoleEnume.MASTER_ADMIN.toString()));
        roles.add(new SimpleGrantedAuthority(RoleEnume.GUEST.toString()));
        roles.add(new SimpleGrantedAuthority(RoleEnume.ADMIN.toString()));
        roles.add(new SimpleGrantedAuthority(RoleEnume.USER.toString()));

        Authentication au = new UsernamePasswordAuthenticationToken("violet",null, roles);
        
        return Mono.just(au);
    }
}
