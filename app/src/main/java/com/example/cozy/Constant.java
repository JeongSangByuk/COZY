package com.example.cozy;

public final class Constant {
    public final static String MAIN_COLOR = "#5776ff";
    public final static String CORONA_INFORMATION_URL = "http://ec2-13-209-74-229.ap-northeast-2.compute.amazonaws.com:3000/coronaInfo";
    public final static String CORONA_MOVING_LINE_URL = "http://ec2-13-209-74-229.ap-northeast-2.compute.amazonaws.com:3000/search";


    public final static String ADRRESS_DATABASE_TABLE_NAME = "userAdrress";
    public final static String REGISER_MY_ADRRESS = "내 주소 추가하기";

    public final static String[] NUMBER_KOKEAN_1 = {"첫", "두", "세", "네", "다섯", "여섯", "일곱", "여덟", "아웃", "열"};
    public final static String[] NUMBER_KOKEAN_2 = {"한", "두", "세", "네", "다섯", "여섯", "일곱", "여덟", "아웃", "열"};
    public final static String[] NUMBER_KOKEAN_3 = {"열 ", "스물 ", "서른 ", "마흔 ", "쉰 ", "예순 ", "일흔 ", "여든 ", "아흔 ", "백"};

    //중복클릭시간차이
    public static final long MINIMUM_CLICK_INTERVAL = 1000;

    public static final int SWIPE_MIN_DISTANCE = 120;
    public static final int SWIPE_MAX_OFF_PATH = 250;
    public static final int SWIPE_THRESHOLD_VELOCITY = 200;


    //--------------------------------주소----------------------------------------

    public final static String[] defaultAdrress = {"시/군/구"};

    public final static String[] adrressInKorea = {"시/도", "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도",
            "강원도", "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치시"};

    public final static String[] adrressInSeoul = {"시/군/구", "종로구", "용산구", "성동구", "광진구", "동대문구", "중랑구", "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구",
            "마포구", "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구"};

    public final static String[] adrressInBusan = {"시/군/구", "중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "강서구", "해운대구", "사하구", "금정구", "연제구", "수영구", "사상구"};

    public final static String[] adrressInDaeGu = {"시/군/구", "중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"};

    public final static String[] adrressInIncheon = {"시/군/구", "중구", "동구", "미추홀구", "연수구", "남동구", "부평구", "계양구", "서구", "강화군", "옹진군"};

    public final static String[] adrressInGwangju = {"시/군/구", "동구", "서구", "남구", "북구", "광산구"};

    public final static String[] adrressInDaejeon = {"시/군/구", "동구", "중구", "서구", "유성구", "대덕구"};

    public final static String[] adrressInUlsan = {"시/군/구", "중구", "남구", "동구", "북구", "울주군"};

    public final static String[] adrressInSejong = {"동/면/읍", "조치원읍", "연기면", "연동면", "부강면", "금남면", "장군면", "연서면", "전의면", "전동면", "소정면",
            "한솔동", "새롬동", "도담동", "아름동", "종촌동", "고운동", "보람동", "대평동", "소담동"};

    public final static String[] adrressInGyeonggi = {"시/군/구", "수원시", "성남시", "안양시", "안산시", "용인시", "부천시", "광명시", "평택시", "과천시", "오산시",
            "시흥시", "군포시", "의왕시", "하남시", "이천시", "안성시", "김포시", "화성시", "광주시", "여주시", "양평군", "고양시", "의정부시", "동두천시",
            "구리시", "남양주시", "파주시", "양주시", "포천시", "연천군", "가평군"};

    public final static String[] adrressInGangwon = {"시/군/구", "춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군",
            "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"};

    public final static String[] adrressInChungbuk = {"시/군/구", "청주시", "충주시", "제천시", "보은군", "옥천군", "영동군", "증평군", "진천군", "괴산군",
            "음성군", "단양군"};

    public final static String[] adrressInChungnam = {"시/군/구", "천안시", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시", "당진시", "금산군",
            "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군"};

    public final static String[] adrressInJeonbuk = {"시/군/구", "전주시", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군", "임실군",
            "순창군", "고창군", "부안군"};

    public final static String[] adrressInJeonnam = {"시/군/구", "목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군", "화순군",
            "장흥군", "강진군", "해남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"};

    public final static String[] adrressInGyeongbuk = {"시/군/구", "포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군",
            "의성군", "청송군", "영양군", "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"};

    public final static String[] adrressInGyeongnam = {"시/군/구", "창원시", "진주시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군", "창녕군",
            "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군"};

    public final static String[] adrressInJeJu = {"시/군/구", "제주시", "서귀포시"};


}
