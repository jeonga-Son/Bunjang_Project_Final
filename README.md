# jelly
## 2023-03-04 진행상황

* 노션 기획서 작성
* ERD 설계(수정중)
* dev/prod 서브도메인 구축중

## 2023-03-05 진행상황
* ERD 설계 완료
* dev/prod 서브도메인 구축 완료
* API 리스트업

---
# Rich
## 2023-03-04 진행상황

* 노션 기획서 작성
* ERD 설계(수정중)
* EC2 인스턴스 생성
* RDS 인스턴스 생성

## 2023-03-05 진행상황
* ERD 설계 완료
* dev/prod 서브도메인 구축 완료
* API 리스트업

## 2023-03-06 진행상황
* DB Dummy Data 입력 완료
* ERD 보수
* 금일 1차 피드백 진행 from 핀

* 피드백 내용  
✅ 1. 상점에서 평점 빼기 : 평점은 그때그때 리뷰테이블에서 계산해서 가져오는게 더 나음  
✅ 2. 찜 상태값 추가 필요 : 데이터에 대한 status 말고 찜 상태에 대한 likeStatus도 필요 → followStatus, favoriteStatus 추가함  
✅ 3. 주소는 위도 경도까지 저장 필요  
✅ 4. 채팅방에 누구있는지 멤버테이블 따로 빼서 저장  
✅ 5. API개발이 느린 편 : 생산성 향상 필요  
✅ 6. shop 테이블 삭제됐으니까 uri도 shops를 users로 수정 필요  
✅ 7. reviews/detail 삭제 : restful 하지않음  
✅ 8. shops/info 삭제 : restful 하지않음  
   9. 채팅방 api 필요  
✅ 10. Like 따로 follow 따로 → ERD에는 찜이 favorite으로 되어있는데 api 명세서에는 like로 되어있어서 favorite으로 변경  
✅ 11. Api 명세서에 누가 개발했는지 내용 추가  


