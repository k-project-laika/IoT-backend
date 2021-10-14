package com.laika.IoT.web;

import com.laika.IoT.exception.errors.CustomJwtRuntimeException;
import com.laika.IoT.exception.errors.RegisterSensorFailedException;
import com.laika.IoT.provider.service.SensorService;
import com.laika.IoT.web.dto.CommonResponse;
import com.laika.IoT.web.dto.RequestIoTSensor;
import com.laika.IoT.web.dto.ResponseIoTSensor;
import com.laika.IoT.web.dto.ResponseManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class IoTSensorController {
    private final SensorService sensorService;

    @PostMapping("/sensor/register")
    public ResponseEntity<CommonResponse> registerSensor(@RequestBody @Valid RequestIoTSensor.Register register) {
        ResponseIoTSensor.Register responseDto = sensorService.register(register.getHomeId(), register.getToken()).orElseThrow(()-> new RegisterSensorFailedException());

        CommonResponse response = CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("등록 성공")
                .list(responseDto)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}