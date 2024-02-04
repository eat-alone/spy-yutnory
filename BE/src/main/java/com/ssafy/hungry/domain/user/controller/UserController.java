package com.ssafy.hungry.domain.user.controller;

import com.ssafy.hungry.domain.user.dto.JoinDto;
import com.ssafy.hungry.domain.user.dto.MyInfoDto;
import com.ssafy.hungry.domain.user.dto.PasswordDto;
import com.ssafy.hungry.domain.user.entity.UserEntity;
import com.ssafy.hungry.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    //회원가입
    @PostMapping("/join")
    public String join(@RequestBody JoinDto joinDto) {
        if(!userService.join(joinDto)){
            return "회원가입 실패";
        }
        return "회원가입 성공";
    }

    //임시비밀번호 발급을 위한 이메일 인증 요청
    @PostMapping("get-temporary-password-email-verification-request")
    public ResponseEntity<String> getTemporaryPasswordEmailVerificationRequest(@RequestParam("email") String email){
        userService.getTemporaryPasswordEmailVerificationRequest(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //임시 비밀번호 이메일 인증 검사
    @PostMapping("get-temporary-password-email-verification")
    public ResponseEntity<String> getTemporaryPassword(@RequestParam("email") String email, @RequestParam("code") String code){
        Boolean response = userService.getTemporaryPasswordEmailVerificationCode(email, code);
        if(response){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    //비밀번호 변경
    @PostMapping("change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordDto dto){
        if(userService.changePassword(dto.getPreviousPassword(), dto.getNextPassword())){
            return ResponseEntity.ok().body("비밀번호 변경 성공");
        }
        return ResponseEntity.badRequest().body("비밀번호 변경 실패");
    }

    //이메일 중복체크
    @GetMapping("/{userId}")
    public ResponseEntity checkId(@PathVariable("email") String email) {
        Boolean isExist = userService.checkId(email);

        Map<String, String> result = new HashMap<>();

        if (isExist) {
            result.put("message", "이미 존재하는 사용자 email 입니다.");
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            result.put("message", "사용 가능한 email 입니다.");
            return new ResponseEntity(result, HttpStatus.OK);
        }
    }

    //닉네임 중복체크
    @GetMapping("/{nickname}")
    public ResponseEntity checkNickname(@PathVariable("nickname") String nickname) {
        Boolean isExist = userService.checkNickname(nickname);

        Map<String, String> result = new HashMap<>();

        if (isExist) {
            result.put("message", "이미 존재하는 사용자 닉네임 입니다.");
            return new ResponseEntity(result, HttpStatus.OK);
        } else {
            result.put("message", "사용 가능한 닉네임 입니다.");
            return new ResponseEntity(result, HttpStatus.OK);
        }
    }

    //내정보조회
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        System.out.println("UserController.getInfo");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;

        try {
            UserEntity user = userService.findByEmail(email);
            MyInfoDto dto = MyInfoDto.builder()
                    .profileImgUrl(user.getProfileImgUrl())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .gender(user.getGender())
                    .birthDate(user.getBirthDate())
                    .build();
            resultMap.put("userInfo", dto);
            status = HttpStatus.OK;
        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map<String, Object>>(resultMap, status);
    }

    //닉네임 변경
    @PostMapping("change-nickname")
    public ResponseEntity<String> changeNickname(@RequestBody String nickname){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.changeNickname(email, nickname);
        return null;
    }

    //회원 탈퇴
    @DeleteMapping("/delete-account")
    public ResponseEntity<String> deleteUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, String> result = new HashMap<>();
        userService.deleteUser(email);
        return ResponseEntity.ok().body("회원 탈퇴 성공");
    }

    //메일 인증 요청 api
    @PostMapping("/emails/verification-requests")
    public ResponseEntity sendMessage(@RequestParam("email") @Valid String email) {
        userService.sendCodeToEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //메일 인증 api
    @GetMapping("/emails/verifications")
    public ResponseEntity verificationEmail(@RequestParam("email") @Valid String email,
                                            @RequestParam("code") String authCode) {
        Boolean response = userService.verifiedCode(email, authCode);
        if(response){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

}