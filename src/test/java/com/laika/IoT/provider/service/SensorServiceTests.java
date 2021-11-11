package com.laika.IoT.provider.service;

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
    //

    @Test
    @DisplayName("센서 등록 테스트")
    @Transactional
    void registerSensorTest() {
        //관리 대상자 생성
        Home home = new Home();
        home = homeRepository.save(home);
        //센서 등록
        RequestIoTSensor.Register registerDto = RequestIoTSensor.Register.builder()
                .homeId(home.getId())
                .token("aaaa")
                .type(SensorType.HUMAN_DETECTION)
                .build();
        ResponseIoTSensor.Register responseIoTSensor = sensorService.register(registerDto.getHomeId(), registerDto.getToken(), registerDto.getType()).orElseGet(()->null);

        //검증
        assertNotNull(responseIoTSensor);
        assertNotNull(home.getId());
        System.out.println(responseIoTSensor.getRegisteredToken());
        System.out.println(home.getId()+"아이디임");
    }
    @Transactional
    @Test
    @DisplayName("센서 리스트 테스트")
    void listSensorTest() {
        RequestManger.Register dto = RequestManger.Register.builder()
                .email("hello")
                .password("itsmypassword")
                .build();
        managerService.register(dto);
        Manager manager = managerRepository.findByEmail(dto.getEmail());
        Pageable pageable = PageRequest.of(0, 3);

        managerService.registerHome(manager.getEmail(), "경기도 용인시");
        Home home = homeRepository.findByAddress("경기도 용인시");

        sensorService.register(home.getId(), "123", SensorType.HUMAN_DETECTION);
        sensorService.register(home.getId(), "1234", SensorType.HUMAN_DETECTION);
        sensorService.register(home.getId(), "1235", SensorType.HUMAN_DETECTION);
        sensorService.register(home.getId(), "12325", SensorType.HUMAN_DETECTION);
        sensorService.register(home.getId(), "123125", SensorType.HUMAN_DETECTION);

        System.out.println("home id : " + home.getId());
        Page<ResponseIoTSensor.MySensor> sensorlist = sensorService.sensorlist(home.getId(), pageable);
        System.out.println("총 개수 : " + sensorlist.getTotalElements());
        assertNotNull(sensorlist);
        for (ResponseIoTSensor.MySensor sensor : sensorlist) {
            System.out.println("결과:");
            System.out.println(sensor.getSensorid() + sensor.getToken() + " " + sensor.getTimestamp());
        }
    }

    @Test
    @DisplayName("센서 업데이트")
    @Transactional
    void updateSensorTest(){
        //관리 대상자 생성
        Home home = new Home();
        home = homeRepository.save(home);
        //센서 등록
        RequestIoTSensor.Register registerDto = RequestIoTSensor.Register.builder()
                .homeId(home.getId())
                .token("aaaa")
                .type(SensorType.HUMAN_DETECTION)
                .build();
        sensorService.register(registerDto.getHomeId(), registerDto.getToken(), registerDto.getType()).orElseGet(()->null);
        IoTSensor sensor = ioTSensorRepository.findByToken(registerDto.getToken());

        System.out.println(sensor.getTimestamp());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sensorService.update(sensor.getToken());
        System.out.println(sensor.getTimestamp());

    }
}
