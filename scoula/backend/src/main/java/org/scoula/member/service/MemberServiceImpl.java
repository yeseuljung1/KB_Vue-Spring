package org.scoula.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.member.dto.ChangePasswordDTO;
import org.scoula.member.dto.MemberDTO;
import org.scoula.member.dto.MemberJoinDTO;
import org.scoula.member.dto.MemberUpdateDTO;
import org.scoula.member.excepition.PasswordMissmatchException;
import org.scoula.member.mapper.MemberMapper;
import org.scoula.security.account.domain.AuthVO;
import org.scoula.security.account.domain.MemberVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{


    final PasswordEncoder passwordEncoder; //비밀번호 암호화
    final MemberMapper mapper; //db접근을 위한 매퍼

    @Override
    public boolean checkDuplicate(String username) {
        MemberVO member = mapper.findByUsername(username); //주어진 사용자이름으로 회원정보 조회
        return member != null ? true : false; //이미 있는 사용자라면 true 반환(중복 여부 반환)
    }

    @Override
    public MemberDTO get(String username) {
        MemberVO member = Optional.ofNullable(mapper.get(username))
                .orElseThrow(NoSuchElementException::new);
        return MemberDTO.of(member); //회원 정보를 dto로 변환하여 반환
    }

    //아바타 이미지를 저장하는 메소드
    private void saveAvatar(MultipartFile avatar, String username) {
//아바타 업로드
        if(avatar != null && !avatar.isEmpty()) {
            File dest = new File("c:/upload/avatar", username + ".png");
            try {
                avatar.transferTo(dest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Transactional
    @Override
    public MemberDTO join(MemberJoinDTO dto) {
        MemberVO member = dto.toVO();
        member.setPassword(passwordEncoder.encode(member.getPassword())); // 비밀번호 암호화
        mapper.insert(member);
        AuthVO auth = new AuthVO();
        auth.setUsername(member.getUsername());
        auth.setAuth("ROLE_MEMBER");
        mapper.insertAuth(auth);
        saveAvatar(dto.getAvatar(), member.getUsername());
        return get(member.getUsername());
    }
    @Override
    public MemberDTO update(MemberUpdateDTO member) {
        MemberVO vo = mapper.get(member.getUsername());
        if(!passwordEncoder.matches(member.getPassword(),vo.getPassword())) { // 비밀번호 일치 확인
            throw new PasswordMissmatchException();
        }
        mapper.update(member.toVO());
        saveAvatar(member.getAvatar(), member.getUsername());
        return get(member.getUsername());
    }
    @Override
    public void changePassword(ChangePasswordDTO changePassword) {
        MemberVO member = mapper.get(changePassword.getUsername());
        if(!passwordEncoder.matches(changePassword.getOldPassword(), member.getPassword())) {
            throw new PasswordMissmatchException();
        }
        changePassword.setNewPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        mapper.updatePassword(changePassword);
    }
}
