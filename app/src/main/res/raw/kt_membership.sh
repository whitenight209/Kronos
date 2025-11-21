#!/system/bin/sh

# wake up
input keyevent 224
sleep 1
#
input keyevent 82
sleep 1
# 앱 실행
monkey -p com.olleh.android.oc2 -c android.intent.category.LAUNCHER 1
sleep 10
# 이벤트 닫기
#screenshot
input tap 920 2082
sleep 2
# 더보기 진입
#screenshot
input tap 920 2060
sleep 2
# back
input tap 850 2260
sleep 2
# 출석 체크 화면 진입
input tap 365 1685
sleep 3
# 출석 체크
input tap 530 1770
sleep 2
# 앱 종료
am force-stop com.olleh.android.oc2
# 홈으로
input keyevent 3
# 화면 끄기
input keyevent 223