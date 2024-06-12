let map, infoWindow, service, geocoder;
let userLocation = null; 
let username = null;
let idx = 0;
let resultLen;
let totalSeconds;
let originalSec;
let timerInterval = null;
let getScore = false;
let userKeyword = null;
let minDistance = 0.2;
let searchRadius = 1000;


function calculateWalkTime(Km) {
    const avg = 83; 
    const meters = Km * 1000; 
    const estimatedTime = meters / avg;
    
    return Math.round(estimatedTime*1.5);
}

function calculateDistance(lat1, lon1, lat2, lon2) {
    let R = 6371; 
    let dLat = toRadians(lat2 - lat1);
    let dLon = toRadians(lon2 - lon1);
    let a = 
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) * 
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
    let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return Math.round(R * c * 100) / 100; 
}

function toRadians(degrees) {
    return degrees * Math.PI / 180;
}


function initMap() {

    if (!userData) {
        console.error("사용자 정보가 세션에 없습니다.");
        return;
    }

    console.log("사용자 :", userData);

    //userData = JSON.parse(localStorage.getItem(username));
    let mode = userData.selectedMode || 'auto'
    let selectedPlaces = userData.selectedPlaces || ['park'];
    searchRadius = userData.searchRadius || 1000;
    minDistance = userData.minDistance || 0.2;
    
    if (mode == 'manual') {

        minDistance = 0.2;
        searchRadius = 50000;

        if (userKeyword === null) {
            userKeyword = prompt("검색 키워드를 입력해주세요.");
        }

        if (userKeyword === null) {
            alert("검색이 취소되었습니다.");
            return window.location.href = '/mainMenu';
        }
    }

    clearInterval(timerInterval);
    timerInterval = null;
    $limitTime = document.getElementById('limitTime');
    $limitTime.innerHTML = ``;
    
    
    
    console.log("모드 :", mode);
    console.log("장소 타입 :", selectedPlaces);
    console.log("검색 반경 :", searchRadius, "m");
    console.log("최소 거리 :", minDistance, "km");

    map = new google.maps.Map(document.getElementById('map'), {zoom: 15});
    infoWindow = new google.maps.InfoWindow;
    service = new google.maps.places.PlacesService(map);
    geocoder = new google.maps.Geocoder();

    
    if (!userLocation) { 
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    userLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                    proceedWithLocation(userLocation, searchRadius, selectedPlaces, idx, mode);
                }, 
                function() {
                    handleError(true, infoWindow, map.getCenter());
                }
            );
        } else {
            handleError(false, infoWindow, map.getCenter());
        }
    } else {
        proceedWithLocation(userLocation, searchRadius, selectedPlaces, idx, mode); 
    }
}

function proceedWithLocation(location, searchRadius, selectedPlaces, idx, mode) {
    map.setCenter(location);
    infoWindow.setPosition(location);
    infoWindow.setContent('Your location');
    infoWindow.open(map);

    let combinedResults = [];
    let searchParams = {
        location: location,
        radius: searchRadius
    };

    

    for (let i = 0; i < selectedPlaces.length; i++) {
        if (mode == 'manual'){
            searchParams.keyword = userKeyword;
        } else {
            searchParams.type = selectedPlaces[i];
        }
        service.nearbySearch(searchParams, function(results, status) {
            if (status === google.maps.places.PlacesServiceStatus.OK) {
                combinedResults = combinedResults.concat(results);
            } else {
                console.error(`${selectedPlaces[i]} : ${status}`);
                if (status == 'ZERO_RESULTS'){
                    alert('검색된 장소가 없네요. 최대 검색 반경은 50000m 입니다!');
                    return showMainMenu();
                }
            }

            
            if (i === selectedPlaces.length - 1) {
                processResults(combinedResults, status, idx, location);
            }
        });
    }

}


