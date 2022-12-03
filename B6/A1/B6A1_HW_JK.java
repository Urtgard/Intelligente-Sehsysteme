import itb2.engine.Controller;
import itb2.filter.AbstractFilter;
import itb2.image.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class B6A1_HW_JK extends AbstractFilter {
    @Override
    public Image filter(Image _input) {
        var input = ImageConverter.convert(_input, GrayscaleImage.class);
        var graph = new Graph(input);

        var selections = Controller.getCommunicationManager().getSelections("Auswahl", 2, input);
        var startPoint = selections.get(0);
        var endPoint = selections.get(1);
        var start = graph.GetNode(startPoint.x, startPoint.y);
        var end = graph.GetNode(endPoint.x, endPoint.y);

        graph.Dijkstra(start, end);
        var path = graph.Backtrack(start, end);

        var output = ImageFactory.getPrecision(input).rgb(input.getSize());
        ImageUtils.copy(_input, output);
//        var outputRgb = ImageConverter.convert(output, RgbImage.class);
        for (var node : path) {
            output.setValue(node.x, node.y, 0, 255, 0);
        }
        output.setValue(start.x, start.y, 255, 0, 0);
        output.setValue(end.x, end.y, 255, 0, 0);

        return output;
    }
}

class Graph {
    private final GrayscaleImage image;
    private final Node[][] nodes;
    private final int width, height;
    private double max_costs;

    public Graph(GrayscaleImage image) {
        this.image = image;

        width = image.getWidth();
        height = image.getHeight();

        nodes = new Node[width][height];

        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                var S = Strobel.S(image, x, y);

                var node = new Node(x, y, S);
                nodes[x][y] = node;

                node.AddNeighbor(GetNode(x - 1, y));
                node.AddNeighbor(GetNode(x - 1, y - 1));
                node.AddNeighbor(GetNode(x - 1, y + 1));
                node.AddNeighbor(GetNode(x, y - 1));
            }
        }
    }

    public Node GetNode(int x, int y) {
        if (x < 0 || x >= width) return null;
        if (y < 0 || y >= height) return null;

        return nodes[x][y];
    }

    private void SetCosts(double g_mod) {
        max_costs = 1;
        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                var node = nodes[x][y];
                var costs = Math.abs(node.S - g_mod);
                node.costs = costs;

                Graph.this.max_costs += costs;
            }
        }

        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                var node = nodes[x][y];
                node.pathCosts = max_costs;
            }
        }
    }

    public void Dijkstra(Node start, Node end) {
        var g_mod = 0.5 * (start.S + end.S);
        this.SetCosts(g_mod);
        start.pathCosts = start.costs;

        var active_nodes = new HashSet<Node>();
        active_nodes.add(start);

        Node v_min = null;
        while (v_min != end) {
            v_min = active_nodes.stream().min(Comparator.comparingDouble((Node node) -> node.costs)).get();
            active_nodes.remove(v_min);
            var neighbors = v_min.neighbors;
            for (var w : neighbors) {
                var costs = v_min.pathCosts + w.costs;
                if (costs < w.pathCosts) {
                    w.pathCosts = costs;
                    active_nodes.add(w);
                }
            }
        }
    }

    public HashSet<Node> Backtrack(Node start, Node end) {
        var nodelist = new HashSet<Node>();
        nodelist.add(end);

        var k = end;
        while (k != start) {
            var neighbors = k.neighbors;
            Node prev_node = null;
            for (var v : neighbors) {
                if (Math.abs(v.pathCosts + k.costs - k.pathCosts) < 0.0001d) {
                    prev_node = v;
                }
            }

            if (prev_node == null) return nodelist;

            nodelist.add(prev_node);
            k = prev_node;
        }

        return nodelist;
    }
}

class Node {
    public final int x, y;
    /**
     * Gradientenbetrag
     */
    public final double S;
    public final ArrayList<Node> neighbors = new ArrayList<>();
    public double costs, pathCosts;

    Node(int x, int y, double S) {
        this.x = x;
        this.y = y;
        this.S = S;
    }

    public void AddNeighbor(Node neighbor) {
        if (neighbor == null) return;

        neighbors.add(neighbor);
        neighbor.neighbors.add(this);
    }
}

class Strobel {
    /**
     * @param image
     * @param x
     * @param y
     * @return Gradientenbetrag
     */
    public static double S(GrayscaleImage image, int x, int y) {
        var Sx = Convolution.calculateConvolution(
                image,
                new double[][]{
                        {-1, 0, 1},
                        {-2, 0, 2},
                        {-1, 0, 1},
                }, x, y);

        var Sy = Convolution.calculateConvolution(
                image,
                new double[][]{
                        {1, 2, 1},
                        {0, 0, 0},
                        {-1, -2, -1},
                }, x, y);

        return Math.pow(Math.pow(Sx, 2) + Math.pow(Sy, 2), 0.5);
    }
}

class Convolution {
    public static double calculateConvolution(GrayscaleImage input, double[][] kernel, int x, int y) {
        var m = kernel.length;
        double value = 0;

        var max = (m - 1) / 2;
        var min = -max;
        for (var u = min; u <= max; u++) {
            for (var v = min; v <= max; v++) {
                var f = f(kernel, u, v);
                var g = g(input, x - u, y - v);
                value += f * g;
            }
        }
        return value;
    }

    private static double f(double[][] kernel, int u, int v) {
        var offset = (kernel.length - 1) / 2;

        return kernel[offset + u][offset + v];
    }

    private static double g(GrayscaleImage input, int x, int y) {
        if (x < 0 || x >= input.getWidth()) return 0;
        if (y < 0 || y >= input.getHeight() - 1) return 0;

        return input.getValue(x, y, 0);
    }
}