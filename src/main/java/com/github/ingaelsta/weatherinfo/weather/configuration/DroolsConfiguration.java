package com.github.ingaelsta.weatherinfo.weather.configuration;

import java.io.IOException;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfiguration {

    private final KieServices kieServices = KieServices.Factory.get();

    private KieFileSystem getKieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/WindDirection.drl"));
        return kieFileSystem;

    }

    @Bean
    public KieSession getKieSession() throws IOException {
        KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
        kb.buildAll();

        KieRepository kieRepository = kieServices.getRepository();
        KieContainer kieContainer  = kieServices
                .newKieContainer(kieRepository
                        .getDefaultReleaseId());
        KieSession kieSession = kieContainer.newKieSession();

        System.out.println("KieSession created...");
        return kieSession;
    }

}