function processResults(results, status, idx2, userLocation) {
    
    
    if (status !== google.maps.places.PlacesServiceStatus.OK) {
        console.error(status);
        return;
    }

    results.forEach(function(place) {
        place.distance = calculateDistance(
            userLocation.lat(), 
            userLocation.lng(), 
            place.geometry.location.lat(), 
            place.geometry.location.lng()
        );
    });

    results = results.filter(function(place) {
        return (place.distance >= minDistance && place.distance < 50);
    });
    results.sort(function(a, b) {
        return a.distance - b.distance;
    });

    if (results.length==0){
        alert('검색된 장소가 없네요. 검색 반경(최대 50000m) 또는 최소 거리를 수정하고 다시 시도해주세요!');
        return showMainMenu();
    }

    const $resultsDiv = document.getElementById('results');
    $resultsDiv.innerHTML = '';

    resultLen = results.length
    const firstPlace = results[idx2];
    console.log("firstPlace :", firstPlace);
    if (firstPlace) {
        const lat = firstPlace.geometry.location.lat();
        const lng = firstPlace.geometry.location.lng();
        const name = firstPlace.name;

        let dist = calculateDistance(userLocation.lat(), userLocation.lng(), lat, lng)
        const $locName = document.getElementById('locName')
        
        const placeElement = document.createElement('div');
        placeElement.innerHTML = `<button id="button" onclick="naverMap('${name}', ${lat}, ${lng}, ${dist})">시작하기!</button><br>`;
        $resultsDiv.appendChild(placeElement);

        $locName.innerHTML = `<div>목적지는 "${name}" 입니다!</div><div>직선거리 : ${dist}km</div>`
    }

}

function convertToGooglePlace(data) {
    return {
        business_status: data.business_status,
        geometry: {
            location: new google.maps.LatLng(data.geometry.location.lat, data.geometry.location.lng),
            viewport: {
                south: data.geometry.viewport.south,
                west: data.geometry.viewport.west,
                north: data.geometry.viewport.north,
                east: data.geometry.viewport.east
            }
        },
        icon: data.icon,
        icon_background_color: data.icon_background_color,
        icon_mask_base_uri: data.icon_mask_base_uri,
        name: data.name,
        place_id: data.place_id,
        plus_code: data.plus_code,
        rating: data.rating,
        reference: data.reference,
        scope: data.scope,
        types: data.types,
        user_ratings_total: data.user_ratings_total,
        vicinity: data.vicinity,
        photos: data.photos,
        distance: data.distance
    };
}


function processResults2(firstPlace) {

    map = new google.maps.Map(document.getElementById('map'), {zoom: 15});
    infoWindow = new google.maps.InfoWindow;
    service = new google.maps.places.PlacesService(map);
    geocoder = new google.maps.Geocoder();

    if (!userLocation) {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    userLocation = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
                    // 이제 userLocation이 설정되었으므로 여기서 firstPlace를 처리
                    handleFirstPlace(firstPlace);
                }, 
                function() {
                    handleError(true, infoWindow, map.getCenter());
                }
            );
        } else {
            handleError(false, infoWindow, map.getCenter());
        }
    } else {
        // userLocation이 이미 설정되어 있으면 바로 처리
        handleFirstPlace(firstPlace, userLocation);
    }
}

function handleFirstPlace(firstPlace) {
    console.log("firstPlace :", firstPlace);
    const $resultsDiv = document.getElementById('results');
    $resultsDiv.innerHTML = '';
    if (firstPlace) {
        const lat = firstPlace.geometry.location.lat();
        const lng = firstPlace.geometry.location.lng();
        const name = firstPlace.name;

        let dist = calculateDistance(userLocation.lat(), userLocation.lng(), lat, lng);
        const $locName = document.getElementById('locName');
        
        const placeElement = document.createElement('div');
        placeElement.innerHTML = `<button id="button" onclick="naverMap('${name}', ${lat}, ${lng}, ${dist})">시작하기!</button><br>`;
        $resultsDiv.appendChild(placeElement);

        $locName.innerHTML = `<div>이번 레이스의 목적지는</div><div>"${name}" 입니다!</div><div>직선거리 : ${dist}km</div>`;

        const marker = new google.maps.Marker({
            position: { lat: lat, lng: lng },
            map: map,
            title: name
        });

        map.setCenter(userLocation);
        infoWindow.setPosition(userLocation);
        infoWindow.setContent('Your location');
        infoWindow.open(map);

    }
}



