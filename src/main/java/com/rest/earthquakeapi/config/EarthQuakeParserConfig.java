package com.rest.earthquakeapi.config;

import com.rest.earthquakeapi.ParserManager.EarthQuakeParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EarthQuakeParserConfig {

    @Bean
    public EarthQuakeParser earthQuakeParser(){
        return new EarthQuakeParser();
    }


}
