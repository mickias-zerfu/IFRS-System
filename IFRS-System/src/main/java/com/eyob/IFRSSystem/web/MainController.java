/*
 * Copyright (c) 2020
 * Created by Eyob Amare on 11 - 8 - 2020
 */

package com.eyob.IFRSSystem.web;

import com.eyob.IFRSSystem.domain.*;
import com.eyob.IFRSSystem.repository.*;
import com.eyob.IFRSSystem.web.dto.BillRegistrationDto;
import com.eyob.IFRSSystem.web.dto.EntityRegistrationDto;
import com.eyob.IFRSSystem.web.dto.VendorRegistrationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(value = {"", "/entity", "/pages", "/userPages", "/pages/userPages"})
public class MainController {

    boolean issmw = false;

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    VendorRepository vendorRepository;

    @Autowired
    CustomerRepository customerRepository;

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


    @GetMapping(value = "index")
    public String index(Model model) {
        return "index";
    }

    @GetMapping(value = "/")
    public String index2(Model model) {
        return "index";
    }


    @GetMapping(value = {"login", "/login"})
    public String login(Model model) {
        return "login";
    }

    @GetMapping(value = {"forget", "/forget"})
    public String forget(Model model) {
        return "forget";
    }

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostMapping(value = {"forget", "/forget"})
    public String forgetPost(Model model, @ModelAttribute("email") String email) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            return "redirect:/index";
        String temp = getAlphaNumericString(5);
        user.setPassword(encoder.encode(temp));
        sendEmail(email, temp);
        userRepository.save(user);

