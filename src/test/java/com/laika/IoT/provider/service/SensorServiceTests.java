package com.laika.IoT.provider.service;

import com.laika.IoT.configuration.QuartzConfig;
import com.laika.IoT.core.type.SensorType;
import com.laika.IoT.entity.Home;
import com.laika.IoT.entity.IoTSensor;
import com.laika.IoT.entity.Manager;
import com.laika.IoT.repository.IoTSensorRepository;
import com.laika.IoT.repository.HomeRepository;
import com.laika.IoT.repository.ManagerRepository;
import com.laika.IoT.web.dto.RequestIoTSensor;
import com.laika.IoT.web.dto.RequestManger;
import com.laika.IoT.web.dto.ResponseIoTSensor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
public class SensorServiceTests {
    @Autowired
    private SensorService sensorService;
    @Autowired
    private HomeRepository homeRepository;
    @Autowired
    private IoTSensorRepository ioTSensorRepository;

    // 관련 메서드를 센서로 옮기고 지우기
    @Autowired
    private ManagerService managerService;
    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private QuartzConfig quartzConfig;

    @Test
    @DisplayName("미등록 센서 등록 테스트")
    @Transactional
    void registerUnregisteredSensorTest() {
        sensorService.registerUnregisteredSensor("1234", SensorType.HUMAN_DETECTION);
        sensorService.registerUnregisteredSensor("12345", SensorType.IN_OUT);
        sensorService.registerUnregisteredSensor("123456", SensorType.IN_OUT);


        Page<ResponseIoTSensor.UnregisteredSensor> list = sensorService.getUnregisteredSensorList(PageRequest.of(0,10));

        for (ResponseIoTSensor.UnregisteredSensor sensorItem : list) {
            System.out.println("결과:");
            System.out.println(sensorItem.getType() + sensorItem.getToken() + " " + sensorItem.getTimestamp());
        }
    }

    @Test
    @DisplayName("센서 등록 테스트")
    @Transactional
    void registerSensorTest() {
        //관리 대상자 생성
        Home home = new Home();
        home = homeRepository.save(home);

        sensorService.registerUnregisteredSensor("1234", SensorType.HUMAN_DETECTION);
        IoTSensor sensor = ioTSensorRepository.findByToken("1234");

        ResponseIoTSensor.Register responseIoTSensor = sensorService.register(home.getId(), sensor.getToken(), sensor.getType()).orElseGet(()->null);

        //검증
        assertNotNull(responseIoTSensor);
        assertNotNull(home.getId());
        System.out.println(responseIoTSensor.getRegisteredToken());
        System.out.println(sensor.isRegisterHome());
        System.out.println(home.getId()+"아이디임");
    }

    @Transactional
    @Test
    @DisplayName("센서 리스트 테스트")
    void listSensorTest() {
        //페이지 크기 설정
        Pageable pageable = PageRequest.of(0, 3);
        //관리 대상자 생성
        Home home = new Home();
        home = homeRepository.save(home);
        //센서 생성
        RequestIoTSensor.Register registerDto = RequestIoTSensor.Register.builder()
                .homeId(home.getId())
                .token("1123")
                .type(SensorType.HUMAN_DETECTION)
                .build();
        sensorService.registerUnregisteredSensor(registerDto.getToken(), registerDto.getType());
        //센서 등록
        sensorService.register(registerDto.getHomeId(), registerDto.getToken(), registerDto.getType()).orElseGet(()->null);
        IoTSensor sensor = ioTSensorRepository.findByToken(registerDto.getToken());
        //센서 업데이트
        sensorService.update(sensor.getToken());
        sensorService.update(sensor.getToken());

        System.out.println("home id : " + home.getId());
        Page<ResponseIoTSensor.MySensor> sensorlist = sensorService.sensorlist(home.getId(), pageable);
        System.out.println("총 개수 : " + sensorlist.getTotalElements());
        assertNotNull(sensorlist);
        for (ResponseIoTSensor.MySensor sensor1 : sensorlist) {
            System.out.println("결과:");
            System.out.println(sensor1.getSensorid() + sensor1.getToken() + " " + sensor1.getSensorDateList());
        }
    }

    @Test
    @DisplayName("센서 업데이트")
    @Transactional
    void updateSensorTest(){
        //관리 대상자 생성
        Home home = new Home();
        home = homeRepository.save(home);
        //센서 생성
        RequestIoTSensor.Register registerDto = RequestIoTSensor.Register.builder()
                .homeId(home.getId())
                .token("1123")
                .type(SensorType.HUMAN_DETECTION)
                .build();
        sensorService.registerUnregisteredSensor(registerDto.getToken(), registerDto.getType());
        //센서 등록
        sensorService.register(registerDto.getHomeId(), registerDto.getToken(), registerDto.getType()).orElseGet(()->null);
        IoTSensor sensor = ioTSensorRepository.findByToken(registerDto.getToken());

        //센서 업데이트
        sensorService.update(sensor.getToken());
        sensorService.update(sensor.getToken());
        sensorService.update(sensor.getToken());
        sensorService.update(sensor.getToken());
        sensorService.update(sensor.getToken());

       int lastDate;
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sensorService.update(sensor.getToken());
        lastDate = sensor.getDates().size()-1;
        System.out.println(sensor.getDates().get(lastDate));

    }

    @Test
    @Transactional
    @DisplayName("센서 최신 업데이트 시간 체크 테스트")
    void checkTest(){
        //관리 대상자 생성
        RequestManger.Register registerManager = RequestManger.Register.builder()
                .email("111")
                .password("111")
                .build();
        managerService.register(registerManager);
        Manager manager = managerRepository.findByEmail(registerManager.getEmail());
        Home home = Home.builder()
                .address("용인")
                .manager(manager)
                .build();
        home = homeRepository.save(home);
        Home home1 = Home.builder()
                .address("안양")
                .manager(manager)
                .build();
        home1 = homeRepository.save(home1);
        //센서 등록
        RequestIoTSensor.Register registerDto = RequestIoTSensor.Register.builder()
                .homeId(home.getId())
                .token("aaaa1")
                .type(SensorType.HUMAN_DETECTION)
                .build();
        RequestIoTSensor.Register registerDto1 = RequestIoTSensor.Register.builder()
                .homeId(home1.getId())
                .token("aaaa2")
                .type(SensorType.HUMAN_DETECTION)
                .build();
        sensorService.register(registerDto.getHomeId(), registerDto.getToken(), registerDto.getType()).orElseGet(()->null);
        sensorService.register(registerDto1.getHomeId(), registerDto1.getToken(), registerDto1.getType()).orElseGet(()->null);
        //센서 마지막 업데이트 시간 임의 설정
        IoTSensor sensor1 = ioTSensorRepository.findByToken(registerDto.getToken());
        IoTSensor sensor2 = ioTSensorRepository.findByToken(registerDto1.getToken());
        LocalDateTime testTime1 = LocalDateTime.of(2021,11,18,19,38,30);
        LocalDateTime testTime2 = LocalDateTime.of(2021,11,17,15,38,30);
        Date testDate = Date.from(testTime1.atZone(ZoneId.systemDefault()).toInstant());
        Date testDate2 = Date.from(testTime2.atZone(ZoneId.systemDefault()).toInstant());
//        sensor1.UpdateTimestamp(testDate);
//        sensor2.UpdateTimestamp(testDate2);

        sensorService.check(LocalDateTime.now());
    }
}
