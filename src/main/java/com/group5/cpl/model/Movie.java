package com.group5.cpl.model;

import com.group5.cpl.model.enums.Movie_status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movie")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movie_id;
    @Column(name = "movie_title_en", columnDefinition = "longtext")
    private String movie_title_en;
    @Column(name = "movie_title_vn", columnDefinition = "longtext")

    private String movie_title_vn;
    @Column(name = "shortDescription", columnDefinition = "longtext")

    private String shortDescription;
    @Column(name = "description", columnDefinition = "longtext")

    private String description;

    private String duration; // format: 00:00

    private Date release_date;
    @Column(name = "actors", columnDefinition = "longtext")

    private String actors;

    private String production_company;

    private String directors;

    private String poster;

    private String banner;

    private Movie_status movie_status;

    @Column(name = "trailer", columnDefinition = "longtext")
    private String trailer;

    @ManyToMany()
    @JoinTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id", referencedColumnName = "movie_id"), inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "genre_id"))
    private List<Genre> movie_genres;

    @Transient
    public String getPosterImagePath() {
        if (poster == null || movie_id == null)
            return null;

        return "/images/" + poster;
    }

    @Transient
    public String getBannerImagePath() {
        if (banner == null || banner == null)
            return null;
        return "/images/" + banner;
    }

    @OneToMany(mappedBy = "movie")
    private List<Movie_Room_Date> movie_Room_Dates;
    // @ManyToMany()
    // @JoinTable(
    // name = "movie_actor",
    // joinColumns = @JoinColumn(
    // name = "movie_id",
    // referencedColumnName = "movie_id"
    // ),
    // inverseJoinColumns = @JoinColumn(
    // name = "actor_id",
    // referencedColumnName = "actor_id"
    // )
    // )
    // private List<Actor> actors;
    //
    // @ManyToOne
    // @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    // private ProductionCompany productionCompany;
    //
    // @ManyToOne
    // @JoinColumn(name = "director_id", referencedColumnName = "director_id")
    // private Director director;

}
