package io.arabesque.pattern;

import io.arabesque.conf.Configuration;
import io.arabesque.graph.Edge;
import io.arabesque.graph.MainGraph;
import io.arabesque.graph.Vertex;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PatternEdge implements Comparable<PatternEdge>, Writable {

    private int srcPos;
    private int srcLabel;
    private int destPos;
    private int destLabel;
    private boolean isForward;

    public PatternEdge() {
        this(-1, -1, -1, -1, true);
    }

    public PatternEdge(PatternEdge edge) {
        setFromOther(edge);
    }

    public PatternEdge(int srcPos, int srcLabel, int destPos, int destLabel) {
        this(srcPos, srcLabel, destPos, destLabel, true);
    }

    public PatternEdge(int srcPos, int srcLabel, int destPos, int destLabel, boolean isForward) {
        this.srcPos = srcPos;
        this.srcLabel = srcLabel;
        this.destPos = destPos;
        this.destLabel = destLabel;
        this.isForward = isForward;
    }

    public void setFromOther(PatternEdge edge) {
        setSrcPos(edge.getSrcPos());
        setSrcLabel(edge.getSrcLabel());

        setDestPos(edge.getDestPos());
        setDestLabel(edge.getDestLabel());

        isForward(edge.isForward());
    }

    public void setFromEdge(Edge edge, int srcPos, int dstPos) {
        MainGraph mainGraph = Configuration.get().getMainGraph();

        int srcVertexId = edge.getSourceId();
        int dstVertexId = edge.getDestinationId();

        Vertex srcVertex = mainGraph.getVertex(srcVertexId);
        Vertex dstVertex = mainGraph.getVertex(dstVertexId);

        setSrcPos(srcPos);
        setDestPos(dstPos);
        setSrcLabel(srcVertex.getVertexLabel());
        setDestLabel(dstVertex.getVertexLabel());

        isForward(true);
    }

    public void setFromEdge(Edge edge, int srcPos, int dstPos, int srcId) {
        setFromEdge(edge, srcPos, dstPos);

        if (edge.getSourceId() != srcId) {
            invert();
        }
    }

    public void invert() {
        int tmp = srcPos;
        srcPos = destPos;
        destPos = tmp;

        tmp = srcLabel;
        srcLabel = destLabel;
        destLabel = tmp;
    }

    public int getSrcPos() {
        return srcPos;
    }

    public void setSrcPos(int srcPos) {
        this.srcPos = srcPos;
    }

    public int getSrcLabel() {
        return srcLabel;
    }

    public void setSrcLabel(int srcLabel) {
        this.srcLabel = srcLabel;
    }

    public int getDestPos() {
        return destPos;
    }

    public void setDestPos(int destPos) {
        this.destPos = destPos;
    }

    public int getDestLabel() {
        return destLabel;
    }

    public void setDestLabel(int destLabel) {
        this.destLabel = destLabel;
    }

    public boolean isForward() {
        return isForward;
    }

    public void isForward(boolean type) {
        this.isForward = type;
    }


    public String toString() {
        //return ("[" + srcPos + "," + srcLabel + "-" + destPos + "," + destLabel + "-" + (isForward ? 'F' : 'B') + "]");
        return ("[" + srcPos + "," + srcLabel + "-" + destPos + "," + destLabel + "]");
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.srcPos);
        out.writeInt(this.srcLabel);
        out.writeInt(this.destPos);
        out.writeInt(this.destLabel);
        //out.writeBoolean(this.isForward);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.srcPos = in.readInt();
        this.srcLabel = in.readInt();
        this.destPos = in.readInt();
        this.destLabel = in.readInt();
        //this.isForward = in.readBoolean();
    }

    public boolean isSmaller(PatternEdge e) {
        boolean isSmaller = false;

        if (this.srcPos == e.getSrcPos() && this.destPos == e.getDestPos()) {
            if (this.srcLabel == e.getSrcLabel()) {
                if (this.destLabel < e.getDestPos()) {
                    isSmaller = true;
                }
            } else if (this.srcLabel < e.getSrcLabel()) {
                isSmaller = true;
            }
        } else {
            //fwd, fwd
            if (this.isForward && e.isForward()) {
                if (this.destPos < e.getDestPos())
                    isSmaller = true;
                else if (this.destPos == e.getDestPos()) {
                    if (this.srcPos > e.getSrcPos())
                        isSmaller = true;
                }
            }
            //bwd, bwd
            else if (!this.isForward && !e.isForward()) {
                if (this.srcPos < e.getSrcPos())
                    isSmaller = true;
                if (this.srcPos == e.getSrcPos()) {
                    if (this.destPos < e.getDestPos())
                        isSmaller = true;
                }
            }

            //fwd, bwd
            else if (this.isForward && !e.isForward()) {
                if (this.destPos <= e.getSrcPos()) {
                    isSmaller = true;
                }
            }
            //bwd, fwd
            else {
                if (this.srcPos < e.getDestPos()) {
                    isSmaller = true;
                }
            }
        }
        return isSmaller;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternEdge that = (PatternEdge) o;

        if (srcPos != that.srcPos) return false;
        if (srcLabel != that.srcLabel) return false;
        if (destPos != that.destPos) return false;
        if (destLabel != that.destLabel) return false;
        return true;
        //return isForward == that.isForward;

    }

    @Override
    public int hashCode() {
        int result = srcPos;
        result = 31 * result + srcLabel;
        result = 31 * result + destPos;
        result = 31 * result + destLabel;
        //result = 31 * result + (isForward ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(PatternEdge o) {
        if (equals(o))
            return 0;
        else if (isSmaller(o)) return -1;
        else return 1;

    }
}