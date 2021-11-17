package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Controller {
    @FXML
    public Label timeLabel;
    @FXML
    public Label latencyLabel;
    @FXML
    public Label countLabel;
    @FXML
    public Label averageLabel;
    @FXML
    public Label worstCountLabel;
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
    @FXML
    private Button pauseButton;

    //用来存后端线程的ping延迟
    private boolean threadFlag = true;
    private Thread backendThread;
    //图表的数据
    private XYChart.Series series;
    private boolean pauseFlag;

    public void init(){
        cancelButton.setDisable(true);
        pauseButton.setDisable(true);
    }

    @FXML
    private void pingTest(){
        pauseFlag = false;
        threadFlag = true;
        startThread();
    }

    @FXML
    private void stop(){
        terminateThread();
    }

    @FXML
    private void pause(){
        if(!pauseFlag){
            pauseFlag = true;
            pauseButton.setText("继续");
        }else {
            pauseFlag = false;
            pauseButton.setText("暂停");
        }
    }

    private void startThread(){
        series = new XYChart.Series();
        lineChart.setLegendVisible(false);
        Platform.runLater(()->
                lineChart.getData().add(series));
        lineChart.setCreateSymbols(false);
        backendThread = new Thread(() -> {
            String hostname = hostnameTextFiled.getText();
            long count = 0;
            long latencySum = 0;
            long worstCount = 0;
            while (threadFlag){
                try {
                    Thread.sleep(300);
                    if(pauseFlag){
                        continue;
                    }
                    long currentTime  = System.currentTimeMillis();
                    String currentDate = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
                    boolean isPinged = InetAddress.getByName(hostname).isReachable(1000);
                    System.out.println(isPinged);
                    currentTime = System.currentTimeMillis() - currentTime;
                    count ++;
                    long finalCount = count;
                    long finalCurrentTime;
                    if(isPinged && currentTime<999){
                        finalCurrentTime = currentTime;
                    }else{
                        finalCurrentTime = 999;
                    }
                    latencySum = latencySum + finalCurrentTime;
                    long finalAverage = latencySum / count;
                    if(finalCurrentTime > finalAverage * 2){
                        worstCount ++;
                    }
                    long finalWorstCount = worstCount;
                    Platform.runLater(()->{
                        series.getData().add(new XYChart.Data<Number, Number>(finalCount, finalCurrentTime));
                        countLabel.setText("总测试数："+ finalCount);
                        timeLabel.setText("测试时间：" + currentDate);
                        averageLabel.setText("平均时延：" + finalAverage);
                        latencyLabel.setText("ping延迟：" + finalCurrentTime);
                        worstCountLabel.setText("延迟显著高于平均时延的次数：" + finalWorstCount);
                    });
                    System.out.println(currentTime);
                }catch (IOException | InterruptedException e){
                    e.printStackTrace();
                    break;
                }
            }
        });

        backendThread.start();
        confirmButton.setDisable(true);
        cancelButton.setDisable(false);
        pauseButton.setDisable(false);
    }

    private void terminateThread(){
        threadFlag = false;
        lineChart.getData().clear();
        confirmButton.setDisable(false);
        cancelButton.setDisable(true);
        pauseButton.setDisable(true);
    }

}
