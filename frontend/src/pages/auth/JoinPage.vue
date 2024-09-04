<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import authApi from '@/api/authApi';

const router = useRouter();
const avatar = ref(null);
const checkError = ref('');

// 회원가입 폼에 사용될 반응형 테스트 객체 생성
const member = reactive({
    // 테스트용 초기화
    username: 'yeseul',
    email: 'yeseul@gmail.com',
    password: '1234',
    password2: '1234',
    avatar: null,
});

const disableSubmit = ref(true); //제출버튼 비활성화 여부
// username 중복 체크
const checkUsername = async () => {
    // 사용자 이름이 입력되지 않은 경우
    if (!member.username) {
        return alert('사용자 ID를 입력하세요.');
    }

    // 이름이 중복된 경우 제출 버튼 비황성화
    disableSubmit.value = await authApi.checkUsername(member.username);
    console.log(disableSubmit.value, typeof disableSubmit.value);
    // 제출 버튼이 비활성화 상태면 id가 사용중이란 에러 메세지 출력
    checkError.value = disableSubmit.value ? '이미 사용중인 ID입니다.' : '사용가능한 ID입니다.';
};

// username 입력 핸들러
const changeUsername = () => {
    disableSubmit.value = true; //id 입력 중엔 항상 제출 버튼을 비활성화
    if (member.username) {
        checkError.value = 'ID 중복 체크를 하셔야 합니다.';
    } else {
        checkError.value = '';
    }
};
const join = async () => {
    if (member.password != member.password2) {
        // 비밀번화와 비밀번화 확인이 일치하지 않는 겨웅 경고
        return alert('비밀번호가 일치하지 않습니다.');
    }
    if (avatar.value.files.length > 0) {
        // 아바타 파일이 업로드된 경우 member 객테에 추가
        member.avatar = avatar.value.files[0];
    }
    try {
        await authApi.create(member); // 회원가입
        router.push({ name: 'home' }); // 회원 가입 성공 시, 첫 페이지로 이동 또는 로그인 페이지로 이동
    } catch (e) {
        console.error(e);
    }
};
</script>
<template>
    <div class="mt-5 mx-auto" style="width: 500px">
        <h1 class="my-5">
            <i class="fa-solid fa-user-plus"></i>
            회원 가입 페이지
        </h1>
        <!-- form을 제출 할 경우 기본 동작인 서버 전송은 하지 않고 join메소드만 부르겟다 -->
        <form @submit.prevent="join">
            <div class="mb-3 mt-3">
                <label for="username" class="form-label">
                    <i class="fa-solid fa-user"></i>
                    사용자 ID :
                    <button type="button" class="btn btn-success btn-sm py-0 me-2" @click="checkUsername">ID 중복 확인</button>
                    <!-- 제출 버튼의 활성화 여부에 따라 글씨 스타일을 다르게 적용 -->
                    <span :class="disableSubmit.value ? 'text-primary' : 'text-danger'">{{ checkError }}</span>
                </label>
                <!-- v-model : 양 방향 바인딩 , v-bind 는 script에서 읽어오기만 가능  -->
                <input type="text" class="form-control" placeholder="사용자 ID" id="username" @input="changeUsername" v-model="member.username" />
            </div>
        </form>
    </div>
    <div>
        <label for="avatar" class="form-label">
            <i class="fa-solid fa-user-astronaut"></i>
            아바타 이미지:
        </label>
        <input type="file" class="form-control" ref="avatar" id="avatar" accept="image/png, image/jpeg" />
    </div>
    <div class="mb-3 mt-3">
        <label for="email" class="form-label">
            <i class="fa-solid fa-envelope"></i>
            email
        </label>
        <input type="email" class="form-control" placeholder="Email" id="email" v-model="member.email" />
    </div>
    <div class="mb-3">
        <label for="password" class="form-label"> <i class="fa-solid fa-lock"></i> 비밀번호: </label>
        <input type="password" class="form-control" placeholder="비밀번호" id="password" v-model="member.password" />
    </div>
    <div class="mb-3">
        <label for="password" class="form-label"> <i class="fa-solid fa-lock"></i> 비밀번호 확인: </label>
        <input type="password" class="form-control" placeholder="비밀번호 확인" id="password2" v-model="member.password2" />
    </div>
    <!-- disableSubmit의 값에 따라 버튼 할성화 여부가 변한다 -->
    <button type="submit" class="btn btn-primary mt-4" :disabled="disableSubmit">
        <i class="fa-solid fa-user-plus"></i>
        확인
    </button>
</template>
