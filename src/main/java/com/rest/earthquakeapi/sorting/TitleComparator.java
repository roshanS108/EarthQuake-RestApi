package com.rest.earthquakeapi.sorting;
import com.rest.earthquakeapi.model.QuakeEntry;
import java.util.Comparator;
public class TitleComparator implements Comparator<QuakeEntry> {
    public int compare(QuakeEntry q1, QuakeEntry q2) {
        return q1.getTitle().compareTo(q2.getTitle());
    }
}
