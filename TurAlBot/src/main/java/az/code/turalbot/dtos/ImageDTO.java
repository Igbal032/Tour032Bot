package az.code.turalbot.dtos;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private String UUID;
    private String companyName;
    private double price;
    private String dateRange;
    private String place;
    private String notes;
    private String description;
}
