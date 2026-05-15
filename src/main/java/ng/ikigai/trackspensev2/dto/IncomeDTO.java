package ng.ikigai.trackspensev2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String icon;
    private String categoryName;
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive")
    private BigDecimal amount;

    private LocalDate date;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
