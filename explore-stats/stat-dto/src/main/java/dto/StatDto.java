package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//public interface StatDto {
public class StatDto {
    /* String getApp();
     String getUri();
     int getHits();*/
     private String app;
     private String uri;
     int hits;
}
