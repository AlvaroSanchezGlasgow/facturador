package com.modules.invoicer.config;

import com.modules.invoicer.InvoicerApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.stereotype.Component;

@Component
public class ModulithStartupLogger implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ModulithStartupLogger.class);

    @Override
    public void run(ApplicationArguments args) {
        ApplicationModules modules = ApplicationModules.of(InvoicerApplication.class);
        modules.stream()
                .map(ApplicationModule::getDisplayName)
                .forEach(name -> logger.info("Detected modulith module: {}", name));
    }
}
