package com.rest.earthquakeapi.filter;

import com.rest.earthquakeapi.model.QuakeEntry;

public class PhraseFilter implements Filter{

    private String typeOfRequest;
    private String phraseToSearch;

    public PhraseFilter(String typeOfRequest, String phraseToSearch) {
        this.typeOfRequest = typeOfRequest;
        this.phraseToSearch = phraseToSearch;
    }

    @Override
    public boolean satisfies(QuakeEntry qe) {
        String title = qe.getInfo();
        return switch (typeOfRequest) {
            case "start" -> title.startsWith(phraseToSearch);
            case "end" -> title.endsWith(phraseToSearch);
            case "any" -> title.contains(phraseToSearch);
            default -> false;
        };
    }
}