function naverMap(endName, elat, elng, km) {
    if (room) {
        if (userData.id === room.admin.id) {
            if (room.participantsReady.length === room.participants.length - 1) {
                // 모든 참가자가 준비되었을 때 실행되는 코드
                room.participants.forEach(participant => {
                    // 모든 참가자들에게 else 부분의 코드 실행을 위한 메시지 전송
                    sendStartRaceMessage(participant.id, endName, elat, elng, km);
                    startRaceHandler();
                });
            } else {
                alert("모든 참가자가 준비되지 않았습니다.");
            }
        } else {
            alert("방의 리더만 시작할 수 있습니다.");
        }
    } else {
        geocoder.geocode({'location': userLocation}, function(results, status) {
            if (status === google.maps.GeocoderStatus.OK && results[0]) {
                const startPlaceName = results[0].formatted_address;
                const url = createNaverDirectionsUrl(startPlaceName, endName, elng, elat);

                window.open(url, '_blank');
                
                const $limitTime = document.getElementById('limitTime');
                const totalMinutes = calculateWalkTime(km); 
                totalSeconds = totalMinutes * 60; 
                originalSec = totalSeconds;
                timerInterval = setInterval(function() {
                    updateTimer($limitTime, elat, elng, km, startPlaceName, endName);
                }, 1000);

                const $resultsDiv = document.getElementById('results');
                $resultsDiv.innerHTML = '';
                const placeElement = document.createElement('div');
                placeElement.innerHTML = `<button id="button" onclick="verify(${elat}, ${elng}, ${km}, '${startPlaceName}', '${endName}')">도착 확인!</button><br>`;
                $resultsDiv.appendChild(placeElement);

                getScore = false;
            } else {
                console.error(status);
            }
        });
    }
}


function updateTimer($limitTime, elat, elng, km, sName, eName) {
    
    let minutes = Math.floor(totalSeconds / 60);
    let seconds = totalSeconds % 60;


    $limitTime.innerHTML = `<div>제한 시간 : ${minutes} : ${seconds}</div>`;

    if (totalSeconds > 0) {
        totalSeconds--;
    } else {
        clearInterval(timerInterval);
        $limitTime.innerHTML = `<div>시간 종료!</div>`;
        verify(elat, elng, km, sName, eName);
        

      
    }
}


function createNaverDirectionsUrl(startPlaceName, endName, elat, elng) {
    const isMobile = /Mobi|Android/i.test(navigator.userAgent);
    const startLng = userLocation.lng()
    const startLat = userLocation.lat()

    if (isMobile) {
        return `nmap://route/walk?slat=${startLat}&slng=${startLng}&sname=${startPlaceName}&dlat=${elng}&dlng=${elat}&dname=${endName}`;
    } else {
        return `https://map.naver.com/p/directions/${startLng},${startLat},${startPlaceName},PLACE_POI,PLACE_POI/${elat},${elng},${endName},PLACE_POI,PLACE_POI/-/walk?c=15.00,0,0,0,dh`;
    }
}

function handleError(status, infoWindow, pos) {
    infoWindow.setPosition(pos);
    infoWindow.setContent(status ?'Error : 정보를 불러오는데에 실패했습니다.' : 'Error : 이 브라우저는 Geolocation API를 지원하지 않습니다.');
    infoWindow.open(map);
}

