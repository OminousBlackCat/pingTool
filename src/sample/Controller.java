package sample;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class Controller {
    @FXML
    private TextField hostnameTextFiled;
    @FXML
    private Button confirmButton;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Button cancelButton;
    //用来存后端线程的ping延迟
    private ArrayList<Long> latencyList;
    private boolean threadFlag = true;
    private Thread backendThread;

    @FXML
    private void pingTest(){

    }

    @FXML
    private void stop(){

    }

    private void startThread(){
        threadFlag = true;
        backendThread = new Thread(() -> {
            latencyList = new ArrayList<>();
            String hostname = hostnameTextFiled.getText();
            while (threadFlag){
                try {
                    Thread.sleep(100);
                    long currentTime  = System.currentTimeMillis();
                    boolean isPinged = InetAddress.getByName(hostname).isReachable(10000);
                    currentTime = System.currentTimeMillis() - currentTime;
                    if(isPinged){
                        latencyList.add(currentTime);
                        System.out.println(currentTime);
                    }
                }catch (IOException | InterruptedException e){
                    e.printStackTrace();
                    break;
                }
            }
        });

        backendThread.start();
    }

    private void terminateThread(){
        threadFlag = false;
    }

}
