/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 15 - 12 - 2020
 */

package com.eyob.IFRSSystem.web;

import com.eyob.IFRSSystem.domain.GeneralLedger;
import com.eyob.IFRSSystem.domain.customer;
import com.eyob.IFRSSystem.domain.product;
import com.eyob.IFRSSystem.repository.GeneralLedgerRepository;
import com.eyob.IFRSSystem.repository.UserRepository;
import com.eyob.IFRSSystem.web.dto.AccountUpdateDto;
import com.eyob.IFRSSystem.web.dto.BankAccountRegistrationDto;
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
@RequestMapping(value = {"userPages", "/pages/userPages", "" })
public class BankingController {
    @Autowired
    UserRepository userR;

    @Autowired
    GeneralLedgerRepository generalLedgerRepository;

    @GetMapping("/Bankingmain")
    private String getAllAccounts(Model model, Principal principal){
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

        List<GeneralLedger> accounts = (List<GeneralLedger>) generalLedgerRepository.findAll();

        model.addAttribute("accounts", accounts);
        model.addAttribute("productSize", generalLedgerRepository.count());
        model.addAttribute("image", imageFile);



        return "pages/userPages/Bankingmain";
    }


    @ModelAttribute("newAccount")
    public BankAccountRegistrationDto newAccount() {
        return new BankAccountRegistrationDto();
    }

    @GetMapping("/addAccount")
    private String addAccount1(@ModelAttribute("newAccount") @Valid BankAccountRegistrationDto newAccount, BindingResult result){
        GeneralLedger newaccount = new GeneralLedger();
        newaccount.setAccount(newAccount.getAccount());
        newaccount.setAccount_type(newAccount.getAccount_type());
        newaccount.setAccount_description(newAccount.getAccount_description());
        newaccount.setCurrent_amount(newAccount.getBegining_balance());
        generalLedgerRepository.save(newaccount);

        return "redirect:/Bankingmain";
    }

    @PostMapping("/addAccount")
    private String addAccount(@ModelAttribute("newAccount") @Valid BankAccountRegistrationDto newAccount, BindingResult result){
        GeneralLedger newaccount = new GeneralLedger();
        newaccount.setAccount(newAccount.getAccount());
        newaccount.setAccount_type(newAccount.getAccount_type());
        newaccount.setAccount_description(newAccount.getAccount_description());
        newaccount.setCurrent_amount(newAccount.getBegining_balance());
        generalLedgerRepository.save(newaccount);

        return "redirect:/Bankingmain";
    }

    @GetMapping("/AccountEdit/{name}")
    private String updateAccountForm(@PathVariable("name") Long name, Model model, Principal principal){

        Optional<GeneralLedger> chu = generalLedgerRepository.findById(name);

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


        model.addAttribute("id",name);
        model.addAttribute("accountEdited", chu);
        return "pages/userPages/accountEdit";
    }

    @PostMapping("/AccountEdit/{name}")
    private String UpdateAccount(@PathVariable Long name, Model model, AccountUpdateDto accountEdited){
        GeneralLedger UpdateAccount = generalLedgerRepository.findById(name).get();
        UpdateAccount.setAccount(accountEdited.getAccount());
        UpdateAccount.setAccount_type(accountEdited.getAccount_type());
        UpdateAccount.setAccount_description(accountEdited.getAccount_description());
        UpdateAccount.setCurrent_amount(accountEdited.getCurrent_amount());

        generalLedgerRepository.save(UpdateAccount);

        return "redirect:/Bankingmain";
    }

    @GetMapping("/AccountDelete/{name}")
    private String deleteAccount(@PathVariable("name") String name){
        Optional<GeneralLedger> custom = generalLedgerRepository.findByAccount(name);
            generalLedgerRepository.delete(custom.get());
        return "redirect:/Bankingmain";
    }



}
