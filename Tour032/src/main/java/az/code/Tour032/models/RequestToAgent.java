package az.code.Tour032.models;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "request_to_agent")
public class RequestToAgent implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Agent agent;
    @OneToOne
    private Requests requests;
    private String requestStatus;
    private boolean isArchive;
    @CreationTimestamp
    private LocalDateTime createdDate;
}
