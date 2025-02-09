package com.cs237.kafkajava.controller;


import com.cs237.kafkajava.consumer.MyTopicConsumer;
import com.google.gson.Gson;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
public class KafkaController {
    private KafkaTemplate<String, String> template;
    private MyTopicConsumer myTopicConsumer;
//    private List<String> CategoryList;

    private List<Shoes> Grocery_map;

    private Random random_number;
    private String csv_path;
    private int Shoes_index;

    public KafkaController(KafkaTemplate<String, String> template, MyTopicConsumer myTopicConsumer) throws FileNotFoundException {
        this.template = template;
        this.myTopicConsumer = myTopicConsumer;
        this.csv_path = "src/main/resources/Mock-data-mens-shoes.csv";
        this.random_number = new Random();
        this.Grocery_map = new CsvToBeanBuilder(new FileReader(this.csv_path))
                .withType(Shoes.class)
                .build()
                .parse();
        this.Shoes_index = 0;
        String[] colors_fake = {"White", "Black", "Grey", "Yellow", "Red", "Green", "Brown", "Multicolor", "Other"};
        for (Shoes p : this.Grocery_map) {
            p.set("colors", colors_fake[this.random_number.nextInt(7)]);
        };

    }


    Timer timer = new Timer();

    class Task extends TimerTask {
        @Override
        public void run() {
            if(Shoes_index >= Grocery_map.size())return;
            template.send((String) Grocery_map.get(Shoes_index).get("colors"), new Gson().toJson(Grocery_map.get(Shoes_index)));
            System.out.println(Grocery_map.get(Shoes_index).get("colors") + Grocery_map.get(Shoes_index).toString());
            Shoes_index++;
        }

    }

    @GetMapping("/kafka/produce_random")
    public void produce() throws InterruptedException {
        //control param to topic, Map<String, String>
        while(Shoes_index < Grocery_map.size()){
            int delay = new Random().nextInt(100) * 100;
            timer.schedule(new Task(), delay);
            TimeUnit.SECONDS.sleep(1);
        }

    }

    @GetMapping("/kafka/producetest")
    public void produce(@RequestParam String message) {
        //control param to topic, Map<String, String>
        template.send("meat", message);
    }

    @GetMapping("/kafka/white_messages")
    public List<String> getWhiteMessages() {
        return myTopicConsumer.getWhite_messages();
    }

    @GetMapping("/kafka/white_recent")
    public Queue<String> getWhiteQueue(){
        return myTopicConsumer.getWhite_queue();
    }

    @GetMapping("/kafka/black_messages")
    public List<String> getBlackMessages() {
        return myTopicConsumer.getBlack_messages();
    }

    @GetMapping("/kafka/black_recent")
    public Queue<String> getBlackQueue(){
        return myTopicConsumer.getBlack_queue();
    }

}
