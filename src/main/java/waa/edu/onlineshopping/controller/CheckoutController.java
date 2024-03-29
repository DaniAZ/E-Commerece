package waa.edu.onlineshopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import waa.edu.onlineshopping.domain.*;
import waa.edu.onlineshopping.service.*;
import waa.edu.onlineshopping.util.EmailNotification;
//import waa.edu.onlineshopping.util.EmailNotification;


import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {

    @Autowired
    CredentialService credentialService;
    @Autowired
    BuyerService buyerService;
    @Autowired
    CartItemService cartItemService;
    @Autowired
    CartService cartService;
    @Autowired
    OrderService orderService;
    @Autowired
    EmailNotification emailNotification;

    @Secured("ROLE_BUYER")
    @RequestMapping("buyer/checkout")
    public String checkout(@ModelAttribute("newBillingAddress") BillingAddress billingAddress, @RequestParam("id") Long cartId,
                           @RequestParam(value = "missingRequiredField", required = false) boolean missingRequiredField,
                           Principal principal, Model model) {
        Credential credential = credentialService.findByEmail(principal.getName());
        Buyer buyer = buyerService.findByCredential(credential);


        if (cartId != buyer.getCart().getId()) {
            return "badRequestPage";
        }
        List<CartItem> cartItemList = cartService.findByCart(buyer.getCart());

        for (CartItem cartItem : cartItemList) {
            if (cartItem.getProduct().getQuantity() < cartItem.getQuantity()) {
                model.addAttribute("notEnoughStock", true);
                return "forward:/buyer/shoppingCart/cart";
            }
        }
        List<Address> buyerAddresses = buyer.getAddresses();
        Payment buyerPayment = buyer.getPayment();


        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        model.addAttribute("buyerShippingList", buyerAddresses);

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        model.addAttribute("payment", buyerPayment);
        /// model.addAttribute("newBillingAddress",new BillingAddress());


        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        if (buyerPayment == null) {
            model.addAttribute("emptyPayment", true);
        } else {
            model.addAttribute("emptyPayment", false);
        }

        if (buyerAddresses.size() == 0) {
            model.addAttribute("emptyShippingList", true);
        } else {
            model.addAttribute("emptyShippingList", false);
        }

        model.addAttribute("cartItemList", cartItemList);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        model.addAttribute("shoppingCart", buyer.getCart());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        // System.out.println(buyer.getCart());
        model.addAttribute("classActiveShipping", true);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");


        if (missingRequiredField) {
            model.addAttribute("missingRequiredField", true);
        }

        return "buyer/checkout";
    }
    @Secured("ROLE_BUYER")
    @PostMapping("buyer/checkout")
    public String checkoutPost(@ModelAttribute("newBillingAddress") BillingAddress billingAddress,
                               @ModelAttribute("shippingMethod") String shippingMethod, Principal principal, Model model) {
//           if(result.hasErrors()){
//               return "buyer/checkout";
//           }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        Credential credential = credentialService.findByEmail(principal.getName());
        Buyer buyer = buyerService.findByCredential(credential);

        List<CartItem> cartItemList = cartService.findByCart(buyer.getCart());
        model.addAttribute("cartItemList", cartItemList);

//         Cart cart=user.getBuyer().getCart();
        Cart cart = buyer.getCart();

        Orders order = orderService.createOrder(cart, billingAddress, buyer);

//          StringBuilder stringBuilder=null;
//        for(Orders orders: orderService.findAll()){
//              if(orders.getBuyer().getId()==buyer.getId())
//             stringBuilder.append(orders.getId());
//        }
        model.addAttribute("order_id", order.getId());
        emailNotification.sendEmail(principal.getName(), "Thank you for shopping on our book store. We hope you had a good time with our service!",
                "ordernumber" + order.getId());
        cartService.clearCart(cart);

        LocalDate today = LocalDate.now();
        LocalDate estimatedDeliveryDate;

        if (shippingMethod.equals("groundShipping")) {
            estimatedDeliveryDate = today.plusDays(5);
        } else {
            estimatedDeliveryDate = today.plusDays(3);
        }

        model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);


        return "buyer/confirmationPage";
    }

}
