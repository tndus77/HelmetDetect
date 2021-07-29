# TADAK
'TADAK'은 공유 킥보드 사용자들의 헬멧 착용을 위해 만들어진 앱이다. 사용자들이 킥보드를 대여하기 전 셀카를 업로드하면, TADAK application에 내재된 기계 학습 모델이 사용자들의 헬멧 착용 여부를 자동으로 판단해준다.

## Team information
##### Members : 박수연, 임가은, 이권은

<p align="center">
<img src="https://user-images.githubusercontent.com/59522019/127528148-2a4fed46-ece2-413c-8293-e05abbe5ef50.jpg" width="200" height="400"> <img src="https://user-images.githubusercontent.com/59522019/127528153-a6fc8b4f-8d3c-4236-9b46-84b4f71396b1.jpg" width="200" height="400" >
</p>
<p align="center">
<img src="https://user-images.githubusercontent.com/59522019/127528185-7a61ca97-d65d-4fb3-8b60-d5b33de5b72a.jpg" width="200" height="400" > <img src="https://user-images.githubusercontent.com/59522019/127528158-871f165c-fd16-4431-832d-58b56f9aa7af.jpg" width="200" height="400" > <img src="https://user-images.githubusercontent.com/59522019/127528179-48009a6d-1fea-4fcd-9035-2566a1dfc175.jpg" width="200" height="400">
</p>

## 사용 방법
1. 회원가입과 로그인을 완료한다
2. 지도에서 이용할 공유킥보드를 선택한다
3. 대여하기 버튼을 누르면 셀프 캠 화면이 켜진다
4. 헬멧을 착용한 후, 가이드라인에 맞추어 사진을 찍는다
5. TADAK에게 헬멧 착용 여부를 확인받고 킥보드를 대여한다

## 모델 학습
헬멧을 쓴 사람과 쓰지 않은 사람을 구분하도록 만드는 것이 학습의 목적이었습니다.
학습을 위해 헬멧 쓴 사람의 사진이 여럿 필요했지만 적합한 데이터를 충분히 찾을 수 없었습니다. 그때 마스크를 일반 인물 사진에 합성해서 데이터셋을 만든 예시(https://github.com/kairess/mask-detection)를 발견하여 응용해보기로 하였습니다.
헬멧만 있는 사진 여섯장을 일반 인물 사진에 합성하는 방식으로 약 7800장(with_mask 3600장, without_mask 4200장) 의 학습 데이터를 만들었습니다.
구글 코랩에서 pytorch를 이용하여 pth모델을 만들었으나, pth를 안드로이드 앱에서 활용하는 것이 어려웠습니다. (pth->onnx->tflite 변환을 시도했으나 실패) 그래서 tensorflow로 다시 학습을 하여 .h5 형식의 모델을 만든 후, .tflite형식의 모델로 변환하였습니다.
모델을 res폴더에 추가하여 인식에 활용하였습니다.

## Environment
#### 개발 환경 : Android Studio 11

## Download our game!

