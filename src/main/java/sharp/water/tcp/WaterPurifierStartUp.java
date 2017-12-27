package sharp.water.tcp;

import com.sharp.netty.core.HeartBeatServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WaterPurifierStartUp {

    public WaterPurifierStartUp() {
        new Thread(() -> {
            HeartBeatServer heartBeatServer = new HeartBeatServer();
            heartBeatServer.start();
        }).start();

    }

    public static void main(String[] args) {
        SpringApplication.run(WaterPurifierStartUp.class, args);
    }
}
