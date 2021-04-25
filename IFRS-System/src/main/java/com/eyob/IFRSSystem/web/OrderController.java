/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.web;

import com.eyob.IFRSSystem.domain.*;
import com.eyob.IFRSSystem.repository.*;
import com.eyob.IFRSSystem.web.dto.CustomerRegistrationDto;
import com.eyob.IFRSSystem.web.dto.OrderRegistrationDto;
import com.eyob.IFRSSystem.web.dto.OrderUpdateDto;
import com.eyob.IFRSSystem.web.dto.sample;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = {"userPages", "/entity" ,"/pages/userPages", ""})
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    UserRepository userR;


    /*@GetMapping("/addOrder")
    private String addOrder(@ModelAttribute("newOrder") @Valid OrderRegistrationDto newOrder,
                            BindingResult result, Model model, Principal principal) {

        String firstName = userR.findByEmail(principal.getName()).getFirstName();
        String lastName = userR.findByEmail(principal.getName()).getLastName();
        String role = userR.findByEmail(principal.getName()).getRoles().toString();

        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);

        byte[] image = userR.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);

        model.addAttribute("image", imageFile);

        OrderRegistrationDto temporderRegistrationDto = new OrderRegistrationDto();
        for (int i = 0; i < productRepository.count(); i++) {
            temporderRegistrationDto.addOrders(new sample());
        }
        model.addAttribute("newOrder", temporderRegistrationDto);



        return "/pages/userPages/newOrder";
    }*/

    @ModelAttribute("newOrder")
    public OrderRegistrationDto newOrder() {
        return new OrderRegistrationDto();
    }
/*
    @PostMapping("/addOrder")
    private String addOrder1(@ModelAttribute("newOrder") OrderRegistrationDto newOrder) {

        System.out.println(newOrder.getOrdersList().size());

        order neworder = new order();
        neworder.setCustomer_id(newOrder.getCustomer1());
        neworder.setOrder_date(newOrder.getOrder_date1());
        neworder.setShipment_date(newOrder.getShipment_date1());
        neworder.setFreight(newOrder.getFreight1());

        Double totAmmount = 0.0;
        Long order_id = orderRepository.save(neworder).getOrder_id();

        for (int i = 0; i < newOrder.getOrdersList().size(); i++) {

            orderProduct pro = new orderProduct();
            pro.setProductIdentity(new ProductIdentity(order_id, newOrder.getOrdersList().get(i).getName()));
            System.out.println(pro.getProductIdentity().getName());
            Double quantity = newOrder.getOrdersList().get(i).getQuantity();
            pro.setQuantity(quantity);
            product prod1 = productRepository.findById(newOrder.getOrdersList().get(i).getName()).get();
            totAmmount = totAmmount + (quantity * prod1.getUnit_price());

            orderProductRepository.save(pro);
        }

        order temp = orderRepository.findById(order_id).get();
        temp.setAmount(totAmmount);
        orderRepository.save(temp);

        return "redirect:/customerSale";
    }
*/

    @GetMapping("/orderEdit/{order_id}")
    private String updateOrderForm(@PathVariable("order_id") Long order_id, Model model, Principal principal) {
        order che = orderRepository.findById(order_id).get();
        Long id = order_id;
        String firstName = userR.findByEmail(principal.getName()).getFirstName();
        String lastName = userR.findByEmail(principal.getName()).getLastName();
        String role = userR.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);

        byte[] image = userR.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);

        model.addAttribute("image", imageFile);
        model.addAttribute("id", id);
        model.addAttribute("order", che);

        return "pages/userPages/orderEdit";
    }


    @GetMapping("/orderEdit1/{order_id}")
    private String updateOrderFormProducts(@PathVariable("order_id") Long order_id, Model model, Principal principal) {

        List<orderProduct> chu = orderProductRepository.findByProductIdentity_id(order_id);


        Long id = order_id;
        String firstName = userR.findByEmail(principal.getName()).getFirstName();
        String lastName = userR.findByEmail(principal.getName()).getLastName();
        String role = userR.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);

        byte[] image = userR.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);

        model.addAttribute("image", imageFile);
        model.addAttribute("id", id);
        model.addAttribute("orders", chu);

        return "pages/userPages/orderEdit1";
    }

    @PostMapping("/orderEdit1/{order_id}")
    private String orderEdit1(@PathVariable Long order_id, Model model, OrderUpdateDto orderUpdateDto) {


        return "redirect:/customerSale";
    }


    @PostMapping("/orderEdit/{order_id}")
    private String UpdateOrder(@PathVariable Long order_id, Model model, OrderUpdateDto orderUpdateDto) {


        return "redirect:/customerSale";
    }


    @GetMapping("/orderDelete/{order_id}")
    private String deleteOrder(@PathVariable("order_id") Long order_id) {
        Optional<order> custom = orderRepository.findById(order_id);

        if (custom.isPresent()) {
            orderRepository.delete(custom.get());
            List<orderProduct> chu = orderProductRepository.findByProductIdentity_id(order_id);
            for (orderProduct c : chu) {
                orderProductRepository.delete(c);
            }
        } else {
            System.err.println("not found");
        }
        return "redirect:/customerSale";
    }

    @GetMapping("/sellInvoice/{order_id}")
    private String sellsInvoice(@PathVariable("order_id") Long order_id, Model model, Principal principal) {

        List<orderProduct> chu = orderProductRepository.findByProductIdentity_id(order_id);
        order che = orderRepository.findById(order_id).get();

        double totalAmount = 0;

        for(orderProduct c : chu){
            c.setTax(productRepository.findById(c.getProductIdentity().getName()).get().getTax());
            c.setUnit_price(productRepository.findById(c.getProductIdentity().getName()).get().getUnit_price());
            c.setAmount((c.getQuantity() * c.getUnit_price()) + ((c.getQuantity() * c.getUnit_price()) * (c.getTax() / 100)));
            totalAmount = totalAmount + c.getAmount();
        }

        che.setAmount(totalAmount);

        Long id = order_id;
        String firstName = userR.findByEmail(principal.getName()).getFirstName();
        String lastName = userR.findByEmail(principal.getName()).getLastName();
        String role = userR.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);

        byte[] image = userR.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);



        model.addAttribute("image", imageFile);
        model.addAttribute("id", id);
        model.addAttribute("orders", chu);
        model.addAttribute("order",che);

        return "pages/userPages/sellInvoice";
    }

    @PostMapping("/sellInvoice/{order_id}")
    private String sellInvoice(@PathVariable Long order_id, Model model, OrderUpdateDto orderUpdateDto) {


        return "redirect:/customerSale";
    }

}