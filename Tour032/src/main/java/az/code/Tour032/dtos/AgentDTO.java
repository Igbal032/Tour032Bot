package az.code.Tour032.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {
    private String name;
    private String email;
    private String companyName;
    private String VOEN;
    private String password;
}
