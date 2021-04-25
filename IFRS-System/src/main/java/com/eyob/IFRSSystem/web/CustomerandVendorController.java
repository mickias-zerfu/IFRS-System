/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.web;

import com.eyob.IFRSSystem.domain.*;
import com.eyob.IFRSSystem.repository.*;
import com.eyob.IFRSSystem.web.dto.CustomerRegistrationDto;

import com.eyob.IFRSSystem.web.dto.VendorRegistrationDto;

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
@RequestMapping(value = {"userPages","/entity", "/pages/userPages", ""})
public class CustomerandVendorController {

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    boolean issmw = false;
    public void logeduser (String entity_id, Principal principal, Model model){
        String firstName = userRepository.findByEmail(principal.getName()).getFirstName();
        String lastName = userRepository.findByEmail(principal.getName()).getLastName();
        String role = userRepository.findByEmail(principal.getName()).getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
        model.addAttribute("entity_id", entity_id);
        Long entity_admin = Long.valueOf(1116105615);
        for (entity entity: entityRepository.findAll()){
            if(entity.getEntity_id().equals(entity_id))
                entity_admin =  entity.getAdmin_id();
        }

        if( !userRepository.findByEmail(principal.getName()).getId().equals(entity_admin))
            issmw = true;

    }

    @GetMapping("{entity_id}/customer")
    private String getAllCustomer(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged in user
        logeduser(entity_id,principal,model);
        if (issmw){
            issmw = false;
            return "pages/userPages/error";
        }
        ///******************
        ArrayList<customer> customers = new ArrayList<>();
        for (customer customer : customerRepository.findAll()){
            if (customer.getCustomer_id().contains(entity_id) && !customer.getCustomer_id().contains("self"))
                customers.add(customer);
        }
        model.addAttribute("entity_id", entity_id);
        model.addAttribute("customers", customers);
        model.addAttribute("customerSize", customers.size());

        return "pages/userPages/customer";

    }


    @GetMapping("{entity_id}/customerEdit/{customer_id}")
    private String updateCustomerForm(@PathVariable("customer_id") String customer_id, @PathVariable("entity_id") String entity_id,
            Model model, Principal principal) {
        customer customer = customerRepository.findById(customer_id).get();

        logeduser(entity_id,principal,model);
        if (issmw){
            issmw = false;
            return "pages/userPages/error";
        }

        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        System.out.println("get");
        System.out.println(entity_id);
        model.addAttribute("image", imageFile);
        model.addAttribute("entity_id", entity_id);
        model.addAttribute("customer_id", customer_id);
        model.addAttribute("customer", customer);
        return "pages/userPages/customerEdit";
    }


    @PostMapping("{entity_id}/customerEdit/{customer_id}")
    private String UpdateCustomer(@PathVariable("entity_id") String entity_id, @PathVariable("customer_id") String customer_id,
                                  Model model, CustomerRegistrationDto customerRegistrationDto) {

        customer UpdateCustomer = customerRepository.findById(customer_id).get();
        Date date = new Date(System.currentTimeMillis());
        UpdateCustomer.setUpdated(date);
        UpdateCustomer.setCustomer_name(customerRegistrationDto.getCustomer_name());
        UpdateCustomer.setAddress_1(customerRegistrationDto.getAddress_1());
        UpdateCustomer.setAddress_2(customerRegistrationDto.getAddress_2());
        UpdateCustomer.setPhone(customerRegistrationDto.getPhone());
        UpdateCustomer.setEmail(customerRegistrationDto.getEmail());
        UpdateCustomer.setState(customerRegistrationDto.getState());
        UpdateCustomer.setCity(customerRegistrationDto.getCity());
        UpdateCustomer.setCountry(customerRegistrationDto.getCountry());
        UpdateCustomer.setZip_code(customerRegistrationDto.getZip_code());
        UpdateCustomer.setWebsite(customerRegistrationDto.getWebsite());

        customerRepository.save(UpdateCustomer);
        System.out.println(entity_id);
        model.addAttribute("entity_id",entity_id);
        return "redirect:/{entity_id}/customer";
    }

