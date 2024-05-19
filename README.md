# RunTogether
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

## Overview
html/css/javascript 와 Java SpringBoot 이용해 만든 런닝 웹페이지입니다.  
html/css/javascript 로 한정적인 기능만 구현되어 있던 기존의 프론트엔드 프로젝트를 백엔드까지 확장시켜 완성도를 높였습니다.  
기존 프로젝트에 대한 설명과 개발 과정은 아래 링크에 상세히 기재되어 있습니다.    

This is a running web page created using HTML/CSS/JavaScript and Java Spring Boot.  
I extended the original frontend project, which was limited to functionalities implemented with HTML/CSS/JavaScript, to the backend, enhancing its overall completeness.  
Detailed explanations of the original project and the development process are provided in the link below.  
  
[GitHub](https://github.com/pishonn/Web-Programing-Team-10)  
[Notion](https://tar-beast-134.notion.site/a69a754afce0477baeaca68326450183)  
<details>
  <summary>Existing Features</summary>
  
  ### 사용자 위치 표시 (Displaying User Location)
  - Google Maps API를 사용하여 사용자 위치와 지도 정보를 받아옵니다.
  - Fetch user location and map data using Google Maps API.

  ### 주변 장소 안내 (Nearby Places Guidance)
  - Google Maps API가 제공하는 Places API의 함수들을 이용해서 사용자 위치 주변의 장소들을 가까운 순으로 배열하고 안내합니다.
  - Display nearby places sorted by proximity using functions provided by Google Maps API's Places API.

  ### 사용자 설정 (User Settings)
  - 사용자는 자신이 원하는 반경, 원하는 장소 등을 사용자 설정을 통해 선택할 수 있고 설정한 옵션에 따라 자동으로 근처의 목적지를 배정받게 됩니다.
  - Users can select desired radius and types of places through user settings, and receive nearby destinations based on the selected options.

  ### 수동 모드 (Manual Mode)
  - 수동 모드를 선택하게 되면, 자신이 원하는 목적지를 직접 입력할 수 있습니다.
  - Users can enter desired destinations manually in manual mode.

  ### 달리기 모드 (Running Mode)
  - 시작 페이지로 들어가 원하는 목적지에서 버튼을 누르면 네이버 스키마 링크를 통해 도보 안내 페이지가 열리고 시간 제한이 시작됩니다.
  - Enter the start page, select the desired destination, and click the start button to open the walking guidance page via Naver Schema Link and start the countdown timer.

  ### 도착 검증 (Arrival Verification)
  - 목적지에 이동 후, 도착 버튼을 누르면 현재 사용자의 위치와 목적지 사이의 직선 거리를 계산하여 도착 여부를 검증합니다.
  - After moving to the destination, click the arrival button to calculate the straight-line distance between the user's current location and the destination to verify arrival.

  ### 점수 부여 (Scoring)
  - 사용자가 달린 거리, 남은 시간에 따라 가산점이 부여됩니다.
  - Bonus points are awarded based on the distance run and the remaining time.

  ### 기록 페이지 (Record Page)
  - 기록 페이지에서 날짜에 따라 이동한 장소들과 점수 내역 등을 확인할 수 있습니다.
  - View traveled places and score history by date on the record page.
  
</details>  
## Updates

- **UI 디자인 강화 (Enhanced UI Design)**
  - 프론트엔드 디자인이 개선되었습니다.
  - Improved frontend design for a better user experience.
    
- **메뉴 항목 추가 (Menu Items Expansion)**
  - 시작, 설정, 기록 세 가지로 단조롭게 구성되어 있었던 메뉴를 달리기, 설정, 기록, 랭킹, 프로필, 게시판, 크루, 로그아웃 등으로 확장했습니다.
  - Expanded the previously simple menu, which consisted of only Start, Settings, and History, to include Running, Settings, History, Rankings, Profile, Board, Crew, and Logout.

- **DB 구현 (Database Implementation)**
  - MariaDB를 사용하여 데이터베이스를 구현했습니다.
  - Implemented a database using MariaDB to store user data and crew data.

- **사용자 프로필 사진 선택 및 정보 수정 기능 (Profile Picture Selection and Information Update)**
  - 사용자가 자신의 프로필 사진을 선택하고 정보를 수정할 수 있습니다.
  - Users can choose their profile pictures and update their information.

- **프로필 페이지 (Profile Page)**
  - 게시판이나 랭킹 페이지에서 사용자들이 서로의 프로필 사진을 눌러 프로필을 확인할 수 있습니다.
  - Users can view each other's profiles by clicking on profile pictures on the board or rankings page.

- **같은 지역인들끼리 모여 같이 런닝을 즐길 수 있도록 크루 기능 추가 (Crew Feature)**
  - 지역 기반 크루를 생성하고 참여할 수 있습니다.
  - Added a crew feature to enjoy running with people from the same area.

- **크루 관리용 관리자 페이지 추가 (Admin Page for Crew Management)**
  - 크루 관리자가 가입 신청을 관리하고 회원을 조정할 수 있는 관리자 페이지가 추가되었습니다.
  - Added an admin page for crew creators to manage membership requests, remove members, and adjust crew information.

- **크루 프로필 페이지 (Crew Profile Page)**
  - 각 크루의 프로필 페이지가 추가되었습니다.
  - Added profile pages for each crew.

- **크루 프로필 사진 선택 및 정보 수정 기능 (Crew Profile Picture Selection and Information Update)**
  - 크루 프로필 사진을 선택하고 정보를 수정할 수 있습니다.
  - Crew profile pictures can be selected and information can be updated.

- **자유게시판과 크루 모집 게시판 추가 (Free Board and Crew Recruitment Board)**
  - 자유롭게 글을 쓸 수 있는 게시판과 크루 모집을 위한 게시판이 추가되었습니다.
  - Added a free board and a crew recruitment board for open discussions and crew recruitment.

- **게시판 검색 (Board Search)**
  - 게시판에서 원하는 글을 검색할 수 있습니다.
  - Added a search function for the board.

- **크루 검색 (Crew Search)**
  - 원하는 크루를 검색할 수 있습니다.
  - Added a search function for crews.

- **이동 거리와 점수에 따른 유저 랭킹과 크루 랭킹 조회 (User and Crew Rankings)**
  - 유저와 크루의 이동 거리와 점수를 기반으로 한 랭킹 시스템이 추가되었습니다.
  - Added user and crew rankings based on distance traveled and points.

- **크루원들끼리 실시간 채팅 가능 (Real-time Chat)**
  - 크루 멤버들끼리 실시간 채팅을 할 수 있습니다.
  - Real-time chat is available among crew members.

- **경주 기능 (Race)**
  - 달리기 페이지에서 다른 유저와 경주를 할 수 있는 기능이 추가되었습니다.
  - Added a race feature to compete with other users on the running page.

- **경주를 통한 가산점 부여 (Bonus Points for Races)**
  - 경주 결과에 따라 승자와 패자 모두에게 보너스 점수가 부여됩니다.
  - Adds fun to the exercise by racing, with bonus points for both winners and losers.

- **AI 크루원 추가 (AI Crew Member)**
  - ChatGPT API를 사용하여 대화의 재미를 더하는 AI 크루원이 추가되었습니다.
  - Added an AI crew member using the ChatGPT API to enhance the chat room experience.





