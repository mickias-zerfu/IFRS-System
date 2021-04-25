/*
 * Copyright (c) 2021
 * Created by Eyob Amare on 17 - 1 - 2021
 */

package com.eyob.IFRSSystem.web;
import com.eyob.IFRSSystem.domain.User;
import com.eyob.IFRSSystem.domain.customer;
import com.eyob.IFRSSystem.domain.entity;
import com.eyob.IFRSSystem.domain.reviewer;
import com.eyob.IFRSSystem.repository.*;
import com.eyob.IFRSSystem.web.dto.CustomerRegistrationDto;
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

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Controller
@RequestMapping
public class AdminController {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    BillRepository billRepository;

    @Autowired
    LedgerRepository ledgerRepository;

    @Autowired
    JournalentryRepository journalentryRepository;

    @Autowired
    TransactionRepository transactionRepository;

    boolean issmw = false;
    public void logeduser( Principal principal, Model model) {

        String role = userRepository.findByEmail(principal.getName()).getRoles().toString();
        if (!role.contains("ADMIN")) {
            issmw = true;
        }

        String firstName = userRepository.findByEmail(principal.getName()).getFirstName();
        String lastName = userRepository.findByEmail(principal.getName()).getLastName();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else
            model.addAttribute("role", "Accountant");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
    }


    @GetMapping(value = {"admin", "/admin"})
    public String admin(Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";
        }
        String userRole = "ROLE_USER";
        String reviewer = "REVIEWER";
        ArrayList<User> userRequest = new ArrayList<>();
        ArrayList<User> userPassRequest = new ArrayList<>();
        int totalAccountant = 0;
        int totalReviewer = 0;
        for (User user : userRepository.findAll()){
            if (user.getNotify())
                userRequest.add(user);
            if(user.getForget())
                userPassRequest.add(user);
            if (user.getRoles().toString().contains("ROLE_USER") && user.getEnabled().equals(true))
                totalAccountant++;
            else if (user.getRoles().toString().contains("REVIEWER") && user.getEnabled().equals(true))
                totalReviewer++;
        }

        model.addAttribute("userPassRequest",userPassRequest);
        model.addAttribute("ROLE_USER",userRole);
        model.addAttribute("REVIEWER", reviewer);
        model.addAttribute("totalAccountant", totalAccountant);
        model.addAttribute("totalReviewer",totalReviewer);
        model.addAttribute("totalEntity",entityRepository.count());
        model.addAttribute("userRequest", userRequest);