    @GetMapping("{entity_id}/customerDelete/{customer_id}")
    private String deleteCustomer(@PathVariable("entity_id") String entity_id, @PathVariable("customer_id") String customer_id,
                                  Model model) {
        customer customer = customerRepository.findById(customer_id).get();
        customerRepository.delete(customer);
        
        model.addAttribute("entity_id", entity_id);
        return "redirect:/{"+entity_id+"}/customer";
    }

    @ModelAttribute("customer")
    public CustomerRegistrationDto newCustomer() {
        return new CustomerRegistrationDto();
    }

    @GetMapping("/{entity_id}/addCustomer")
    private String add(@PathVariable("entity_id") String entity_id, @ModelAttribute("customer") CustomerRegistrationDto
            customerRegistrationDto, Model model,BindingResult result, Principal principal) {

        ///****** this part is always logged in user
        logeduser(entity_id,principal,model);
        if (issmw){
            issmw = false;
            return "pages/userPages/error";
        }
        ///******************

        model.addAttribute("entity_id",entity_id);
        return "pages/userPages/newCustomer";
    }

    @PostMapping("/{entity_id}/addCustomer")
    private String add1(@PathVariable("entity_id") String entity_id,@ModelAttribute("customer") @Valid CustomerRegistrationDto customerRegistrationDto, BindingResult result) {

        customer UpdateCustomer = new customer();
        Date date = new Date(System.currentTimeMillis());
        UpdateCustomer.setCreated(date);
        UpdateCustomer.setCustomer_name(customerRegistrationDto.getCustomer_name());
        UpdateCustomer.setAddress_1(customerRegistrationDto.getAddress_1());
        UpdateCustomer.setAddress_2(customerRegistrationDto.getAddress_2());
        UpdateCustomer.setPhone(customerRegistrationDto.getPhone());
        UpdateCustomer.setEmail(customerRegistrationDto.getEmail());
        UpdateCustomer.setState(customerRegistrationDto.getState());
        UpdateCustomer.setCity(customerRegistrationDto.getCity());
        UpdateCustomer.setCountry(customerRegistrationDto.getCountry());
        UpdateCustomer.setZip_code(customerRegistrationDto.getZip_code());
        UpdateCustomer.setWebsite(customerRegistrationDto.getWebsite());
        UpdateCustomer.setEntity_id(entity_id);
        UpdateCustomer.setCustomer_id(entity_id+"_customer_"+getAlphaNumericString(5));

        customerRepository.save(UpdateCustomer);
        return "redirect:/{entity_id}/customer";
    }

    public static String getAlphaNumericString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    /***********************************************************************************************
     *
     *                      Vendor
     *
     *
     **************************************************************/

