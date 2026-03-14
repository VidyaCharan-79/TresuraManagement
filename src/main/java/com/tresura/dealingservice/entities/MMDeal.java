import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class MMDeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mmDealId;

    @ManyToOne
    @JoinColumn(name = "cpid")
    private Counterparty counterparty;

    private String buySell;
    private Double principal;
    private String currency;
    private Double rate;
    private LocalDate tradeDate;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private String status;
}
