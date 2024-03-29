package com.saltlux.khnp.searcher.dummy.controller;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@RequestMapping("/api/hisdata")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DummyController {

    @GetMapping("/{tagnames}")
    public SignalResponse signal(
            @PathVariable(value = "tagnames")String[] tagnames,
            @RequestParam(value = "start")String start,
            @RequestParam(value = "end")String end,
            @RequestParam(value = "point", defaultValue = "250")Integer point) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        long std = sdf.parse(start).getTime();
        long etd = sdf.parse(end).getTime();
        long period = (etd - std) / point;

        List<Long> times = LongStream.range(0, point).boxed()
                .map(i -> Instant.ofEpochMilli(std + (i * period))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .toEpochSecond(ZoneOffset.UTC))
                .map(l -> l * 1000)
                .collect(Collectors.toList());

        List<Map<String,Object>> datas = new ArrayList<>();
        List<Metric> calc = new ArrayList<>();
        for(String tagname : tagnames){
            List<List<Object>> data = new ArrayList<>();
            List<Double> values = randomGenerator(point);
            for (int i = 0; i < times.size(); i++) {
                // [[1560259800000, 48.7]. [1560346200000, 48.55]]
                // 배열 0번 : unix timestamp, 배열 1번 value
                data.add(Arrays.asList(times.get(i), values.get(i)));
            }

            LinkedHashMap<String,Object> map = new LinkedHashMap();
            map.put("tag", tagname);
            map.put("data", data);
            datas.add(map);

            DoubleSummaryStatistics stat = values.stream().mapToDouble(Double::doubleValue).summaryStatistics();
            Metric metric = new Metric();
            metric.setTag(tagname);
            metric.setDescription(String.format("%s_description", tagname));
            metric.setMax(stat.getMax());
            metric.setMin(stat.getMin());
            metric.setAvg(stat.getAverage());
            calc.add(metric);
        }

        Timestamps timestamps = new Timestamps();
        timestamps.datas = datas;
        timestamps.calc = calc;

        SignalResponse resp = new SignalResponse();
        resp.result = timestamps;
        return resp;
    }

    private List<Double> randomGenerator(int point){
        Random r = new Random(System.nanoTime());
        double d1 = -10 + 20 * r.nextDouble();
        double d2 = -10 + 20 * r.nextDouble();
        return IntStream.range(0, point)
                .mapToDouble(i -> Math.min(d1, d2) + (Math.max(d1, d2) - Math.min(d1, d2)) * r.nextDouble())
                .boxed()
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public class SignalResponse{
        Integer code = 0;
        Object result;
    }

    @Getter
    @Setter
    public class Timestamps{
        List<Map<String,Object>> datas;
        List<Metric> calc;
    }

    @Getter
    @Setter
    public class Metric{
        String tag;
        String description;
        Double min;
        Double max;
        Double avg;
    }

}
