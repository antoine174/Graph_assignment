package org.example;

public record Pair(int first, int second) implements Comparable<Pair> {

    @Override
    public int compareTo(Pair other) {
        if (this.first != other.first) {
            return Integer.compare(this.first, other.first);
        }
        return Integer.compare(this.second, other.second);
    }
}