function verify(elat, elng, km, sName, eName) {

    if (getScore){
        alert('이미 점수를 획득하셨습니다.');
        return;
    } 

    let additional = 0;
    let additional2 = 0;
    let points = 10;

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                let newLat = position.coords.latitude;
                let newLng = position.coords.longitude;
                let dist = calculateDistance(newLat, newLng, elat, elng)
                
                if (totalSeconds > 0){
                    additional = (totalSeconds/originalSec);
                }
                
                
                if (parseInt(km / 5) > 0){
                    additional2 = parseInt(km / 5);
                }

                points = Math.ceil(points *(1+additional)) + 10*additional2;
                
                
                if (dist < 100) {

                    if (room) {
                        addScore3(type='경주 점수', additional, additional2, points=points, totalSeconds, sName, eName, km);
                    } else {
                        addScore(type='도착 점수', additional, additional2, points=points, totalSeconds, sName, eName, km);
                    }
                } else if (totalSeconds > 0) {
                    alert('아직 도착하지 않으셨네요! 목적지의 200m 반경 안으로 이동해주세요.');
                }
                // } else {
                //     if ((km-dist)/km > 0.5){

                //         if (room) {
                //             addScore4(type='경주 점수', sName, eName);
                //         } else {
                //             addscore2(type='노력 점수', sName, eName);
                //         }
                //     }
                // }
                
            }, 
            function() {
                handleError(true, infoWindow, map.getCenter());
            }
        );
    } else {
        handleError(false, infoWindow, map.getCenter());
    }


}
function addScore(type, additional, additional2, points, diff, sName, eName, km) {

    console.log("km : " + km);
    const now = new Date();
    let msg = '';

    if (additional > 0) {
        msg += `\n${diff}초 빨리 도착하셨네요!`;
    }
    if (additional2 > 0) {
        msg += msg ? `\n게다가 5km 이상의 먼 거리를 이동하셨네요!` : `\n5km 이상의 먼 거리를 이동하셨네요!`;
    }
    if (msg) {
        msg += `\n기본 점수에 10점에 가산점 ${points - 10}점을 추가해드릴게요!`;
    }

    const scoreData = {
        roomId: null,
        type: type,
        points: points,
        sName: sName,
        eName: eName,
        distance: Number(km),
        time: `${now.getFullYear()}년 ${now.getMonth() + 1}월 ${now.getDate()}일 ${now.getHours()}시 ${now.getMinutes()}분`
    };
    console.log(scoreData);
    fetch('/addScore', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(scoreData)
    })
    .then(response => response.json())
    .then(totalPoints => {
        document.getElementById('userPoints').innerText = totalPoints;
        alert(`${type} ${points}점을 획득하였습니다!${msg}`);
    })
    .catch(error => console.error('Error:', error));

    clearInterval(timerInterval);
    timerInterval = null;
    const $limitTime = document.getElementById('limitTime');
    $limitTime.innerHTML = `<div>도착 완료!</div>`;

    getScore = true;
}


function addscore2(type, sName, eName) {
    const now = new Date();

    let scoreData = {
        type: type,
        points: 5,
        sName: sName,
        eName: eName,
        distance: 0,
        time: `${now.getFullYear()}년 ${now.getMonth() + 1}월 ${now.getDate()}일 ${now.getHours()}시 ${now.getMinutes()}분`
    };

    fetch('/addScore', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(scoreData)
    })
    .then(response => response.json())
    .then(totalPoints => {
        document.getElementById('userPoints').innerText = totalPoints;
        alert(`도착하지 못하셨군요! 하지만 절반 이상 이동하셨기 때문에 점수를 드릴게요!\n${type} 5점을 획득하였습니다!`);
    })
    .catch(error => console.error('Error:', error));

    getScore = true;
}

function addScore3(type, additional, additional2, points, diff, sName, eName, km) {

    console.log("km : " + km);
    const now = new Date();
    let msg = '';

    if (additional > 0) {
        msg += `\n${diff}초 빨리 도착하셨네요!`;
    }
    if (additional2 > 0) {
        msg += msg ? `\n게다가 5km 이상의 먼 거리를 이동하셨네요!` : `\n5km 이상의 먼 거리를 이동하셨네요!`;
    }
    if (msg) {
        msg += `\n기본 점수에 10점에 가산점 ${points - 10}점을 추가해드릴게요!`;
    }

    console.log("room :", room)
    const scoreData = {
        type: type,
        points: points,
        sName: sName,
        eName: eName,
        distance: Number(km),
        time: `${now.getFullYear()}년 ${now.getMonth() + 1}월 ${now.getDate()}일 ${now.getHours()}시 ${now.getMinutes()}분`,
        roomId: room.id
    };
    console.log(scoreData);
    fetch('/addScore', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(scoreData)
    })
    .then(response => response.json())
    .then(data => {
        const totalPoints = data.totalPoints;
        const rank = data.rank;
        const rankBonus = data.rankBonus;
        console.log('Success:', data);

        const finalPoints = points + rankBonus;

        document.getElementById('userPoints').innerText = totalPoints;
        alert(`축하합니다! ${rank}위로 도착했습니다!\n${msg}\n순위 보너스 ${rankBonus}점 추가로 ${type} 총 ${finalPoints}점을 획득하였습니다!`);
    })
    .catch(error => console.error('Error:', error));

    clearInterval(timerInterval);
    timerInterval = null;
    const $limitTime = document.getElementById('limitTime');
    $limitTime.innerHTML = `<div>도착 완료!</div>`;

    getScore = true;

    console.log("userData :", userData);
    // var url = `/raceResult/${crewId}/${userData.Id}`;
    // window.location.href = url;
}


