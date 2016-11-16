package org.biclogic;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;

/**
 * Created by jgriesel on 2016/11/15.
 */
public class ServoMain {

    public static void main(String Args[]){

        final PCA9685GpioProvider gpioProvider = ServoController.initializeGpio();

        //gpioProvider.reset();

        ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_12, 800, 2000, 2000, 2);
        //ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_13, 2000, 800, 3000, 2);
        //ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_14, 800, 2200, 4000, 2);
        //ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_15, 800, 2200, 5000, 2);

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_12, 2000, 800, 2000, 2);
        //ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_13, 800, 2000, 2000, 2);
        //ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_14, 2200, 800, 2000, 2);
        //ServoController.servoAction(gpioProvider, PCA9685Pin.PWM_15, 2200, 800, 2000, 2);

    }

}