        return "redirect:/index";
    }

    @Autowired
    private JavaMailSender javaMailSender;
    void sendEmail(String to, String temp) {

        final String username = "eyob512@gmail.com";
        final String password = "eyobamare";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        String newLine = System.getProperty("line.separator");
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Temporary password from IFRS System");
        msg.setText("This is your temporary password:- "+temp+newLine+
                "please do change your password ASAP!");

        javaMailSender.send(msg);
}


 // Login form with error
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }


    public void logeduser(String entity_id, Principal principal, Model model) {
        User user = userRepository.findByEmail(principal.getName());
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else if (role.contains("ROLE_USER"))
            model.addAttribute("role", "Accountant");
        else
            model.addAttribute("role", "Reviewer");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);
        model.addAttribute("entity_id", entity_id);
        Long entity_admin = Long.valueOf(1);
        for (entity entity : entityRepository.findAll()) {
            if (entity.getEntity_id().equals(entity_id))
                entity_admin = entity.getAdmin_id();
        }


        if (!userRepository.findByEmail(principal.getName()).getId().equals(entity_admin) || !user.getApproved()
        || !user.getApproved())
            issmw = true;

        model.addAttribute("user", user);

    }

    @GetMapping(value = {"/{entity_id}", "/{entity_id}/dashboard"})
    public String mmdashboard(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        model.addAttribute("entity_id", entity_id);

        Date date = new Date(System.currentTimeMillis());
        LocalDate ld = date.toLocalDate();
        int currYear = ld.getYear();
        LocalDate thisYear = LocalDate.of(currYear,1,1);
        LocalDate thisYearEnd = LocalDate.of(currYear,12,31);
        Double revenue = 0.0;
        Double texpense = 0.0;
        Double max = 0.0;
        Double[] income = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        Double[] expense = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

        Double[] reciveable = {0.0, 0.0, 0.0, 0.0, 0.0};
        for (invoice invoice : invoiceRepository.findAll()) {
            if (invoice.getInvoice_id().startsWith(entity_id)) {
                if (invoice.getTerms().contains("30")) {
                    reciveable[1] = reciveable[1] + 1;
                } else if (invoice.getTerms().contains("60")) {
                    reciveable[2] = reciveable[2] + 1;
                } else if (invoice.getTerms().contains("90")) {
                    reciveable[3] = reciveable[3] + 1;
                } else if (invoice.getTerms().contains("Due")) {
                    reciveable[0] = reciveable[0] + 1;
                } else
                    reciveable[4] = reciveable[4] + 1;

                if(invoice.getCreated().after(java.sql.Date.valueOf(thisYear))
                        && invoice.getCreated().before(java.sql.Date.valueOf(thisYearEnd))
                        && !invoice.getCustomer_id().contains("self")) {
                    revenue = revenue + invoice.getAmount_due();
                    LocalDate temp = invoice.getCreated().toLocalDate();
                    income[temp.getMonthValue() - 1] = income[temp.getMonthValue() - 1] + invoice.getAmount_due();
                }
            }
        }

        Double[] payable = {0.0, 0.0, 0.0, 0.0, 0.0};
        for (bill bill : billRepository.findAll()) {
            if (bill.getBill_id().startsWith(entity_id)) {
                if (bill.getTerms().contains("30")) {
                    payable[1] = payable[1] + 1;
                } else if (bill.getTerms().contains("60")) {
                    payable[2] = payable[2] + 1;
                } else if (bill.getTerms().contains("90")) {
                    payable[3] = payable[3] + 1;
                } else if (bill.getTerms().contains("Due")) {
                    payable[0] = payable[0] + 1;
                } else
                    payable[4] = reciveable[4] + 1;


                if (bill.getCreated().after(java.sql.Date.valueOf(thisYear)) &&
                        bill.getCreated().before(java.sql.Date.valueOf(thisYearEnd)) &&
                        !bill.getVendor_id().contains("self")) {
                    texpense = texpense + bill.getAmount_due();

                    LocalDate temp = bill.getCreated().toLocalDate();
                    expense[temp.getMonthValue() - 1] = expense[temp.getMonthValue() - 1] + bill.getAmount_due();

                }
            }
        }

        for (int i = 0; i < income.length; i++){
             if (max < income[i] || max < expense[i])
                 max = Math.max(income[i], expense[i]);
        }

        max = max + 10;

        for (int i = 0; i < income.length; i++){
            income[i] = (income[i] * 100) / max;
            expense[i] = (expense[i] * 100) / max;
        }
        model.addAttribute("er", round(revenue/texpense,2));

        model.addAttribute("texpense",texpense);
        model.addAttribute("revenue",revenue);
        model.addAttribute("recData", reciveable);
        model.addAttribute("payData", payable);
        model.addAttribute("incomeData", income);
        model.addAttribute("expenseData", expense);
        model.addAttribute("user",user);
        model.addAttribute("user_id", user.getUser_id());
        return "/pages/dashboard";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Autowired
    ReviewerRepository reviewerRepository;

    @GetMapping("entities")
    public String entities(Model model, Principal principal) {

        User user = userRepository.findByEmail(principal.getName());
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRoles().toString();

        System.out.println();

        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else if (role.contains("ROLE_USER"))
            model.addAttribute("role", "Accountant");
        else
            model.addAttribute("role", "Reviewer");

        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);

        model.addAttribute("firstName", firstName + " " + lastName);
        model.addAttribute("user", user);

        ArrayList<entity> entities = new ArrayList<>();
        for (entity entity : entityRepository.findAll()) {
            Long admin = userRepository.findByEmail(principal.getName()).getId();
            if (entity.getAdmin_id() == admin)
                entities.add(entity);
        }

        if (!user.getApproved())
            return "/pages/userPages/notApproved";
        else if (!user.getEnabled())
            return "/pages/userPages/accountDisabled";

        if(role.contains("REVIEWER")) {
            model.addAttribute("user",user);

            dates reportDates = new dates();
            request request = new request();
            model.addAttribute("reviewer",reviewerRepository.findByEmail(user.getEmail()));
            model.addAttribute("entities", entityRepository.findAll());
            model.addAttribute("request", request);
            model.addAttribute("report",reportDates);
            System.out.println("*****************************");
            System.out.println(user.getRoles().toString());
            return "/pages/reviewerPages/reviewer";
        }

        model.addAttribute("entities", entities);

        Boolean isAdmin = false;
        if(user.getRoles().toString().contains("ADMIN"))
            isAdmin = true;
        model.addAttribute("isAdmin" , isAdmin);

        return "/pages/userPages/entities";
    }

    @PostMapping("{user_id}/reviewer/request")
    private String reviwer(@PathVariable("user_id") Long user_id, Model model, Principal principal,
                           @ModelAttribute("request") request request)  {
        User user = userRepository.findById(user_id).get();
        reviewer reviewer = reviewerRepository.findByEmail(user.getEmail());
        reviewer.setFrom_date(request.getDate_start());
        reviewer.setTo_date(request.getDate_end());
        String entity_id = "";
        for(entity entity: entityRepository.findAll())
            if(entity.getName().equals(request.getEntity_id()))
                entity_id = entity.getEntity_id();
        reviewer.setEntity_id(entity_id);

        reviewerRepository.save(reviewer);

        return "redirect:/entities";
    }




    @GetMapping("/userEdit/{user_id}")
    private String userEdit(@PathVariable("user_id") Long user_id, Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else if (role.contains("ROLE_USER"))
            model.addAttribute("role", "Accountant");
        else
            model.addAttribute("role", "Reviewer");
        //String temp = repository.count()+"";
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("image", imageFile);

        model.addAttribute("firstName", firstName + " " + lastName);

        model.addAttribute("user",user);
        manageAccount ma = new manageAccount();
        model.addAttribute("ma",ma);
        return "pages/manageAccount";
    }



    @PostMapping("/userEdit/{user_id}")
    private String userEditPost(@PathVariable("user_id") Long user_id, Model model, Principal principal,
                                @ModelAttribute("ma") manageAccount ma) throws IOException {
        User user = userRepository.findByEmail(principal.getName());

        if(ma.newP.length() != 0){
            System.out.println(encoder.encode(ma.newP));
                if (ma.newP.equals(ma.cpass)) {
                    user.setPassword(encoder.encode(ma.newP));
            }

        }


        userRepository.save(user);

        return "redirect:/entities";
    }


    @GetMapping("/entity/{entity_id}")
    private String entity(@PathVariable("entity_id") String entity_id, Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }
        model.addAttribute("id", entity_id);

        return "pages/dashboard";
    }

    @GetMapping("/entityEdit/{entity_id}")
    private String updateEntityForm(@PathVariable("entity_id") String entity_id,
                                    Model model, Principal principal) {
        entity entity = entityRepository.findById(entity_id).get();

        User user = userRepository.findByEmail(principal.getName());
        logeduser(entity_id, principal, model);
        if (issmw) {
            issmw = false;
            if (!user.getApproved())
                return "/pages/userPages/notApproved";
            else if (!user.getEnabled())
                return "/pages/userPages/accountDisabled";

            return "pages/userPages/error";
        }

        model.addAttribute("entity_id", entity_id);
        model.addAttribute("entity", entity);
        return "pages/userPages/entityEdit";
    }

    @PostMapping("/editEntity/{entity_id}")
    private String UpdateEntity(@PathVariable("entity_id") String entity_id, Model model, EntityRegistrationDto entityRegistrationDto) {

        entity UpdateEntity = entityRepository.findById(entity_id).get();
        Date date = new Date(System.currentTimeMillis());
        UpdateEntity.setUpdated(date);
        UpdateEntity.setName(entityRegistrationDto.getName());
        UpdateEntity.setAddress_1(entityRegistrationDto.getAddress_1());
        UpdateEntity.setAddress_2(entityRegistrationDto.getAddress_2());
        UpdateEntity.setPhone(entityRegistrationDto.getPhone());
        UpdateEntity.setEmail(entityRegistrationDto.getEmail());
        UpdateEntity.setState(entityRegistrationDto.getState());
        UpdateEntity.setCity(entityRegistrationDto.getCity());
        UpdateEntity.setCountry(entityRegistrationDto.getCountry());
        UpdateEntity.setZip_code(entityRegistrationDto.getZip_code());
        UpdateEntity.setWebsite(entityRegistrationDto.getWebsite());

        entityRepository.save(UpdateEntity);

        return "redirect:/entities";
    }

    @GetMapping("/entityDelete/{entity_id}")
    private String deleteEntity(@PathVariable("entity_id") String entity_id) {
        entity entity = entityRepository.findById(entity_id).get();
        entityRepository.delete(entity);
        for (bill bill : billRepository.findAll()) {
            if (bill.getBill_id().startsWith(entity_id))
                billRepository.delete(bill);
        }
        for (invoice invoice : invoiceRepository.findAll()) {
            if (invoice.getInvoice_id().startsWith(entity_id))
                invoiceRepository.delete(invoice);
        }
        for (ledger ledger : ledgerRepository.findAll()) {
            if (ledger.getLedger_id().startsWith(entity_id))
                ledgerRepository.delete(ledger);
        }
        for (journalEntry journalEntry : journalentryRepository.findAll()) {
            if (journalEntry.getJe_id().startsWith(entity_id))
                journalentryRepository.delete(journalEntry);
        }
        for (transaction transaction : transactionRepository.findAll()) {
            if (transaction.getTransaction_id().startsWith(entity_id))
                transactionRepository.delete(transaction);
        }
        return "redirect:/entities";
    }

    @ModelAttribute("entity")
    public EntityRegistrationDto newEntity() {
        return new EntityRegistrationDto();
    }

    @GetMapping("/addEntity")
    private String addEntity(@ModelAttribute("entity") EntityRegistrationDto
                                     entityRegistrationDto, Model model, BindingResult result, Principal principal) {

        ///****** this part is always logged in user
        User user = userRepository.findByEmail(principal.getName());
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String role = user.getRoles().toString();
        if (role.contains("ADMIN")) {
            model.addAttribute("role", "Manager");
        } else if (role.contains("ROLE_USER"))
            model.addAttribute("role", "Accountant");
        else
            model.addAttribute("role", "Reviewer");
        model.addAttribute("firstName", firstName + " " + lastName);
        byte[] image = userRepository.findByEmail(principal.getName()).getData();
        String imageFile = Base64.getMimeEncoder().encodeToString(image);
        model.addAttribute("user",user);
        model.addAttribute("image", imageFile);
        ///******************

        return "pages/userPages/newEntity";
    }

    @PostMapping("/addEntity")
    private String addEntity(@ModelAttribute("entity") @Valid EntityRegistrationDto
                                     entityRegistrationDto, BindingResult result, Principal principal) {

        entity UpdateEntity = new entity();
        Date date = new Date(System.currentTimeMillis());
        UpdateEntity.setCreated(date);
        UpdateEntity.setName(entityRegistrationDto.getName());
        String id =entityRegistrationDto.getName().concat("-").concat(getAlphaNumericString(8));
        UpdateEntity.setEntity_id(id);
        UpdateEntity.setAddress_1(entityRegistrationDto.getAddress_1());
        UpdateEntity.setAddress_2(entityRegistrationDto.getAddress_2());
        UpdateEntity.setPhone(entityRegistrationDto.getPhone());
        UpdateEntity.setEmail(entityRegistrationDto.getEmail());
        UpdateEntity.setState(entityRegistrationDto.getState());
        UpdateEntity.setCity(entityRegistrationDto.getCity());
        UpdateEntity.setCountry(entityRegistrationDto.getCountry());
        UpdateEntity.setZip_code(entityRegistrationDto.getZip_code());
        UpdateEntity.setWebsite(entityRegistrationDto.getWebsite());
        UpdateEntity.setHidden(false);
        Long admin = userRepository.findByEmail(principal.getName()).getId();
        UpdateEntity.setAdmin_id(admin);
        vendor vendor = new vendor();
        vendor.setVendor_id(id+"-"+"self");
        vendor.setCreated(date);
        vendor.setVendor_name("self");
        vendor.setEntity_id(id);
        vendor.setAddress_1("here");
        vendorRepository.save(vendor);

        customer customer = new customer();
        customer.setCustomer_id(id+"-"+"self");
        customer.setCreated(date);
        customer.setCustomer_name("self");
        customer.setEntity_id(id);
        customer.setAddress_1("here");
        customerRepository.save(customer);

        entityRepository.save(UpdateEntity);
        return "redirect:/entities";
    }

    public static String getAlphaNumericString(int n) {

        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

}