function minusIndex() {
    
    if (idx == 0){
        alert("가장 가까운 장소입니다.");
    } else{
        idx -= 1
        initMap();
    }
}

function plusIndex() {
    
    if (resultLen == idx+1){
        alert("가장 먼 장소입니다.");
    } else{
        idx += 1
        initMap();
    }
}

function showSettings() {

    console.log("사용자 :", userData);

    if (!userData) {
        console.error("사용자 정보가 세션에 없습니다.");
        return;
    }

    
    var userId = userData.userId;
    console.log("사용자 ID :", userId);
    
    // 이 부분을 수정하여 사용자 정보를 직접 Thymeleaf를 통해 가져옴

    if (userData.selectedMode) {
        document.getElementById(userData.selectedMode + 'Mode').checked = true;
    }

    let selectedPlaces = userData.selectedPlaces || ['park'];
    document.getElementsByName('place').forEach(input => {
        input.checked = selectedPlaces.includes(input.value);
    });

    document.getElementById('searchRadius').value = userData.searchRadius || 1000;
    document.getElementById('minDistance').value = userData.minDistance || 0.2;
}


function applySetting() {
    var userData = {
        selectedMode: document.querySelector('input[name="mode"]:checked').value,
        selectedPlaces: Array.from(document.getElementsByName('place'))
                            .filter(input => input.checked)
                            .map(input => input.value),
        searchRadius: parseInt(document.getElementById('searchRadius').value),
        minDistance: parseInt(document.getElementById('minDistance').value)
    };
    console.log("사용자 설정 :", userData);
    // AJAX 요청을 통해 서버에 데이터 전송
    fetch('/updateSettings', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json;'
        },
        body: JSON.stringify(userData)
    })
    .then(response => response.json()) // 응답을 JSON으로 변환
    .then(data => {
        console.log('Success:', data);
        alert(data.message); // JSON 응답에서 메시지 추출
    })
    .catch(error => {
        console.error('Error:', error);
    });
    
}


function showScoreHistory() {
    console.log("sh :", sh);
    fillYearAndMonthSelectors();
    createCalendar(new Date().getFullYear(), new Date().getMonth());

    // document.getElementById('mainMenu').classList.remove('grid');
    // document.getElementById('mainMenu').classList.add('hidden');
    // document.getElementById('scoreHistory').classList.remove('hidden');
}


function displayUserDetails() {
    idx = 0;
    userLocation = null;
    console.log("totalPoints :", totalPoints);
    document.getElementById('userPoints').innerText = totalPoints;
    console.log("룸 :", room);
    if (room) {
        const placeData = JSON.parse(room.placeData);
        var firstPlace = convertToGooglePlace(placeData);
        console.log("firstPlace :", firstPlace);
        processResults2(firstPlace);
    } else {
        initMap();
    }
    

    clearInterval(timerInterval);
    timerInterval = null;
    const $limitTime = document.getElementById('limitTime');
    $limitTime.innerHTML = ``;
}

