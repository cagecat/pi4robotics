package org.biclogic;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Created by jgriesel on 2016/11/14.
 *
 *      2016/11/14
 *          work out how many steps should be taken
 *          and the delay between steps to finish move in given duration
 *          stepWidth is eg 2, servo moves two positions per step.
 *
 *      2016/11/15
 *          working with negative direction
 *
 */



public class ServoController {

    static int stepCount;
    static String direction;

    public static int getStepCount() {
        return stepCount;
    }
    public static void setStepCount(int stepCount) {
        ServoController.stepCount = stepCount;
    }

    public static String getDirection() {
        return direction;
    }
    public static void setDirection(String direction) {
        ServoController.direction = direction;
    }



    public static void servoAction(final PCA9685GpioProvider provider, final Pin pin, final int startPosition, final int endPosition, int duration, int stepWidth){

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stepCount = (startPosition - endPosition);

        setDirection("positive");

        if (stepCount < 0){
            setStepCount(Math.abs(stepCount));
            setDirection("negative");
        }

        final Long speedNano = Long.valueOf((duration * 1000) / (stepCount / stepWidth));


        System.out.println("Move " + pin + " from " + startPosition + " to " + endPosition + ", direction is " + direction + ", duration will be " + duration );


        Thread t = new Thread(new Runnable() {

            public void run() {
                System.out.println(direction);
                if (direction == "positive") {
                    for (int i = startPosition; i < endPosition; i++) {
                        provider.setPwm(pin, i);
                        try {
                            TimeUnit.MICROSECONDS.sleep(speedNano);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (direction == "negative") {
                    for (int i = startPosition; i > endPosition; i--) {
                        provider.setPwm(pin, i);
                        try {
                            TimeUnit.MICROSECONDS.sleep(speedNano);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        t.start();

    }

    public static PCA9685GpioProvider initializeGpio() {

        BigDecimal frequency = new BigDecimal("48.828");
        BigDecimal frequencyCorrectionFactor = new BigDecimal("1.0578");
        I2CBus bus = null;
        try {
            try {
                bus = I2CFactory.getInstance(I2CBus.BUS_1);

            } catch (I2CFactory.UnsupportedBusNumberException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        PCA9685GpioProvider gpioProvider = null;
        try {
            gpioProvider = new PCA9685GpioProvider(bus, 0x40, frequency, frequencyCorrectionFactor);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        provisionPwmOutputs(gpioProvider);
        return gpioProvider;
    }

    private static GpioPinPwmOutput[] provisionPwmOutputs(PCA9685GpioProvider gpioProvider) {
        GpioController gpio = GpioFactory.getInstance();
        GpioPinPwmOutput myOutputs[] =
                { gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_00),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_01),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_02),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_03),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_04),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_05),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_06),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_07),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_08),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_09),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_10),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_11),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_12),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_13),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_14),
                        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_15) };
        return myOutputs;
    }


    /*
    public static void main(String args[]){

        setToZero();

        final PCA9685GpioProvider gpioProvider = initializeGpio();

        int speed = 1;
        int step = 4;
        int min = 700;
        int max = 2500;

        Thread servo01 = new Thread(new Runnable() {

            public void run() {

                for (int i = min; i < max; i = i+step){
                    System.out.println("Moving to " + i);
                    gpioProvider.setPwm(PCA9685Pin.PWM_15, i);
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        Thread servo02 = new Thread(new Runnable() {

            public void run() {

                for (int i = min; i < max; i = i+step){
                    System.out.println("Moving to " + i);
                    gpioProvider.setPwm(PCA9685Pin.PWM_14, i);
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        Thread servo03 = new Thread(new Runnable() {

            public void run() {

                for (int i = min; i < max; i = i+step){
                    System.out.println("Moving to " + i);
                    gpioProvider.setPwm(PCA9685Pin.PWM_13, i);
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        Thread servo04 = new Thread(new Runnable() {

            public void run() {

                for (int i = min; i < max; i = i+step){
                    System.out.println("Moving to " + i);
                    gpioProvider.setPwm(PCA9685Pin.PWM_12, i);
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        servo01.start();
        servo02.start();
        servo03.start();
        servo04.start();


    }


    private static void setToZero() {
        final PCA9685GpioProvider gpioProvider = initializeGpio();
        System.out.println("resetting");

        gpioProvider.setPwm(PCA9685Pin.PWM_15, 700);
        gpioProvider.setPwm(PCA9685Pin.PWM_14, 700);
        gpioProvider.setPwm(PCA9685Pin.PWM_13, 700);
        gpioProvider.setPwm(PCA9685Pin.PWM_12, 700);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        gpioProvider.reset();
    }
     */


    // This was the old way of ding threads
/*        Thread t = new Thread(new Runnable() {

            public void run() {

                System.out.println("starting");


                // if direction negative - should subtract
                for (int i = steps; i < stepCount; i = i+stepWidth){
                    provider.setPwm(pin, i);
                    try {
                        TimeUnit.MICROSECONDS.sleep(speedNano);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("finished");

            }
        });
        t.start();*/




/*        Runnable t2 = () -> {
            // move from high to low
            if (direction == "positive"){

                for (int i = startPosition; i > endPosition; i = i-stepWidth){
                    provider.setPwm(pin, i);
                    try {
                        TimeUnit.MICROSECONDS.sleep(speedNano);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            // move from low to high
            } else {

                for (int i = startPosition; i < endPosition; i = i+stepWidth){
                    provider.setPwm(pin, i);
                    try {
                        TimeUnit.MICROSECONDS.sleep(speedNano);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        };*/
}