    @GetMapping("{entity_id}/vendor")
    private String getAllVendor(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {

        ///****** this part is always logged in user
        logeduser(entity_id,principal,model);
        if (issmw){
            issmw = false;
            return "pages/userPages/error";
        }
        ///******************
        ArrayList<vendor> vendors = new ArrayList<>();
        for(vendor vendor: vendorRepository.findAll()){
            if(vendor.getVendor_id().contains(entity_id) && !vendor.getVendor_id().contains("self"))
                vendors.add(vendor);
        }


        model.addAttribute("entity_id", entity_id);
        model.addAttribute("vendors", vendors);
        model.addAttribute("vendorSize", vendors.size());

        return "pages/userPages/vendor";

    }


    @GetMapping("{entity_id}/vendorEdit/{vendor_id}")
    private String updateVendorForm(@PathVariable("vendor_id") String vendor_id, @PathVariable("entity_id") String entity_id,
                                      Model model, Principal principal) {
        vendor vendor = vendorRepository.findById(vendor_id).get();

        logeduser(entity_id,principal,model);
        if (issmw){
            issmw = false;
            return "pages/userPages/error";
        }

        model.addAttribute("entity_id", entity_id);
        model.addAttribute("vendor_id", vendor_id);
        model.addAttribute("vendor", vendor);
        return "pages/userPages/vendorEdit";
    }

    @PostMapping("{entity_id}/vendorEdit/{vendor_id}")
    private String UpdateVendor(@PathVariable("vendor_id") String vendor_id, @PathVariable("entity_id") String entity_id, Model model, VendorRegistrationDto vendorRegistrationDto) {

        vendor UpdateVendor = vendorRepository.findById(vendor_id).get();
        Date date = new Date(System.currentTimeMillis());
        UpdateVendor.setUpdated(date);
        UpdateVendor.setVendor_name(vendorRegistrationDto.getVendor_name());
        UpdateVendor.setAddress_1(vendorRegistrationDto.getAddress_1());
        UpdateVendor.setAddress_2(vendorRegistrationDto.getAddress_2());
        UpdateVendor.setPhone(vendorRegistrationDto.getPhone());
        UpdateVendor.setEmail(vendorRegistrationDto.getEmail());
        UpdateVendor.setState(vendorRegistrationDto.getState());
        UpdateVendor.setCity(vendorRegistrationDto.getCity());
        UpdateVendor.setCountry(vendorRegistrationDto.getCountry());
        UpdateVendor.setZip_code(vendorRegistrationDto.getZip_code());
        UpdateVendor.setWebsite(vendorRegistrationDto.getWebsite());

        vendorRepository.save(UpdateVendor);

        return "redirect:/{entity_id}/vendor";
    }

    @ModelAttribute("vendor")
    public VendorRegistrationDto newVendor() {
        return new VendorRegistrationDto();
    }

    @GetMapping("/{entity_id}/addVendor")
    private String addVendor(@PathVariable("entity_id") String entity_id, @ModelAttribute("vendor") VendorRegistrationDto
            vendorRegistrationDto, Model model,BindingResult result, Principal principal) {

        ///****** this part is always logged in user
        logeduser(entity_id,principal,model);
        if (issmw){
            issmw = false;
            return "pages/userPages/error";
        }
        ///******************

        model.addAttribute("entity_id",entity_id);
        return "pages/userPages/newVendor";
    }

    @PostMapping("/{entity_id}/addVendor")
    private String addVendor(@PathVariable("entity_id") String entity_id,@ModelAttribute("vendor") @Valid VendorRegistrationDto
            vendorRegistrationDto, Model model, BindingResult result) {

        vendor UpdateVendor = new vendor();
        Date date = new Date(System.currentTimeMillis());
        UpdateVendor.setCreated(date);
        UpdateVendor.setVendor_name(vendorRegistrationDto.getVendor_name());
        UpdateVendor.setAddress_1(vendorRegistrationDto.getAddress_1());
        UpdateVendor.setAddress_2(vendorRegistrationDto.getAddress_2());
        UpdateVendor.setPhone(vendorRegistrationDto.getPhone());
        UpdateVendor.setEmail(vendorRegistrationDto.getEmail());
        UpdateVendor.setState(vendorRegistrationDto.getState());
        UpdateVendor.setCity(vendorRegistrationDto.getCity());
        UpdateVendor.setCountry(vendorRegistrationDto.getCountry());
        UpdateVendor.setZip_code(vendorRegistrationDto.getZip_code());
        UpdateVendor.setWebsite(vendorRegistrationDto.getWebsite());
        UpdateVendor.setEntity_id(entity_id);
        UpdateVendor.setVendor_id(entity_id+"_vendor_"+getAlphaNumericString(5));

        vendorRepository.save(UpdateVendor);
        model.addAttribute("entity_id",entity_id);
        return "redirect:/{entity_id}/vendor";
    }

    @GetMapping("{entity_id}/vendorDelete/{vendor_id}")
    private String deleteVendor(@PathVariable("entity_id") String entity_id, @PathVariable("vendor_id") String vendor_id,
                                  Model model) {
        vendor vendor = vendorRepository.findById(vendor_id).get();
        vendorRepository.delete(vendor);

        model.addAttribute("entity_id", entity_id);
        return "redirect:/{"+entity_id+"}/vendor";
    }


    /********************************************************************************************************************
    @GetMapping("/customerSale")
    private String getAll(Model model, Principal principal) {
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

        model.addAttribute("orders", orderRepository.findAll());
        model.addAttribute("orderSize", orderRepository.count());

        model.addAttribute("customers", repository.findAll());
        model.addAttribute("customerSize", repository.count());
        model.addAttribute("image", imageFile);


        return "pages/userPages/customerSale";

    }

    @ModelAttribute("newCustomer")
    public CustomerRegistrationDto newCustomer() {
        return new CustomerRegistrationDto();
    }

    @GetMapping("/add")
    private String add(@ModelAttribute("newCustomer") @Valid CustomerRegistrationDto newCustomer, BindingResult result) {
        customer newcustomer = new customer();
        newcustomer.setName(newCustomer.getName());
        newcustomer.setAccount_no(newCustomer.getAccount_no());
        newcustomer.setPhone(newCustomer.getPhone());
        newcustomer.setEmail(newCustomer.getEmail());
        newcustomer.setFax(newCustomer.getFax());
        java.sql.Date date = new Date(System.currentTimeMillis());
        newcustomer.setAddress(newCustomer.getAddress());
        newcustomer.setCustomer_since(date);
        repository.save(newcustomer);
        return "redirect:/pages/userPages/list";
    }

    @PostMapping("/add")
    private String add1(@ModelAttribute("newCustomer") @Valid CustomerRegistrationDto newCustomer, BindingResult result) {
        customer newcustomer = new customer();
        newcustomer.setName(newCustomer.getName());
        newcustomer.setAccount_no(newCustomer.getAccount_no());
        newcustomer.setPhone(newCustomer.getPhone());
        newcustomer.setEmail(newCustomer.getEmail());
        newcustomer.setFax(newCustomer.getFax());
        java.sql.Date date = new Date(System.currentTimeMillis());
        newcustomer.setAddress(newCustomer.getAddress());
        newcustomer.setCustomer_since(date);
        repository.save(newcustomer);
        return "redirect:/customerSale";
    }

    @GetMapping("/customerEdit/{customer_id}")
    private String updateCustomerForm(@PathVariable("customer_id") Long customer_id, Model model, Principal principal) {
        Optional<customer> che = repository.findById(customer_id);
        Long id = customer_id;
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
        model.addAttribute("custom", che);
        return "pages/userPages/customerEdit";
    }

    @PostMapping("/customerEdit/{customer_id}")
    private String UpdateCustomer(@PathVariable Long customer_id, Model model, CustomerUpdateDto custom) {

        customer UpdateCustomer = repository.findById(customer_id).get();

        UpdateCustomer.setName(custom.getName());
        UpdateCustomer.setAccount_no(custom.getAccount_no());
        UpdateCustomer.setPhone(custom.getPhone());
        UpdateCustomer.setEmail(custom.getEmail());
        UpdateCustomer.setFax(custom.getFax());
        UpdateCustomer.setAddress(custom.getAddress());
        UpdateCustomer.setCredit(Double.parseDouble(custom.getCredit()));
        repository.save(UpdateCustomer);

        return "redirect:/customerSale";
    }

    @GetMapping("/customerDelete/{customer_id}")
    private String deleteCustomer(@PathVariable("customer_id") Long customer_id) {
        Optional<customer> custom = repository.findById(customer_id);
        if (custom.isPresent()) {
            repository.delete(custom.get());
        } else {
            System.err.println("not found");
        }
        return "redirect:/customerSale";
    }

    /*
     *
     *
     * Order part
     *
     *
     * */
}
