package grain;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.grain.media.MediaUtils;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

/**
 * @author wulifu
 */
public class Bootstrap extends Application {
    private static JFrame jFrame = new JFrame();

    public static void main(String[] args) {
//        initJFrame();
        //playVideo();
        launch(args);
    }

    private static void initJFrame() {
        jFrame.setTitle("测试窗口");
        jFrame.setSize(1000,500);
        jFrame.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                System.out.println(".....................");
            }

            @Override
            public void focusLost(FocusEvent e) {
                System.out.println(".....................1111111111");
            }
        });
        jFrame.setVisible(true);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Media media = new Media(new File("C:\\Users\\wulifu\\Desktop\\91789000202007284679740.3gp").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        Slider processSlider=new Slider();
        Label processLabel=new Label();
        Button playButton=new Button("||");
        Button rePlayButton=new Button(">>");
        Slider volumeSlider=new Slider();

        playButton.setOnAction(e->{
            String text=playButton.getText();
            if(text=="||"){
                playButton.setText(">");
                mediaPlayer.play();
            }
            else {
                playButton.setText("||");
                mediaPlayer.pause();
            }
        });

        rePlayButton.setOnAction(e->{
            mediaPlayer.seek(Duration.ZERO);
        });

        volumeSlider.setValue(10);
        mediaPlayer.volumeProperty().bind(volumeSlider.valueProperty().divide(100));

        Duration totalDuration = mediaPlayer.getTotalDuration();
        String totalString= MediaUtils.DurationToString(totalDuration);
        double maxProcessSlider= processSlider.getMax();
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                processSlider.setValue(newValue.toSeconds() / totalDuration.toSeconds() * 100);
                processLabel.setText(MediaUtils.DurationToString(newValue) + " " + MediaUtils.DurationToString(mediaPlayer.getTotalDuration()));
            }
        });

        processSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                double totalTime=media.getDuration().toMillis();
                double newTime=processSlider.valueProperty().getValue()/100*totalTime;
                mediaPlayer.seek(Duration.millis(newTime));
            }
        });

        BorderPane borderPane=new BorderPane();
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(processSlider,processLabel,playButton,rePlayButton,volumeSlider);
        borderPane.setBottom(hbox);
        borderPane.setCenter(mediaView);

        Scene scene = new Scene(borderPane,media.getHeight(),media.getWidth());
        primaryStage.setScene(scene);
        primaryStage.setTitle("mediaplayer");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        jFrame.setVisible(false);
    }
}
