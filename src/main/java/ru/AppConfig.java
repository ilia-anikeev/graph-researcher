package ru;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.controllers.BuildGraphController;
import ru.controllers.DataBaseController;
import ru.controllers.SaveGraphController;
import ru.controllers.SecurityController;

@Configuration
public class AppConfig {
    @Bean
    public BuildGraphController buildGraphController() {
        return new BuildGraphController();
    }

    @Bean
    public DataBaseController dataBaseController() {
        return new DataBaseController();
    }

    @Bean
    public SaveGraphController saveGraphController() {
        return new SaveGraphController();
    }

    @Bean
    public SecurityController securityGraphController() {
        return new SecurityController();
    }
}
