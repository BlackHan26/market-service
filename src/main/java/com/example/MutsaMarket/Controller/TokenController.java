
package com.example.MutsaMarket.Controller;

import com.example.MutsaMarket.jwt.JwtRequestDto;
import com.example.MutsaMarket.jwt.JwtTokenDto;
import com.example.MutsaMarket.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("token")
public class TokenController {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;

    public TokenController(
            JwtTokenUtils jwtTokenUtils,
            UserDetailsManager manager,
            PasswordEncoder passwordEncoder
    ) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
    }
    @PostMapping("/issue")
    public JwtTokenDto issueJwt(@RequestBody JwtRequestDto dto) {
        UserDetails userDetails
                = manager.loadUserByUsername(dto.getUsername());

        // passwordEncoder.matches(rawPassword, encodedPassword)
        // 평문 비밀번호와 암호화 비밀번호를 비교할 수 있다.
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        JwtTokenDto response = new JwtTokenDto();
        response.setToken(jwtTokenUtils.generateToken(userDetails));
        return response;
    }
    @PostMapping("/secured")
    public String checkSecure(){
        log.info(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
        return "success";
    }
    //auth-token
    @GetMapping("/val")
    public Claims val(@RequestParam("token") String jwt) {
        return jwtTokenUtils.parseClaims(jwt);
    }

}