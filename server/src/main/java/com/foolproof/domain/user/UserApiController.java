package com.foolproof.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foolproof.domain.user.dto.UserJoinDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserApiController {
    
    private final UserJoinService userJoinService;

    @PostMapping("/join")
    public ResponseEntity<UserJoinResult> addUser(@RequestBody UserJoinDTO request) {
        Boolean saveSuccess = userJoinService.save(request);
        UserJoinResult result = new UserJoinResult(request.getUsername(), saveSuccess, !saveSuccess);
        if (saveSuccess)
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
        else
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(result);
    }
}

class UserJoinResult {
    private String username;
    private Boolean joinStatus;
    private Boolean isDuplicate;

    public UserJoinResult() {

    }

    public UserJoinResult(String username, Boolean joinStatus, Boolean isDuplicate) {
        this.username = username;
        this.joinStatus = joinStatus;
        this.isDuplicate = isDuplicate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(Boolean joinStatus) {
        this.joinStatus = joinStatus;
    }


    public Boolean getIsDuplicate() {
        return isDuplicate;
    }

    public void setIsDuplicate(Boolean isDuplicate) {
        this.isDuplicate = isDuplicate;
    }
}
