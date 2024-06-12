package com.rest.earthquakeapi.sorting;
import com.rest.earthquakeapi.model.QuakeEntry;
import java.util.Comparator;
public class DepthComparator implements Comparator<QuakeEntry> {
    @Override
    public int compare(QuakeEntry q1, QuakeEntry q2) {
        double depth1 = q1.getDepth();
        double depth2 = q2.getDepth();

        if (depth1 < depth2) {
            return -1;
        } else if (depth1 > depth2) {
            return 1;
        } else {
            return 0;
        }
    }
}