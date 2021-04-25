/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.web;

import com.eyob.IFRSSystem.domain.customer;
import com.eyob.IFRSSystem.domain.product;
import com.eyob.IFRSSystem.repository.ProductRepository;
import com.eyob.IFRSSystem.repository.UserRepository;
import com.eyob.IFRSSystem.web.dto.CustomerUpdateDto;
import com.eyob.IFRSSystem.web.dto.ProductRegistrationDto;
import com.eyob.IFRSSystem.web.dto.ProductUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = {"userPages","/entity", "/pages/userPages", "" })
public class ProductController {

    @Autowired
    private ProductRepository repository;



    @Autowired
    UserRepository userR;

    @GetMapping("/inventoryServices")
    private String getAllProducts(Model model, Principal principal){
        String firstName = userR.findByEmail(principal.getName()).getFirstName();
        String lastName = userR.findByEmail(principal.getName()).getLastName();
        String role = userR.findByEmail(principal.getName()).getRoles().toString();

        if(role.contains("ADMIN")){
            model.addAttribute("role","Manager" );
        }
        else
            model.addAttribute("role","Accountant" );
        model.addAttribute("firstName", firstName+" "+lastName);

        byte[] image = userR.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);


        List<product> products = (List<product>) repository.findAll();


        model.addAttribute("products", products);
        model.addAttribute("productSize", repository.count());
        model.addAttribute("image", imageFile);
        return "pages/userPages/inventoryServices";

    }

    @ModelAttribute("newProduct")
    public ProductRegistrationDto newProduct() {
        return new ProductRegistrationDto();
    }

    @GetMapping("/addProduct")
    private String addProduct1(@ModelAttribute("newProduct") @Valid ProductRegistrationDto newProduct, BindingResult result){
        product newproduct = new product();
        newproduct.setName(newProduct.getName());
        newproduct.setUnit_price(Double.parseDouble(newProduct.getUnit_price()));
        newproduct.setTax(Double.parseDouble(newProduct.getTax()));
        newproduct.setQuantity(Double.parseDouble(newProduct.getQuantity()));
        repository.save(newproduct);
        return "redirect:/inventoryServices";
    }

    @PostMapping("/addProduct")
    private String addProduct(@ModelAttribute("newProduct") @Valid ProductRegistrationDto newProduct, BindingResult result){
        product newproduct = new product();
        newproduct.setName(newProduct.getName());
        newproduct.setUnit_price(Double.parseDouble(newProduct.getUnit_price()));
        newproduct.setTax(Double.parseDouble(newProduct.getTax()));
        newproduct.setQuantity(Double.parseDouble(newProduct.getQuantity()));
        repository.save(newproduct);
        return "redirect:/inventoryServices";
    }

    @GetMapping("/productEdit/{name}")
    private String updateCustomerForm(@PathVariable("name") String name, Model model, Principal principal){
        Optional<product> che = repository.findById(name);
        String id = name;
        String firstName = userR.findByEmail(principal.getName()).getFirstName();
        String lastName = userR.findByEmail(principal.getName()).getLastName();
        String role = userR.findByEmail(principal.getName()).getRoles().toString();
        if(role.contains("ADMIN")){
            model.addAttribute("role","Manager" );
        }
        else
            model.addAttribute("role","Accountant" );
        model.addAttribute("firstName", firstName+" "+lastName);

        byte[] image = userR.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);

        model.addAttribute("image",imageFile);


        model.addAttribute("id",id);
        model.addAttribute("custom", che);
        return "pages/userPages/productEdit";
    }

    @PostMapping("/productEdit/{name}")
    private String UpdateProduct(@PathVariable String name, Model model, ProductUpdateDto custom){

        product UpdateProduct = repository.findById(name).get();

        UpdateProduct.setName(custom.getName());
        UpdateProduct.setUnit_price(custom.getUnit_price());
        UpdateProduct.setTax(custom.getTax());
        UpdateProduct.setQuantity(custom.getQuantity());
        repository.save(UpdateProduct);

        return "redirect:/inventoryServices";
    }

    @GetMapping("/productDelete/{name}")
    private String deleteProduct(@PathVariable("name") String name){
        Optional<product> custom = repository.findById(name);
        if(custom.isPresent()){
            repository.delete(custom.get());
        }else{
            System.err.println("not found");
        }
        return "redirect:/inventoryServices";
    }

}
