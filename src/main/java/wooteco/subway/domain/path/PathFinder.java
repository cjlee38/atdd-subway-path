package wooteco.subway.domain.path;

import java.util.List;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineSeries;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public static PathFinder from(LineSeries lineSeries) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        final List<Line> lines = lineSeries.getLines();
        for (Line line : lines) {
            final List<Section> sections = line.getSectionSeries().getSections();
            addStationEdge(graph, sections);
        }
        return new PathFinder(graph);
    }

    private static void addStationEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
            graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()),
                section.getDistance().getValue()
            );
        }
    }

    public List<Station> findShortestPath(Station source, Station destination) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph);
        return path.getPath(source, destination).getVertexList();
    }

    public int getDistance(Station source, Station destination) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> path = new DijkstraShortestPath<>(graph);
        final double weight = path.getPath(source, destination).getWeight();
        return (int)Math.round(weight);
    }
}
