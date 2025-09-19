package com.soomsoom.backend.application.port.out.adrewardlog

/**
 * Google AdMob 서버 측 검증(SSV) 콜백의 유효성을 검증하기 위한 포트
 * 이 인터페이스의 구현체는 외부 암호화 라이브러리를 사용하는 Crypto Adapter
 */
interface AdMobVerificationPort {
    /**
     * AdMob에서 받은 전체 콜백 URL의 서명을 암호학적으로 검증
     * 이 요청이 정말 Google로부터 온 유효한 요청인지를 확인
     *
     * @param fullCallbackUrl 쿼리 파라미터를 포함한 전체 콜백 URL
     * @return 검증 성공 시 true, 실패 시 false
     */
    fun verify(fullCallbackUrl: String): Boolean
}
