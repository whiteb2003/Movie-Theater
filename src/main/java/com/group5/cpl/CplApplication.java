package com.group5.cpl;

import com.group5.cpl.model.Genre;
import com.group5.cpl.model.Schedule;
import com.group5.cpl.repository.GenreRepository;
import com.group5.cpl.repository.ScheduleRepository;
import com.group5.cpl.service.StorageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;


@SpringBootApplication
public class CplApplication {

    public static void main(String[] args) {
        SpringApplication.run(CplApplication.class, args);
    }

    @Bean
	CommandLineRunner init(StorageService storageService){
		return (args) -> {
			storageService.init();
		};
	}
    
//     @Bean
//     CommandLineRunner commandLineRunner(GenreRepository repository){
//         return  args -> {
//             Genre g1 = new Genre("Thriller");
//             Genre g2 = new Genre("Adventure");
//             Genre g3 = new Genre("Romance");
//             Genre g4 = new Genre("Horror");
//             Genre g5 = new Genre("Sci-fi");
//
//             repository.saveAll(List.of(g1,g2,g3,g4,g5));
//         };
//     }
}
