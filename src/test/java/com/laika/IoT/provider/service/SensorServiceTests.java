package com.laika.IoT.provider.service;

import com.laika.IoT.entity.Home;
import com.laika.IoT.repository.IoTSensorRepository;
import com.laika.IoT.repository.RecipientRepository;
import com.laika.IoT.web.dto.RequestIoTSensor;
import com.laika.IoT.web.dto.ResponseIoTSensor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
public class SensorServiceTests {
    @Autowired
    private SensorService sensorService;
    @Autowired
    private RecipientRepository recipientRepository;
    @Autowired
    private IoTSensorRepository ioTSensorRepository;

    @Test
    @DisplayName("센서 등록 테스트")
    @Transactional
    void registerSensorTest() {
        //관리 대상자 생성
        Home home = new Home();
        home = recipientRepository.save(home);
        //센서 등록
        RequestIoTSensor.Register registerDto = RequestIoTSensor.Register.builder()
                .homeId(home.getId())
                .build();
        ResponseIoTSensor.Register responseIoTSensor = sensorService.register(registerDto.getHomeId(), registerDto.getToken()).orElseGet(()->null);

        //검증
        assertNotNull(responseIoTSensor);
        assertNotNull(home.getId());
        System.out.println(responseIoTSensor.getRegisteredToken());
        System.out.println(home.getId()+"아이디임");
    }
}