class manageAccount {
    String old;
    String newP;
    String cpass;
    MultipartFile image;

    public manageAccount() {
    }

    public manageAccount(String old, String newP, String cpass, MultipartFile image) {
        this.old = old;
        this.newP = newP;
        this.cpass = cpass;
        this.image = image;
    }

    public String getOld() {
        return old;
    }

    public void setOld(String old) {
        this.old = old;
    }

    public String getNewP() {
        return newP;
    }

    public void setNewP(String newP) {
        this.newP = newP;
    }

    public String getCpass() {
        return cpass;
    }

    public void setCpass(String cpass) {
        this.cpass = cpass;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}

class dates {
    Date date_start;
    Date date_end;

    public dates() {
    }

    public dates(Date date_start, Date date_end) {
        this.date_start = date_start;
        this.date_end = date_end;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }
}

class request {
    Date date_start;
    Date date_end;
    String email;
    String entity_id;
    Boolean approved;

    public request(Boolean approved,Date date_start, Date date_end, String email, String entity_id) {
        this.approved = approved;
        this.date_start = date_start;
        this.date_end = date_end;
        this.email = email;
        this.entity_id = entity_id;
    }

    public request() {
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Date getDate_start() {
        return date_start;
    }

    public void setDate_start(Date date_start) {
        this.date_start = date_start;
    }

    public Date getDate_end() {
        return date_end;
    }

    public void setDate_end(Date date_end) {
        this.date_end = date_end;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }
}

class dashb{
    Double revenue = 0.0;
    Double expense = 0.0;

    Double solvency = 0.0;

    Double grossmargin = 0.0;

    Double [] income = new Double [12];
    Double [] expen = new Double [12];

    public dashb() {
    }

    public dashb(Double revenue, Double expense, Double solvency, Double grossmargin, Double[] income, Double[] expen) {
        this.revenue = revenue;
        this.expense = expense;
        this.solvency = solvency;
        this.grossmargin = grossmargin;
        this.income = income;
        this.expen = expen;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }

    public Double getSolvency() {
        return solvency;
    }

    public void setSolvency(Double solvency) {
        this.solvency = solvency;
    }

    public Double getGrossmargin() {
        return grossmargin;
    }

    public void setGrossmargin(Double grossmargin) {
        this.grossmargin = grossmargin;
    }

    public Double[] getIncome() {
        return income;
    }

    public void setIncome(Double[] income) {
        this.income = income;
    }

    public Double[] getExpen() {
        return expen;
    }

    public void setExpen(Double[] expen) {
        this.expen = expen;
    }
}