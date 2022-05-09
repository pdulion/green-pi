package com.dulion.green.pi;


import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class GreenTask {
    private static final Logger log = LoggerFactory.getLogger(GreenTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static final byte CTRL_HUMIDITY = (byte) 0xF2;
    private static final byte CTRL_MEASURE = (byte) 0xf4;
    private static final byte OSRS_HUMIDITY = (byte) 0x02;
    private static final byte OSRS_TEMPERATURE = (byte) 0x02;
    private static final byte OSRS_PRESSURE = (byte) 0x02;
    private static final byte MODE = (byte) 0x01;
    private static final long WAIT_MILLIS = (long) (1.25
            + (2.3 * OSRS_TEMPERATURE)
            + (2.3 * OSRS_PRESSURE) + 0.575
            + (2.3 * OSRS_HUMIDITY) + 0.575);
    private static final byte PRESSURE_DATA_REG = (byte) 0xf7;
    private static final byte TEMPERATURE_DATA_REG = (byte) 0xfa;
    private static final byte HUMIDITY_DATA_REG = (byte) 0xfd;

    public GreenTask() {
    }

    @Scheduled(fixedRate = 10_000)
    public void pollSensor() throws InterruptedException {
        Context pi4j = Pi4J.newAutoContext();
        I2CProvider provider = pi4j.provider("linuxfs-i2c");
        I2CConfig config = I2C.newConfigBuilder(pi4j).id("BME280-0").bus(1).device(0x77).build();
        try (I2C bus = provider.create(config)) {
            // Initiate reading
            bus.write(CTRL_HUMIDITY, OSRS_HUMIDITY);
            bus.write(CTRL_MEASURE, (byte) (OSRS_TEMPERATURE << 5 | OSRS_PRESSURE << 2 | MODE));

            // Calibration
            byte[] comp_temperature = new byte[6];
            bus.readRegister(0x88, comp_temperature, 0, 6);

            byte[] comp_pressure = new byte[18];
            bus.readRegister(0x8E, comp_pressure, 0, 18);

            byte[] comp_humidity = new byte[8];
            bus.readRegister(0xA1, comp_humidity, 0, 1);
            bus.readRegister(0xE1, comp_humidity, 1, 7);

            // Wait for reading to complete
            Thread.sleep(WAIT_MILLIS);

            // Read results

        }
        log.info("The time is now {}", dateFormat.format(new Date()));
    }
}
