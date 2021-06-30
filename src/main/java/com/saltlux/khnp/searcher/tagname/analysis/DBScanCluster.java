package com.saltlux.khnp.searcher.tagname.analysis;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DBScanCluster<T> {

    private List<Cluster<T>> clusters;
    private DistanceMeasure distanceMeasure;
    private Map<T, PointStatus> visited;
    private double eps;
    private int minPts;

    private enum PointStatus {
        NOISE, PART_OF_CLUSTER
    }

    public DBScanCluster(DistanceMeasure distanceMeasure, double eps, int minPts){
        this.eps = eps;
        this.minPts = minPts;
//        this.distanceMap = distanceMap;
        this.distanceMeasure = distanceMeasure;
        this.visited = new HashMap();
        this.clusters = new ArrayList<>();
    }

    public List<Cluster<T>> getClusters(){
        return clusters;
    }
    
    public void cluster(List<T> points) {
        int progress = 0;
        for (T point : points) {
            log.info("clustering : {}/{}", ++progress, points.size());
            if (visited.containsKey(point)) continue;

            List<T> neighbors = getNeighbors(point, points);
            if(neighbors.size() < minPts){
                visited.put(point, PointStatus.NOISE);
                Cluster cluster = new Cluster(-1);
                cluster.add(point);
                clusters.add(cluster);
                continue;
            }

            Cluster<T> cluster = new Cluster(uniqueClusterId());
            cluster.add(point);
            visited.put(point, PointStatus.PART_OF_CLUSTER);

            List<T> seeds = new ArrayList(neighbors);
            int i = 0;
            while (i < seeds.size()) {
                T current = seeds.get(i);
                PointStatus pStatus = visited.get(current);

                if (pStatus == null) {
                    List<T> currentNeighbors = getNeighbors(current, points);
                    if (currentNeighbors.size() >= minPts) {
                        seeds = merge(seeds, currentNeighbors);
                    }
                }

                if (pStatus != PointStatus.PART_OF_CLUSTER) {
                    visited.put(current, PointStatus.PART_OF_CLUSTER);
                    cluster.add(current);
                }
                i++;
            }
            clusters.add(cluster);
        }
    }

    private int uniqueClusterId() {
        int max = 1;
        for(Cluster cluster : clusters)
            max = Math.max(max, cluster.getId());
        return max + 1;

    }

    private List<T> getNeighbors(T point, List<T> points) {
        List<T> neighbors = new ArrayList();
        for (T neighbor : points) {
            if (!point.equals(neighbor) && distanceMeasure.distance(point, neighbor) <= eps) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    private List<T> merge(List<T> one, List<T> two) {
        return Stream
                .concat(one.stream(), two.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
