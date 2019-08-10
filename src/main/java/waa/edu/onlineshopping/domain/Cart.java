package waa.edu.onlineshopping.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
   // @Cascade(CascadeType.ALL)
    @OneToMany(mappedBy="cart",  fetch=FetchType.LAZY)
    // @JsonIgnore
    private List<CartItem> cartItems;

    private Boolean status;

    @Cascade(CascadeType.ALL)
    @OneToOne
    private Buyer buyer;

    private LocalDate orderdate;

    private double grandTotal;

    public void addCartItem(CartItem item) {
        if (cartItems==null) {
            cartItems = new ArrayList<CartItem>();
            cartItems.add(item);

        } else {
            cartItems.add(item);

        }
      //  return cartItems;
    }
}