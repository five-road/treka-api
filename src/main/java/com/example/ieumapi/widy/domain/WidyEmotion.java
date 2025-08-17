package com.example.ieumapi.widy.domain;

public enum WidyEmotion {
    HAPPY,      // 0: 기쁨
    SAD,        // 1: 슬픔
    ANGRY,      // 2: 분노
    EXCITED,    // 3: 신남
    CALM,       // 4: 차분함
    NERVOUS,    // 5: 긴장
    BORED,      // 6: 지루함
    SURPRISED,  // 7: 놀람
    TIRED,      // 8: 피곤함
    PROUD       // 9: 뿌듯함
}
// ordinal() 값이 0부터 시작하며, 위 순서대로 0~9의 값을 가집니다.
// 예시: WidyEmotion.HAPPY.ordinal() == 0, WidyEmotion.SAD.ordinal() == 1, ...