        return "pages/AdminPages/index";
    }

    @GetMapping("/{user_id}/accept")
    private String acceptUser(@PathVariable("user_id") Long user_id) {
        User user = userRepository.findById(user_id).get();
        user.setEnabled(true);
        user.setApproved(true);
        user.setNotify(false);
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/{user_id}/reject")
    private String rejectUser(@PathVariable("user_id") Long user_id) {
        User user = userRepository.findById(user_id).get();
        user.setEnabled(false);
        user.setApproved(false);
        user.setNotify(false);
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping({"/admin/AccountantList"})
    public String UsersAccuntant(Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";
        }

        ArrayList<User> users = new ArrayList<>();
        for (User user: userRepository.findAll()){
            if (!user.getRoles().toString().contains("REVIEWER"))
                users.add(user);
        }
        model.addAttribute("users", users);

        return "pages/AdminPages/AccountantList";
    }


    @GetMapping({"/admin/ReviewerList"})
    public String UserReviewer(Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";

        }

        ArrayList<User> users = new ArrayList<>();
        for (User user: userRepository.findAll()){
            if (user.getRoles().toString().contains("REVIEWER"))
                users.add(user);
        }
        model.addAttribute("users", users);

        return "pages/AdminPages/ReviewerList";
    }

    @PostMapping("/admin/approve/{user_id}")
    private String approve(@PathVariable("user_id") Long user_id, Model model,
                                @ModelAttribute("request") request request) {
        User user = userRepository.findById(user_id).get();
        reviewer reviewer = reviewerRepository.findByEmail(user.getEmail());

        System.out.println(user.getEmail());

        if(reviewer.getApprove())
            reviewer.setApprove(false);
        else
            reviewer.setApprove(true);

        reviewerRepository.save(reviewer);
        model.addAttribute("user_id",user_id);

        return "redirect:/admin/ReviewerList/{user_id}";
    }



    @GetMapping({"/admin/entities"})
    public String Entities(Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";
        }

        model.addAttribute("entities", entityRepository.findAll());

        return "pages/AdminPages/entities";
    }

    @GetMapping({"/admin/entities/{entity_id}"})
    public String entity(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";
        }

        entity entity = entityRepository.findById(entity_id).get();
        ArrayList<String> users= new ArrayList<>();

        for(User user: userRepository.findAll()){
            String temp = user.getFirstName()+" "+user.getLastName();
            users.add(temp);
        }

        String name = userRepository.findById(entity.getAdmin_id()).get().getFirstName() + " " +
                userRepository.findById(entity.getAdmin_id()).get().getLastName();
        System.out.println(name);
        entity.setCity(name);
        model.addAttribute("entity", entity);
        model.addAttribute("users", users);

        return "pages/AdminPages/entityDetail";
    }

    @PostMapping("/admin/changeAdmin/{entity_id}")
    private String UpdateEntity(@PathVariable("entity_id") String entity_id, Model model,
                                  @ModelAttribute("entity") entity entity) {
        entity entity1 = entityRepository.findById(entity_id).get();
        String[] name = entity.getCity().split(" ");
        for (User user: userRepository.findAll()){
            if(user.getFirstName().equals(name[0]) && user.getLastName().equals(name[1])){
                entity1.setAdmin_id(user.getId());
            }
        }

        entityRepository.save(entity1);
        model.addAttribute("entity_id",entity_id);
        return "redirect:/admin/entities/{entity_id}";
    }


    @GetMapping({"/admin/reports"})
    public String Reports(Model model) {
        return "pages/AdminPages/reports";
    }

    @GetMapping({"/admin/AccountantList/{user_id}"})
    public String detailAccountants(@PathVariable("user_id") Long user_id, Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";
        }
        User user = userRepository.findById(user_id).get();

        ArrayList<entity> entities = new ArrayList<>();
        for(entity entity: entityRepository.findAll()){
            if(entity.getAdmin_id().equals(user_id))
                entities.add(entity);
        }

        byte[] image2 = user.getData();
        String image1 = Base64.getMimeEncoder().encodeToString(image2);
        model.addAttribute("entities", entities);
        model.addAttribute("user", user);
        model.addAttribute("image1", image1);

        return "pages/AdminPages/AccountantDetail";
    }

    @GetMapping({"/admin/ReviewerList/{user_id}"})
    public String detailReviewers(@PathVariable("user_id") Long user_id, Model model, Principal principal) {
        logeduser( principal, model);
        if (issmw) {
            issmw = false;
            return "pages/userPages/error";
        }
        User user = userRepository.findById(user_id).get();

        reviewer reviewer = reviewerRepository.findByEmail(user.getEmail());

        byte[] image2 = user.getData();
        String image1 = Base64.getMimeEncoder().encodeToString(image2);

        model.addAttribute("request", reviewer);
        model.addAttribute("user", user);
        model.addAttribute("image1", image1);



        return "pages/AdminPages/ReviewerDetail";
    }

    @Autowired
    ReviewerRepository reviewerRepository;

    @PostMapping("/admin/enableDisable/{user_id}")
    private String UpdateCustomer(@PathVariable("user_id") Long user_id, Model model,
                                  @ModelAttribute("user") User user) {
        User user1 = userRepository.findById(user_id).get();
        if(user1.getEnabled())
            user1.setEnabled(false);
        else
            user1.setEnabled(true);

        userRepository.save(user1);
        model.addAttribute("user_id",user_id);
        if (user1.getRoles().toString().contains("ROLE_USER"))
            return "redirect:/admin/AccountantList/{user_id}";
        else
            return "redirect:/admin/ReviewerList/{user_id}";
    }

    @Autowired
    private JavaMailSender javaMailSender;

    void sendEmail() {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("to_1@gmail.com", "to_2@gmail.com", "to_3@yahoo.com");

        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");

        javaMailSender.send(msg);

    }


}