function updateScoreHistory(scoreHistory) {
    
    const $yearSelect = document.getElementById('yearSelect');
    const $monthSelect = document.getElementById('monthSelect');

    const selectedYear = parseInt($yearSelect.value);
    const selectedMonth = parseInt($monthSelect.value) + 1; 
    

    markCalendar(scoreHistory);
    const $scoreList = document.getElementById('scoreList');
    $scoreList.innerHTML = "";
    //console.log("변환 전 : " + scoreHistory);
    scoreHistory.forEach(record => {
        const dateTimePattern = /(\d+)년 (\d+)월/;
        const match = record.time.match(dateTimePattern);
        
        const recordYear = parseInt(match[1]);
        const recordMonth = parseInt(match[2]);

        let newTime = record.time.split("년 ")[1];
        if  (recordYear == selectedYear && recordMonth == selectedMonth) {

            let listItem = document.createElement('li');
            let summary = document.createElement('span');
            summary.textContent = `${newTime} ${record.type} +${record.points}점`;
            listItem.appendChild(summary);

            let toggleButton = document.createElement('button');
            toggleButton.textContent = '펼치기';
            toggleButton.onclick = function() {
                details.style.display = details.style.display === 'none' ? 'block' : 'none';
                this.textContent = details.style.display === 'none' ? '펼치기' : '접기';
            };
            listItem.appendChild(toggleButton);

            let details = document.createElement('div');
            details.style.display = 'none';
            details.innerHTML = `${record.sname} → ${record.ename}, <b>${record.distance}km 이동</b>`;
            listItem.appendChild(details);

            $scoreList.appendChild(listItem);
        }
        
    });
}



function markCalendar(scoreHistory) {

    const selectedYear = parseInt(document.getElementById('yearSelect').value);
    const selectedMonth = parseInt(document.getElementById('monthSelect').value) + 1;

    scoreHistory.forEach(record => {
        const dateTimePattern = /(\d+)년 (\d+)월 (\d+)일/;
        const match = record.time.match(dateTimePattern);

        if (match) {
            const year = parseInt(match[1]);
            const month = parseInt(match[2]);
            const day = parseInt(match[3]);

            
            if (year === selectedYear && month === selectedMonth) {
                let dayElement = document.querySelector(`#calendar-day-${day}`);
                if (dayElement) {
                    dayElement.classList.add('marked');
                }
            }
        }
    });
}



function createCalendar(year, month) {

    const days = ['일', '월', '화', '수', '목', '금', '토'];
    const calendarDays = document.getElementById('calendarDays');
    calendarDays.innerHTML = ''; 

    days.forEach(day => {
        let dayDiv = document.createElement('div');
        dayDiv.innerText = day;
        calendarDays.appendChild(dayDiv);
    });

    const $calendar = document.getElementById('calendar');
    $calendar.innerHTML = ''; 

    
    let firstDay = new Date(year, month, 1).getDay();
    let lastDate = new Date(year, month + 1, 0).getDate();

    
    for (let i = 0; i < firstDay; i++) {
        let emptyDiv = document.createElement('div');
        emptyDiv.classList.add('empty'); 

        $calendar.appendChild(emptyDiv);
    }

    
    for (let i = 1; i <= lastDate; i++) {
        let dayElement = document.createElement('div');
        dayElement.innerText = i;
        dayElement.id = `calendar-day-${i}`;
        $calendar.appendChild(dayElement);
    }

    console.log(sh);
    if (sh.length > 0){
        updateScoreHistory(sh);
    }
          
    
}

function fillYearAndMonthSelectors() {
    const $yearSelect = document.getElementById('yearSelect');
    const $monthSelect = document.getElementById('monthSelect');

    
    for (let i = 2020; i <= new Date().getFullYear(); i++) {
        let option = new Option(i, i);
        $yearSelect.options.add(option);
    }

    
    for (let i = 0; i < 12; i++) {
        let option = new Option(i + 1, i);
        $monthSelect.options.add(option);
    }

    
    $yearSelect.value = new Date().getFullYear();
    $monthSelect.value = new Date().getMonth();

    
    $yearSelect.addEventListener('change', () => {
        createCalendar($yearSelect.value, $monthSelect.value);
    });

    $monthSelect.addEventListener('change', () => {
        createCalendar($yearSelect.value, $monthSelect.value);
    });
}

function verifyForm() {
    var userName = document.forms["registerForm"]["name"].value;
    var userId = document.forms["registerForm"]["userId"].value;
    var userPw = document.forms["registerForm"]["pw"].value;
    var userEmail = document.forms["registerForm"]["email"].value;

    if (userName == "" || userId == "" || userPw == "" || userEmail == "") {
        alert("모든 항목을 입력해주세요.");
        return false;
    }
    return true;
}

function showMainMenu() {
    history.back();
}




